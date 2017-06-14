/*
 * Copyright (c) 2017 Comvai, s.r.o. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.ctoolkit.restapi.client.adapter;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;
import org.ctoolkit.restapi.client.ClientErrorException;
import org.ctoolkit.restapi.client.DownloadMediaRequestProvider;
import org.ctoolkit.restapi.client.HttpFailureException;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.NotFoundException;
import org.ctoolkit.restapi.client.PayloadRequest;
import org.ctoolkit.restapi.client.RemoteServerErrorException;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.RequestTimeoutException;
import org.ctoolkit.restapi.client.RestFacade;
import org.ctoolkit.restapi.client.SingleRequest;
import org.ctoolkit.restapi.client.SingleRetrievalRequest;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.ctoolkit.restapi.client.UnderlyingRequest;
import org.ctoolkit.restapi.client.UploadMediaRequestProvider;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.DownloadExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UnderlyingExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.ctoolkit.restapi.client.provider.LocalResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The resource facade implementation as adapter. Executes Java bean mapping
 * and then delegates the execution to one of the binded adaptee.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 * @see MapperFactory
 */
public class RestFacadeAdapter
        implements RestFacade
{
    private static final Logger logger = LoggerFactory.getLogger( RestFacadeAdapter.class );

    private final MapperFacade mapper;

    private final MapperFactory factory;

    private final ResourceProviderInjector injector;

    private final GoogleApiProxyFactory apiFactory;

    @Inject
    RestFacadeAdapter( MapperFacade mapper,
                       MapperFactory factory,
                       ResourceProviderInjector injector,
                       GoogleApiProxyFactory apiFactory )
    {
        this.mapper = mapper;
        this.factory = factory;
        this.injector = injector;
        this.apiFactory = apiFactory;
    }

    /**
     * Returns the mapper instance.
     *
     * @return the mapper instance
     */
    MapperFacade getMapper()
    {
        return mapper;
    }

    /**
     * Executes download. Call to remote endpoint.
     *
     * @param downloader the http downloader to interact with
     * @param adaptee    the download adaptee configured for given resource
     * @param resource   the type of resource to download as a media
     * @param identifier the unique identifier of content to download
     * @param output     the output stream where desired content will be downloaded to.
     * @param type       the content type or {@code null} to expect default
     * @param params     the optional resource params
     * @param locale     the language the client has configured to prefer in results if applicable
     * @return Void
     */
    Void executeDownload( @Nonnull MediaHttpDownloader downloader,
                          @Nonnull DownloadExecutorAdaptee adaptee,
                          @Nonnull Class resource,
                          @Nonnull Identifier identifier,
                          @Nonnull OutputStream output,
                          @Nullable String type,
                          @Nullable Map<String, Object> params,
                          @Nullable Locale locale )
    {
        checkNotNull( downloader );
        checkNotNull( adaptee );
        checkNotNull( resource );
        checkNotNull( identifier );
        checkNotNull( output );

        //noinspection MismatchedQueryAndUpdateOfCollection
        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( params, false );

        URL path = adaptee.prepareDownloadUrl( identifier, type, params, locale );
        if ( path == null )
        {
            String msg = "URL to download a resource content cannot be null. Identifier: ";
            throw new IllegalArgumentException( msg + identifier + " Resource: " + resource.getName() );
        }

        HttpHeaders headers = null;
        if ( locale != null )
        {
            headers = createHttpHeaders();
            String languageTag = new java.util.Locale( locale.getLanguage(), locale.getCountry() ).toLanguageTag();
            headers.put( com.google.common.net.HttpHeaders.ACCEPT_LANGUAGE, languageTag );
        }

        if ( !Strings.isNullOrEmpty( type ) )
        {
            if ( headers == null )
            {
                headers = createHttpHeaders();
            }
            headers.setContentType( type );
        }

        String apiKey = credential.getApiKey();
        if ( !Strings.isNullOrEmpty( apiKey ) )
        {
            if ( headers == null )
            {
                headers = createHttpHeaders();
            }
            headers.setAuthorization( apiKey );
        }

        try
        {
            downloader.download( new GenericUrl( path ), headers, output );
        }
        catch ( IOException e )
        {
            // Update exception handling is being used. We need to return exception if resource is not found.
            throw prepareUpdateException( e, resource, identifier );
        }
        return null;
    }

    @VisibleForTesting
    HttpHeaders createHttpHeaders()
    {
        return new HttpHeaders();
    }

    /**
     * Prepares the configured download request
     *
     * @param resource   the type of resource to download as a media
     * @param identifier the unique identifier of content to download
     * @param output     the output stream where desired content will be downloaded to.
     * @param type       the content type or {@code null} to expect default
     * @return the configured download request
     */
    DownloadRequest prepareDownloadRequest( @Nonnull Class resource,
                                            @Nonnull Identifier identifier,
                                            @Nonnull OutputStream output,
                                            @Nullable String type )
    {
        checkNotNull( resource );
        checkNotNull( identifier );
        checkNotNull( output );

        DownloadExecutorAdaptee adaptee = adaptee( DownloadExecutorAdaptee.class, resource );
        String apiPrefix = adaptee.getApiPrefix();
        MediaHttpDownloader downloader;

        try
        {
            HttpRequestInitializer requestConfig = apiFactory.newRequestConfig( apiPrefix );
            downloader = new MediaHttpDownloader( apiFactory.getHttpTransport(), requestConfig );
        }
        catch ( GeneralSecurityException e )
        {
            logger.error( "Application name: " + apiFactory.getApplicationName( apiPrefix )
                    + " Endpoint URL: " + apiFactory.getEndpointUrl( apiPrefix ), e );
            throw new UnauthorizedException( e.getMessage() );
        }
        catch ( IOException e )
        {
            logger.error( "Application name: " + apiFactory.getApplicationName( apiPrefix )
                    + " Endpoint URL: " + apiFactory.getEndpointUrl( apiPrefix ), e );

            throw new RemoteServerErrorException( HttpStatusCodes.STATUS_CODE_SERVER_ERROR, e.getMessage() );
        }

        return new DownloadRequest( this, adaptee, downloader, resource, identifier, output, type );
    }

    @Override
    public <T> PayloadRequest<T> newInstance( @Nonnull Class<T> resource )
    {
        return newInstance( resource, null, null );
    }

    @Override
    public <T> PayloadRequest<T> newInstance( @Nonnull Class<T> resource,
                                              @Nullable Map<String, Object> parameters,
                                              @Nullable Locale locale )
    {
        checkNotNull( resource );

        NewExecutorAdaptee adaptee = adaptee( NewExecutorAdaptee.class, resource );
        Object remoteRequest;
        try
        {
            remoteRequest = adaptee.prepareNew( resource.getSimpleName(), parameters, locale );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        return new NewInstanceRequest<>( resource, this, adaptee, remoteRequest );
    }

    @Override
    public <T> UploadMediaRequestProvider<T> upload( @Nonnull T resource )
    {
        checkNotNull( resource );
        return new InputStreamMediaRequestProvider<>( this, resource );
    }

    @Override
    public <T> DownloadMediaRequestProvider download( @Nonnull Class<T> resource )
    {
        checkNotNull( resource );
        return new OutputStreamMediaRequestProvider( this, resource );
    }

    <R> R callbackNewInstance( @Nonnull NewExecutorAdaptee adaptee,
                               @Nonnull Object remoteRequest,
                               @Nonnull Class<R> responseType,
                               @Nullable Map<String, Object> parameters,
                               @Nullable Locale locale )
    {
        checkNotNull( adaptee );
        checkNotNull( remoteRequest );
        checkNotNull( responseType );

        if ( parameters == null )
        {
            parameters = new HashMap<>();
        }

        Object remoteInstance;
        try
        {
            remoteInstance = adaptee.executeNew( remoteRequest, parameters, locale );
        }
        catch ( IOException e )
        {
            throw prepareUpdateException( e, responseType, null );
        }

        if ( remoteInstance.getClass() == responseType )
        {
            //noinspection unchecked
            return ( R ) remoteInstance;
        }
        else
        {
            return mapper.map( remoteInstance, responseType );
        }
    }

    @Override
    public <T> SingleRetrievalRequest<T> get( @Nonnull Class<T> resource, @Nonnull Identifier identifier )
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        GetExecutorAdaptee adaptee = adaptee( GetExecutorAdaptee.class, resource );
        Object remoteRequest;
        try
        {
            //noinspection unchecked
            remoteRequest = adaptee.prepareGet( identifier );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        return new GetRequest<>( resource, identifier, this, adaptee, remoteRequest );
    }

    @Override
    public <T> SingleRetrievalRequest<T> get( @Nonnull Class<T> resource, @Nonnull String identifier )
    {
        return get( resource, new Identifier( identifier ) );
    }

    @Override
    public <T> SingleRetrievalRequest<T> get( @Nonnull Class<T> resource, @Nonnull Long identifier )
    {
        return get( resource, new Identifier( identifier ) );
    }

    <R> R callbackExecuteGet( @Nonnull GetExecutorAdaptee adaptee,
                              @Nonnull Object remoteRequest,
                              @Nonnull Class<R> responseType,
                              @Nonnull Object identifier,
                              @Nullable Map<String, Object> parameters,
                              @Nullable Locale locale )
    {
        checkNotNull( responseType );

        if ( parameters == null )
        {
            parameters = new HashMap<>();
        }

        // looking for LocalResourceProvider optional implementation for given resource type
        LocalResourceProvider<R> provider = injector.getExistingResourceProvider( responseType );
        R response = null;

        boolean requestForPersist = false;
        if ( provider != null )
        {
            // retrieve requested local resource
            response = provider.get( identifier, parameters, locale, null );
            requestForPersist = response == null;
        }

        if ( response == null )
        {
            Object remoteObject;
            try
            {
                remoteObject = adaptee.executeGet( remoteRequest, parameters, locale );
            }
            catch ( IOException e )
            {
                RuntimeException exception = prepareRetrievalException( e, responseType, identifier );
                if ( exception == null )
                {
                    return null;
                }
                else
                {
                    throw exception;
                }
            }
            if ( remoteObject.getClass() == responseType )
            {
                //noinspection unchecked
                response = ( R ) remoteObject;
            }
            else
            {
                response = mapper.map( remoteObject, responseType );
            }
        }

        if ( requestForPersist && response != null )
        {
            // provide remote resource instance to be either persisted or cached
            provider.persist( response, identifier, parameters, locale );
        }
        return response;
    }

    @Override
    public <T> ListRequest<T> list( @Nonnull Class<T> resource )
    {
        return list( resource, null );
    }

    @Override
    public <T> ListRequest<T> list( @Nonnull Class<T> resource, @Nullable Identifier parent )
    {
        checkNotNull( resource );

        ListExecutorAdaptee adaptee = adaptee( ListExecutorAdaptee.class, resource );
        Object remoteRequest;
        try
        {
            //noinspection unchecked
            remoteRequest = adaptee.prepareList( parent );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        return new ListRequest<>( resource, this, adaptee, remoteRequest );
    }

    <R> List<R> callbackExecuteList( @Nonnull ListExecutorAdaptee adaptee,
                                     @Nonnull Object remoteRequest,
                                     @Nonnull Class<R> responseType,
                                     @Nullable Map<String, Object> criteria,
                                     @Nullable Locale locale,
                                     int start,
                                     int length )
    {
        checkNotNull( responseType );

        if ( criteria == null )
        {
            criteria = new HashMap<>();
        }

        // looking for LocalResourceProvider optional implementation for given resource type
        LocalResourceProvider<R> provider = injector.getExistingResourceProvider( responseType );
        List<R> response = null;

        boolean requestForPersist = false;
        if ( provider != null )
        {
            // retrieve requested local list of resources
            response = provider.list( criteria, locale, null );
            requestForPersist = response == null;
        }

        if ( response == null )
        {
            List<?> remoteList;
            try
            {
                remoteList = adaptee.executeList( remoteRequest, criteria, locale, start, length );
            }
            catch ( IOException e )
            {
                RuntimeException exception = prepareRetrievalException( e, responseType, null );
                if ( exception == null )
                {
                    remoteList = null;
                }
                else
                {
                    throw exception;
                }
            }
            if ( remoteList == null || remoteList.isEmpty() )
            {
                response = Lists.newArrayList();
            }
            else
            {
                if ( remoteList.get( 0 ).getClass() == responseType )
                {
                    //noinspection unchecked
                    response = ( List<R> ) remoteList;
                }
                else
                {
                    response = mapper.mapAsList( remoteList, responseType );
                }
            }
        }

        if ( requestForPersist && response != null && !response.isEmpty() )
        {
            // provide remote list of resource to be either persisted or cached
            provider.persistList( response, criteria, locale );
        }
        return response;
    }

    @Override
    public <T> PayloadRequest<T> insert( @Nonnull T resource )
    {
        return insert( resource, null );
    }

    @Override
    public <T> PayloadRequest<T> insert( @Nonnull T resource, @Nullable Identifier parentKey )
    {
        return internalInsert( resource, parentKey, null );
    }

    <T> PayloadRequest<T> internalInsert( @Nonnull T resource,
                                          @Nullable Identifier parentKey,
                                          @Nullable MediaProvider provider )
    {
        checkNotNull( resource );

        Class<?> remoteResource = evaluateRemoteResource( resource.getClass() );
        Object source;

        if ( resource.getClass() == remoteResource )
        {
            source = resource;
        }
        else
        {
            source = mapper.map( resource, remoteResource );
        }

        @SuppressWarnings( "unchecked" )
        InsertExecutorAdaptee<Object> adaptee = adaptee( InsertExecutorAdaptee.class, resource.getClass() );
        Object remoteRequest;
        try
        {
            remoteRequest = adaptee.prepareInsert( source, parentKey, provider );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        @SuppressWarnings( "unchecked" )
        Class<T> resourceClass = ( Class<T> ) resource.getClass();
        return new InsertRequest<>( resourceClass, parentKey, this, adaptee, remoteRequest );
    }

    <R> R callbackExecuteInsert( @Nonnull InsertExecutorAdaptee adaptee,
                                 @Nonnull Object remoteRequest,
                                 @Nonnull Class<R> responseType,
                                 @Nullable Object parentKey,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
    {
        checkNotNull( adaptee );
        checkNotNull( remoteRequest );
        checkNotNull( responseType );

        Object source;
        try
        {
            source = adaptee.executeInsert( remoteRequest, parameters, locale );
        }
        catch ( IOException e )
        {
            throw prepareUpdateException( e, responseType, parentKey );
        }

        if ( source == null )
        {
            return null;
        }

        if ( source.getClass() == responseType )
        {
            //noinspection unchecked
            return ( R ) source;
        }
        else
        {
            return mapper.map( source, responseType );
        }
    }

    @Override
    public <T> PayloadRequest<T> update( @Nonnull T resource, @Nonnull Identifier identifier )
    {
        return internalUpdate( resource, identifier, null );
    }

    @Override
    public <T> PayloadRequest<T> update( @Nonnull T resource, @Nonnull String identifier )
    {
        return update( resource, new Identifier( identifier ) );
    }

    @Override
    public <T> PayloadRequest<T> update( @Nonnull T resource, @Nonnull Long identifier )
    {
        return update( resource, new Identifier( identifier ) );
    }

    <T> PayloadRequest<T> internalUpdate( @Nonnull T resource,
                                          @Nonnull Identifier identifier,
                                          @Nullable MediaProvider provider )
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        Class<?> remoteResource = evaluateRemoteResource( resource.getClass() );
        Object source;

        if ( resource.getClass() == remoteResource )
        {
            source = resource;
        }
        else
        {
            source = mapper.map( resource, remoteResource );
        }

        @SuppressWarnings( "unchecked" )
        UpdateExecutorAdaptee<Object> adaptee = adaptee( UpdateExecutorAdaptee.class, resource.getClass() );
        Object remoteRequest;
        try
        {
            remoteRequest = adaptee.prepareUpdate( source, identifier, provider );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        @SuppressWarnings( "unchecked" )
        Class<T> resourceClass = ( Class<T> ) resource.getClass();
        return new UpdateRequest<>( resourceClass, identifier, this, adaptee, remoteRequest );
    }

    <R> R callbackExecuteUpdate( @Nonnull UpdateExecutorAdaptee adaptee,
                                 @Nonnull Object remoteRequest,
                                 @Nonnull Class<R> responseType,
                                 @Nonnull Object identifier,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
    {
        checkNotNull( adaptee );
        checkNotNull( remoteRequest );
        checkNotNull( responseType );
        checkNotNull( identifier );

        Object source;
        try
        {
            source = adaptee.executeUpdate( remoteRequest, parameters, locale );
        }
        catch ( IOException e )
        {
            throw prepareUpdateException( e, responseType, identifier );
        }

        if ( source == null )
        {
            return null;
        }

        if ( source.getClass() == responseType )
        {
            //noinspection unchecked
            return ( R ) source;
        }
        else
        {
            return mapper.map( source, responseType );
        }
    }

    <R, U> R callbackExecuteUnderlying( @Nonnull UnderlyingExecutorAdaptee<U> adaptee,
                                        @Nonnull U remoteRequest,
                                        @Nonnull Class<R> responseType,
                                        @Nullable Map<String, Object> parameters,
                                        @Nullable Locale locale )
    {
        checkNotNull( adaptee );
        checkNotNull( remoteRequest );
        checkNotNull( responseType );

        Object source;
        try
        {
            source = adaptee.executeUnderlying( remoteRequest, parameters, locale );
        }
        catch ( IOException e )
        {
            throw prepareUpdateException( e, responseType, null );
        }

        if ( source == null )
        {
            return null;
        }

        if ( source.getClass() == responseType )
        {
            //noinspection unchecked
            return ( R ) source;
        }
        else
        {
            return mapper.map( source, responseType );
        }
    }

    @Override
    public <U> UnderlyingRequest<U> underlying( @Nonnull Class<U> resource )
    {
        @SuppressWarnings( "unchecked" )
        UnderlyingExecutorAdaptee<U> adaptee = adaptee( UnderlyingExecutorAdaptee.class, resource );

        return new UnderlyingRequestPreparation<>( this, adaptee );
    }

    <U> U callbackPrepareUnderlying( @Nonnull UnderlyingExecutorAdaptee<U> adaptee,
                                     @Nullable Object resource,
                                     @Nullable Identifier identifier,
                                     @Nullable Map<String, Object> parameters )
    {
        U remoteRequest;
        try
        {
            remoteRequest = adaptee.prepareUnderlying( resource, identifier, parameters );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        return remoteRequest;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> SingleRequest<T> delete( @Nonnull Class<T> resource, @Nonnull Identifier identifier )
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        DeleteExecutorAdaptee adaptee = adaptee( DeleteExecutorAdaptee.class, resource );
        Object remoteRequest;
        try
        {
            remoteRequest = adaptee.prepareDelete( identifier );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        return ( SingleRequest ) new DeleteRequest( resource, identifier, this, adaptee, remoteRequest );
    }

    @Override
    public <T> SingleRequest<T> delete( @Nonnull Class<T> resource, @Nonnull String identifier )
    {
        return delete( resource, new Identifier( identifier ) );
    }

    @Override
    public <T> SingleRequest<T> delete( @Nonnull Class<T> resource, @Nonnull Long identifier )
    {
        return delete( resource, new Identifier( identifier ) );
    }

    Void callbackExecuteDelete( @Nonnull DeleteExecutorAdaptee adaptee,
                                @Nonnull Object remoteRequest,
                                @Nonnull Class resource,
                                @Nonnull Object identifier,
                                @Nullable Map<String, Object> parameters,
                                @Nullable Locale locale )
    {
        checkNotNull( identifier );

        try
        {
            adaptee.executeDelete( remoteRequest, parameters, locale );
        }
        catch ( IOException e )
        {
            throw prepareUpdateException( e, resource, identifier );
        }
        return null;
    }

    private RuntimeException prepareRetrievalException( IOException e, Class<?> resource, @Nullable Object identifier )
    {
        return prepareException( e, resource, identifier, false );
    }

    private RuntimeException prepareUpdateException( IOException e, Class<?> resource, @Nullable Object identifier )
    {
        return prepareException( e, resource, identifier, true );
    }

    private RuntimeException prepareException( IOException e,
                                               Class<?> resource,
                                               @Nullable Object identifier,
                                               boolean update )
    {
        int statusCode = -1;
        String statusMessage;

        RuntimeException toBeThrown;

        if ( e instanceof HttpResponseException )
        {
            statusCode = ( ( HttpResponseException ) e ).getStatusCode();
            statusMessage = ( ( HttpResponseException ) e ).getStatusMessage();
        }
        else if ( e instanceof SocketTimeoutException )
        {
            statusCode = 408;
            statusMessage = e.getMessage();
        }
        else if ( e instanceof UnknownHostException )
        {
            statusCode = HttpStatusCodes.STATUS_CODE_SERVICE_UNAVAILABLE;
            statusMessage = "Unknown host: " + e.getMessage();
        }
        else if ( e instanceof SSLHandshakeException )
        {
            statusCode = HttpStatusCodes.STATUS_CODE_UNAUTHORIZED;
            statusMessage = e.getMessage();
        }
        else
        {
            statusMessage = e.getMessage();
        }

        logger.warn( "Resource " + resource.getName() + ", identifier: " + identifier, e );

        if ( 400 == statusCode )
        {
            toBeThrown = new ClientErrorException( statusCode, statusMessage );
        }
        else if ( HttpStatusCodes.STATUS_CODE_UNAUTHORIZED == statusCode )
        {
            toBeThrown = new UnauthorizedException( statusMessage );
        }
        else if ( HttpStatusCodes.STATUS_CODE_FORBIDDEN == statusCode )
        {
            toBeThrown = new ClientErrorException( statusCode, statusMessage );
        }
        else if ( HttpStatusCodes.STATUS_CODE_NOT_FOUND == statusCode && update )
        {
            toBeThrown = new NotFoundException( statusMessage );
        }
        else if ( HttpStatusCodes.STATUS_CODE_NOT_FOUND == statusCode )
        {
            toBeThrown = new NotFoundException( statusMessage );
        }
        else if ( 408 == statusCode )
        {
            toBeThrown = new RequestTimeoutException( statusMessage );
        }
        else if ( 409 == statusCode )
        {
            toBeThrown = new ClientErrorException( statusCode, statusMessage );
        }
        else if ( 400 < statusCode && statusCode < 499 )
        {
            toBeThrown = new ClientErrorException( statusCode, statusMessage );
        }
        else if ( HttpStatusCodes.STATUS_CODE_SERVER_ERROR == statusCode )
        {
            toBeThrown = new RemoteServerErrorException( statusCode, statusMessage );
        }
        else if ( HttpStatusCodes.STATUS_CODE_SERVICE_UNAVAILABLE == statusCode )
        {
            toBeThrown = new RemoteServerErrorException( statusCode, statusMessage );
        }
        else if ( statusCode > -1 )
        {
            toBeThrown = new HttpFailureException( statusCode, statusMessage );
        }
        else
        {
            toBeThrown = new RuntimeException( statusMessage );
        }

        return toBeThrown;
    }

    Class<?> evaluateRemoteResource( Class resource )
    {
        Set<Type<?>> types = factory.lookupMappedClasses( TypeFactory.valueOf( resource ) );
        Iterator<Type<?>> iterator = types.iterator();

        Class<?> remoteResource;

        if ( iterator.hasNext() )
        {
            remoteResource = iterator.next().getRawType();
        }
        else
        {
            // there is no mapping, use directly the given resource type
            remoteResource = resource;
        }

        return remoteResource;
    }

    private <A> A adaptee( Class<A> adapteeType, Class<?> resource )
    {
        Class<?> remoteResource = evaluateRemoteResource( resource );
        A adaptee = injector.getExecutorAdaptee( adapteeType, remoteResource );

        if ( adaptee == null && remoteResource == resource )
        {
            String msg = "Missing binding between adaptee and resource: " + adapteeType.getSimpleName() + "<"
                    + resource.getName() + ">";
            throw new NotFoundException( msg );
        }

        if ( adaptee == null )
        {
            String msg = "Missing binding between adaptee and remote resource: " + adapteeType.getSimpleName() + "<"
                    + remoteResource.getName() + ">. The remote resource " + remoteResource.getName()
                    + " is being mapped to " + resource.getName() + ".";

            throw new NotFoundException( msg );
        }

        return adaptee;
    }
}
