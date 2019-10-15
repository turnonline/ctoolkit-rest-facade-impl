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
import com.google.common.collect.Lists;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;
import org.ctoolkit.restapi.client.ClientErrorException;
import org.ctoolkit.restapi.client.DeleteIdentification;
import org.ctoolkit.restapi.client.DownloadMediaProvider;
import org.ctoolkit.restapi.client.DownloadRequest;
import org.ctoolkit.restapi.client.ForbiddenException;
import org.ctoolkit.restapi.client.HttpFailureException;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.NotFoundException;
import org.ctoolkit.restapi.client.PayloadRequest;
import org.ctoolkit.restapi.client.RemoteServerErrorException;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.RestFacade;
import org.ctoolkit.restapi.client.RetrievalRequest;
import org.ctoolkit.restapi.client.ServiceUnavailableException;
import org.ctoolkit.restapi.client.SingleRetrievalIdentification;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.ctoolkit.restapi.client.UpdateIdentification;
import org.ctoolkit.restapi.client.UploadMediaProvider;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.DownloadExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UnderlyingClientAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.provider.LocalListResourceProvider;
import org.ctoolkit.restapi.client.provider.LocalResourceProvider;
import org.ctoolkit.restapi.client.provider.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

/**
 * The Fluent facade API implementation as an adapter. Executes Java bean mapping
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

    private final Injector injector;

    private final GoogleApiProxyFactory apiFactory;

    private final Map<String, ClientApi> apis;

    private Substitute substitute;

    @Inject
    RestFacadeAdapter( MapperFacade mapper,
                       MapperFactory factory,
                       Injector injector,
                       GoogleApiProxyFactory apiFactory,
                       Map<String, ClientApi> apis )
    {
        this.mapper = mapper;
        this.factory = factory;
        this.injector = injector;
        this.apiFactory = apiFactory;
        this.apis = apis;
    }

    @com.google.inject.Inject( optional = true )
    public void setSubstitute( Substitute substitute )
    {
        this.substitute = substitute;
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
     * @param downloader  the http downloader to interact with
     * @param adaptee     the download adaptee configured for given resource
     * @param resource    the type of resource to download as a media
     * @param identifier  the unique identifier of content to download
     * @param output      the output stream where desired content will be downloaded to.
     * @param interceptor the response interceptor
     * @param headers     the HTTP request headers
     * @param params      the optional resource params
     * @param locale      the language the client has configured to prefer in results if applicable
     * @return the download's response headers.
     */
    Map<String, Object> executeDownload( @Nonnull MediaHttpDownloader downloader,
                                         @Nonnull DownloadExecutorAdaptee adaptee,
                                         @Nonnull Class resource,
                                         @Nonnull Identifier identifier,
                                         @Nonnull OutputStream output,
                                         @Nonnull DownloadResponseInterceptor interceptor,
                                         @Nullable HttpHeaders headers,
                                         @Nullable Map<String, Object> params,
                                         @Nullable Locale locale )
    {
        //noinspection MismatchedQueryAndUpdateOfCollection
        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( params, false );
        String type = headers == null ? null : headers.getContentType();

        URL path = requireNonNull( adaptee ).prepareDownloadUrl( identifier.root(), type, params, locale );
        if ( path == null )
        {
            String msg = "URL to download a resource content cannot be null. Identifier: ";
            throw new IllegalArgumentException( msg + identifier + " Resource: " + resource.getName() );
        }

        try
        {
            boolean remote = substitute == null;
            if ( !remote )
            {
                try
                {
                    substitute.download( checkNotNull( resource ),
                            identifier,
                            checkNotNull( output ),
                            headers,
                            params,
                            locale );
                }
                catch ( Substitute.ProceedWithRemoteCall e )
                {
                    remote = true;
                }
            }

            if ( remote )
            {
                requireNonNull( downloader ).download( new GenericUrl( path ), headers, output );
            }
        }
        catch ( IOException e )
        {
            // Update exception handling is being used. We need to return exception if resource is not found.
            throw prepareUpdateException( e, resource, identifier );
        }
        return interceptor.getHeaders();
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
        DownloadResponseInterceptor interceptor = new DownloadResponseInterceptor();
        DownloadExecutorAdaptee adaptee = adaptee( DownloadExecutorAdaptee.class, checkNotNull( resource ) );
        String apiPrefix = adaptee.getApiPrefix();
        MediaHttpDownloader downloader;

        try
        {
            HttpRequestInitializer requestConfig = apiFactory.newRequestConfig( apiPrefix, interceptor );
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

            throw new RemoteServerErrorException( e.getMessage() );
        }

        Identifier root = checkNotNull( identifier ).root();
        OutputStream os = checkNotNull( output );
        return new DownloadRequestImpl( this, adaptee, downloader, resource, root, os, interceptor, type );
    }

    @Override
    public <T> PayloadRequest<T> newInstance( @Nonnull Class<T> resource )
    {
        NewExecutorAdaptee adaptee = adaptee( NewExecutorAdaptee.class, checkNotNull( resource ) );
        Object remoteRequest;
        try
        {
            remoteRequest = adaptee.prepareNew( resource.getSimpleName() );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( e.getMessage() );
        }

        return new NewInstanceRequest<>( resource, this, adaptee, remoteRequest );
    }

    @Override
    public <T> UploadMediaProvider<T> upload( @Nonnull T resource )
    {
        return new InputStreamUploadMediaRequestProvider<>( this, checkNotNull( resource ) );
    }

    @Override
    public <T> DownloadMediaProvider download( @Nonnull Class<T> resource )
    {
        return new OutputStreamDownloadMediaRequestProvider( this, checkNotNull( resource ) );
    }

    <R> R callbackNewInstance( @Nonnull NewExecutorAdaptee adaptee,
                               @Nonnull Object remoteRequest,
                               @Nonnull Class<R> responseType,
                               @Nullable Map<String, Object> parameters,
                               @Nullable Locale locale )
    {
        if ( parameters == null )
        {
            parameters = new HashMap<>();
        }

        Object remoteInstance = null;
        try
        {
            boolean remote = substitute == null;
            if ( !remote )
            {
                try
                {
                    remoteInstance = substitute.newInstance( checkNotNull( remoteRequest ),
                            checkNotNull( responseType ),
                            parameters,
                            locale );
                }
                catch ( Substitute.ProceedWithRemoteCall e )
                {
                    remote = true;
                }
            }

            if ( remote )
            {
                remoteInstance = adaptee.executeNew( remoteRequest, parameters, locale );
            }
        }
        catch ( IOException e )
        {
            throw prepareUpdateException( e, responseType, null );
        }

        checkNotNull( remoteInstance, "Callback must not return null" );
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
    public <T> SingleRetrievalIdentification<T> get( @Nonnull Class<T> resource )
    {
        return new SingleRetrievalIdentificationImpl<>( this, resource );
    }

    <T> RetrievalRequest<T> internalGet( @Nonnull Class<T> resource, @Nonnull Identifier identifier )
    {
        GetExecutorAdaptee adaptee = adaptee( GetExecutorAdaptee.class, checkNotNull( resource ) );
        Object remoteRequest;
        try
        {
            String errorMessage = "Identifier for GET operation cannot be null.";
            remoteRequest = adaptee.prepareGet( checkNotNull( identifier, errorMessage ) );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( e.getMessage() );
        }

        return new GetRequest<>( resource, identifier.root(), this, adaptee, remoteRequest );
    }

    <R> R callbackExecuteGet( @Nonnull GetExecutorAdaptee adaptee,
                              @Nonnull Object remoteRequest,
                              @Nonnull Class<R> responseType,
                              @Nonnull Identifier identifier,
                              @Nullable Map<String, Object> parameters,
                              @Nullable Locale locale )
    {
        if ( parameters == null )
        {
            parameters = new HashMap<>();
        }

        // looking for LocalResourceProvider optional implementation for given resource type
        LocalResourceProvider<R> provider = getExistingResourceProvider( checkNotNull( responseType ) );
        R response = null;

        boolean requestForPersist = false;
        if ( provider != null )
        {
            // retrieve requested local resource
            response = provider.get( identifier.root(), parameters, locale );
            requestForPersist = response == null;
        }

        if ( response == null )
        {
            Object remoteObject = null;
            try
            {
                boolean remote = substitute == null;
                if ( !remote )
                {
                    try
                    {
                        remoteObject = substitute.get( remoteRequest, responseType, identifier, parameters, locale );
                    }
                    catch ( Substitute.ProceedWithRemoteCall e )
                    {
                        remote = true;
                    }
                }

                if ( remote )
                {
                    remoteObject = adaptee.executeGet( remoteRequest, parameters, locale );
                }
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

            checkNotNull( remoteObject, "Callback must not return null" );
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
            // TODO resource provider lastFor not implemented yet
            // provide remote resource instance to be either persisted or cached
            provider.persist( response, identifier.root(), parameters, locale, null );
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
        ListExecutorAdaptee adaptee = adaptee( ListExecutorAdaptee.class, checkNotNull( resource ) );
        Object remoteRequest;
        try
        {
            remoteRequest = adaptee.prepareList( parent == null ? null : parent.root() );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( e.getMessage() );
        }

        return new ListRequest<>( resource, this, adaptee, remoteRequest );
    }

    <R> List<R> callbackExecuteList( @Nonnull ListExecutorAdaptee adaptee,
                                     @Nonnull Object remoteRequest,
                                     @Nonnull Class<R> responseType,
                                     @Nullable Map<String, Object> criteria,
                                     @Nullable Locale locale,
                                     int start,
                                     int length,
                                     @Nullable String orderBy,
                                     @Nullable Boolean ascending )
    {
        if ( criteria == null )
        {
            criteria = new HashMap<>();
        }

        // looking for LocalResourceProvider optional implementation for given resource type
        LocalListResourceProvider<R> provider = getExistingListResourceProvider( checkNotNull( responseType ) );
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
            List<R> remoteList = null;
            try
            {
                boolean remote = substitute == null;
                if ( !remote )
                {
                    try
                    {
                        remoteList = substitute.list( remoteRequest, responseType, criteria,
                                locale, start, length, orderBy, ascending );
                    }
                    catch ( Substitute.ProceedWithRemoteCall e )
                    {
                        remote = true;
                    }
                }

                if ( remote )
                {
                    //noinspection unchecked
                    remoteList = adaptee.executeList( remoteRequest, criteria, locale, start, length, orderBy, ascending );
                }
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
                    response = remoteList;
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
            provider.persistList( response, criteria, locale, null );
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
        Class<?> remoteResource = evaluateRemoteResource( checkNotNull( resource ).getClass() );
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
            remoteRequest = adaptee.prepareInsert( source, parentKey == null ? null : parentKey.root(), provider );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( e.getMessage() );
        }

        @SuppressWarnings( "unchecked" )
        Class<T> resourceClass = ( Class<T> ) resource.getClass();
        return new InsertRequest<>( resourceClass, parentKey, this, adaptee, remoteRequest );
    }

    <R> R callbackExecuteInsert( @Nonnull InsertExecutorAdaptee adaptee,
                                 @Nonnull Object remoteRequest,
                                 @Nonnull Class<R> responseType,
                                 @Nullable Identifier parentKey,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
    {
        Object source = null;
        try
        {
            boolean remote = substitute == null;
            if ( !remote )
            {
                try
                {
                    source = substitute.insert( checkNotNull( remoteRequest ),
                            checkNotNull( responseType ),
                            parentKey,
                            parameters,
                            locale );
                }
                catch ( Substitute.ProceedWithRemoteCall e )
                {
                    remote = true;
                }
            }

            if ( remote )
            {
                source = adaptee.executeInsert( remoteRequest, parameters, locale );
            }
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
    public <T> UpdateIdentification<T> update( @Nonnull T resource )
    {
        return new UpdateIdentificationImpl<>( this, resource );
    }

    <T> PayloadRequest<T> internalUpdate( @Nonnull T resource,
                                          @Nonnull Identifier identifier,
                                          @Nullable MediaProvider provider )
    {
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
            String errorMessage = "Identifier for UPDATE operation cannot be null.";
            remoteRequest = adaptee.prepareUpdate( source, checkNotNull( identifier, errorMessage ).root(), provider );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( e.getMessage() );
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
        Object source = null;
        try
        {
            boolean remote = substitute == null;
            if ( !remote )
            {
                try
                {
                    source = substitute.update( checkNotNull( remoteRequest ), checkNotNull( responseType ), checkNotNull( identifier ), parameters, locale );
                }
                catch ( Substitute.ProceedWithRemoteCall e )
                {
                    remote = true;
                }
            }

            if ( remote )
            {
                source = adaptee.executeUpdate( remoteRequest, parameters, locale );
            }
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

    @Override
    public <T> DeleteIdentification<T> delete( @Nonnull Class<T> resource )
    {
        return new DeleteIdentificationImpl<>( this, resource );
    }

    @Override
    public <C> C client( @Nonnull Class<C> type )
    {
        @SuppressWarnings( "unchecked" )
        UnderlyingClientAdaptee<C> adaptee = adaptee( UnderlyingClientAdaptee.class, type );
        return adaptee.getUnderlyingClient();
    }

    @Override
    public void impersonate( @Nonnull String userEmail, @Nonnull String api )
    {
        ClientApi provider = apis.get( api );
        if ( provider == null )
        {
            throw new IllegalArgumentException( impersonateClientApiMissingErrorMessage( api ) );
        }

        String emailError = "User email cannot be null";
        provider.init( null, checkNotNull( userEmail, emailError ) );
    }

    @Override
    public void impersonate( @Nonnull Collection<String> scopes, @Nonnull String userEmail, @Nonnull String api )
    {
        ClientApi provider = apis.get( api );
        if ( provider == null )
        {
            throw new IllegalArgumentException( impersonateClientApiMissingErrorMessage( api ) );
        }

        String scopesError = "Scopes cannot be null";
        String emailError = "User email cannot be null";
        provider.init( checkNotNull( scopes, scopesError ), checkNotNull( userEmail, emailError ) );
    }

    private String impersonateClientApiMissingErrorMessage( String api )
    {
        return "No API client found for '"
                + api
                + "'. Make sure API is installed in module, for example: install( new GoogleApiDriveModule() );";
    }

    @SuppressWarnings( "unchecked" )
    <T> PayloadRequest<T> internalDelete( @Nonnull Class<T> resource, @Nonnull Identifier identifier )
    {
        DeleteExecutorAdaptee adaptee = adaptee( DeleteExecutorAdaptee.class, checkNotNull( resource ) );
        Object remoteRequest;
        try
        {
            String errorMessage = "Identifier for DELETE operation cannot be null.";
            remoteRequest = adaptee.prepareDelete( checkNotNull( identifier, errorMessage ).root() );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( e.getMessage() );
        }

        // by default response type is not being provided (resulting in null), client can configure if expected
        return new DeleteRequest( identifier, this, adaptee, remoteRequest );
    }

    <R> R callbackExecuteDelete( @Nonnull DeleteExecutorAdaptee adaptee,
                                 @Nonnull Object remoteRequest,
                                 @Nonnull Object identifier,
                                 @Nullable Class<R> responseType,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
    {
        Object response = null;
        try
        {
            boolean remote = substitute == null;
            if ( !remote )
            {
                try
                {
                    response = substitute.delete( checkNotNull( remoteRequest ),
                            checkNotNull( identifier ),
                            responseType,
                            parameters,
                            locale );
                }
                catch ( Substitute.ProceedWithRemoteCall e )
                {
                    remote = true;
                }
            }

            if ( remote )
            {
                response = adaptee.executeDelete( remoteRequest, parameters, locale );
            }
        }
        catch ( IOException e )
        {
            throw prepareUpdateException( e, responseType, identifier );
        }

        if ( responseType == null || response == null )
        {
            return null;
        }

        if ( response.getClass() == responseType )
        {
            //noinspection unchecked
            return ( R ) response;
        }
        else
        {
            return mapper.map( response, responseType );
        }
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
                                               @Nullable Class<?> resource,
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

        logger.warn( "Response resource " + ( resource == null ? " none" : resource.getName() )
                + ", identifier: " + identifier, e );

        if ( 400 == statusCode )
        {
            toBeThrown = new ClientErrorException( statusMessage );
        }
        else if ( HttpStatusCodes.STATUS_CODE_UNAUTHORIZED == statusCode )
        {
            toBeThrown = new UnauthorizedException( statusMessage );
        }
        else if ( HttpStatusCodes.STATUS_CODE_FORBIDDEN == statusCode )
        {
            toBeThrown = new ForbiddenException( statusMessage );
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
            toBeThrown = new ServiceUnavailableException( statusMessage );
        }
        else if ( 400 < statusCode && statusCode < 499 )
        {
            toBeThrown = new HttpFailureException( statusCode, statusMessage );
        }
        else if ( HttpStatusCodes.STATUS_CODE_SERVER_ERROR == statusCode )
        {
            toBeThrown = new RemoteServerErrorException( statusMessage );
        }
        else if ( HttpStatusCodes.STATUS_CODE_SERVICE_UNAVAILABLE == statusCode )
        {
            toBeThrown = new ServiceUnavailableException( statusMessage );
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

    private Class<?> evaluateRemoteResource( Class resource )
    {
        @SuppressWarnings( "unchecked" )
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
        A adaptee = getExecutorAdaptee( adapteeType, remoteResource );

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

    /**
     * Returns the binding if it already exists, or null if does not exist.
     *
     * @param resource the type of resource to get
     * @param <T>      the concrete type of the resource
     * @return the resource provider
     */
    <T> LocalResourceProvider<T> getExistingResourceProvider( @Nonnull Class<T> resource )
    {
        LocalResourceProvider<T> provider = null;

        ParameterizedType pt = Types.newParameterizedType( LocalResourceProvider.class, resource );
        Binding<?> binding = injector.getExistingBinding( Key.get( TypeLiteral.get( pt ) ) );

        if ( binding != null )
        {
            //noinspection unchecked
            provider = ( LocalResourceProvider<T> ) binding.getProvider().get();
        }

        return provider;
    }

    /**
     * Returns the binding if it already exists, or null if does not exist.
     *
     * @param resource the type of resource to get
     * @param <T>      the concrete type of the resource
     * @return the resource provider
     */
    <T> LocalListResourceProvider<T> getExistingListResourceProvider( @Nonnull Class<T> resource )
    {
        LocalListResourceProvider<T> provider = null;

        ParameterizedType pt = Types.newParameterizedType( LocalListResourceProvider.class, resource );
        Binding<?> binding = injector.getExistingBinding( Key.get( TypeLiteral.get( pt ) ) );

        if ( binding != null )
        {
            //noinspection unchecked
            provider = ( LocalListResourceProvider<T> ) binding.getProvider().get();
        }

        return provider;
    }

    /**
     * Returns the binding if it already exists, or null if does not exist.
     *
     * @param adapteeType the adaptee type to get
     * @param resource    the generic type of adaptee to get
     * @param <A>         the type of the adaptee
     * @return the adaptee implementation for given arguments if any
     */
    <A> A getExecutorAdaptee( @Nonnull Class<A> adapteeType, @Nonnull Class<?> resource )
    {
        A adaptee = null;

        ParameterizedType pt = Types.newParameterizedType( adapteeType, resource );
        Binding<?> binding = injector.getExistingBinding( Key.get( TypeLiteral.get( pt ) ) );

        if ( binding != null )
        {
            //noinspection unchecked
            adaptee = ( A ) binding.getProvider().get();
        }
        return adaptee;
    }

    TokenProvider<Object> getTokenProvider( @Nonnull Class type )
    {
        TokenProvider<Object> provider;
        ParameterizedType pt = Types.newParameterizedType( TokenProvider.class, type );
        Binding<?> binding = injector.getExistingBinding( Key.get( TypeLiteral.get( pt ) ) );

        if ( binding != null )
        {
            //noinspection unchecked
            provider = ( TokenProvider ) binding.getProvider().get();
        }
        else
        {
            provider = null;
        }
        return provider;
    }
}
