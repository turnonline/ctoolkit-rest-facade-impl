/*
 * Copyright (c) 2015 Comvai, s.r.o. All Rights Reserved.
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

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.common.collect.Lists;
import ma.glasnost.orika.MapperFacade;
import org.ctoolkit.restapi.client.ClientErrorException;
import org.ctoolkit.restapi.client.HttpFailureException;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.NotFoundException;
import org.ctoolkit.restapi.client.Patch;
import org.ctoolkit.restapi.client.RemoteServerErrorException;
import org.ctoolkit.restapi.client.ResourceFacade;
import org.ctoolkit.restapi.client.SingleRequest;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.ctoolkit.restapi.client.adaptee.RestExecutorAdaptee;
import org.ctoolkit.restapi.client.provider.LocalResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The resource facade implementation as adapter. Executes Java bean mapping
 * and then delegates the execution to one of the binded adaptee.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 * @see ResourceBinder
 */
public class ResourceFacadeAdapter
        implements ResourceFacade
{
    private static final Logger logger = LoggerFactory.getLogger( ResourceFacadeAdapter.class );

    private final MapperFacade mapper;

    private final ResourceBinder binder;

    private final ResourceProviderInjector injector;

    @Inject
    public ResourceFacadeAdapter( MapperFacade mapper, ResourceBinder binder, ResourceProviderInjector injector )
    {
        this.mapper = mapper;
        this.binder = binder;
        this.injector = injector;
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    private static <T> T checkNotNull( T reference )
    {
        if ( reference == null )
        {
            throw new NullPointerException();
        }
        return reference;
    }

    @Override
    public <T> SingleRequest<T> newInstance( @Nonnull Class<T> resource )
    {
        return newInstance( resource, null, null );
    }

    @Override
    public <T> SingleRequest<T> newInstance( @Nonnull Class<T> resource,
                                             @Nullable Map<String, Object> parameters,
                                             @Nullable Locale locale )
    {
        checkNotNull( resource );

        RestExecutorAdaptee<Object, Object, Object> adaptee = adaptee( resource );
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

    <R> R callbackNewInstance( @Nonnull RestExecutorAdaptee<Object, Object, Object> adaptee,
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

        return mapper.map( remoteInstance, responseType );
    }

    @Override
    public <T> SingleRequest<T> get( @Nonnull Class<T> resource, @Nonnull Identifier identifier )
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        RestExecutorAdaptee<Object, Object, Object> adaptee = adaptee( resource );
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

    <R> R callbackExecuteGet( @Nonnull RestExecutorAdaptee<Object, Object, Object> adaptee,
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
            response = mapper.map( remoteObject, responseType );
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

        RestExecutorAdaptee<Object, Object, Object> adaptee = adaptee( resource );
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

    <R> List<R> callbackExecuteList( @Nonnull RestExecutorAdaptee<Object, Object, Object> adaptee,
                                     @Nonnull Object remoteRequest,
                                     @Nonnull Class<R> responseType,
                                     @Nullable Map<String, Object> criteria,
                                     @Nullable Locale locale )
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
                remoteList = adaptee.executeList( remoteRequest, criteria, locale );
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
            if ( remoteList == null )
            {
                response = Lists.newArrayList();
            }
            else
            {
                response = mapper.mapAsList( remoteList, responseType );
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
    public <T> SingleRequest<T> insert( @Nonnull T resource )
    {
        return insert( resource, null );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> SingleRequest<T> insert( @Nonnull T resource, @Nullable Identifier parentKey )
    {
        checkNotNull( resource );

        Object source = mapper.map( resource, getSourceClassFor( resource.getClass() ) );

        RestExecutorAdaptee<Object, Object, Object> adaptee = adaptee( resource.getClass() );
        Object remoteRequest;
        try
        {
            remoteRequest = adaptee.prepareInsert( source, parentKey );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        return ( InsertRequest<T> ) new InsertRequest<>( resource.getClass(), parentKey, this, adaptee, remoteRequest );
    }

    <R> R callbackExecuteInsert( @Nonnull RestExecutorAdaptee<Object, Object, Object> adaptee,
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

        return mapper.map( source, responseType );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> SingleRequest<T> update( @Nonnull T resource, @Nonnull Identifier identifier )
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        Object source = mapper.map( resource, getSourceClassFor( resource.getClass() ) );

        RestExecutorAdaptee<Object, Object, Object> adaptee = adaptee( resource.getClass() );
        Object remoteRequest;
        try
        {
            remoteRequest = adaptee.prepareUpdate( source, identifier );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        return ( UpdateRequest<T> ) new UpdateRequest<>( resource.getClass(), identifier, this, adaptee, remoteRequest );
    }

    <R> R callbackExecuteUpdate( @Nonnull RestExecutorAdaptee<Object, Object, Object> adaptee,
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

        return mapper.map( source, responseType );
    }

    @Override
    public <T> SingleRequest<T> patch( @Nonnull Patch<T> resource, @Nonnull Identifier identifier )
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        Class<? extends Patch> resourceClass = resource.getClass();
        Object source = mapper.map( resource, getSourceClassFor( resourceClass ) );

        String alias = resourceClass.getSimpleName();
        Class<T> responseType = resource.type();
        RestExecutorAdaptee<Object, Object, Object> adaptee = adaptee( resourceClass );
        Object remoteRequest;
        try
        {
            //noinspection unchecked
            remoteRequest = adaptee.preparePatch( source, identifier, alias );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        return new PatchRequest<>( responseType, identifier, this, adaptee, remoteRequest );
    }

    <R> R callbackExecutePatch( @Nonnull RestExecutorAdaptee<Object, Object, Object> adaptee,
                                @Nonnull Object remoteRequest,
                                @Nonnull Class<R> responseType,
                                @Nonnull Object identifier,
                                @Nullable Map<String, Object> parameters,
                                @Nullable Locale locale )
    {
        checkNotNull( responseType );
        checkNotNull( identifier );

        Object source;
        try
        {
            source = adaptee.executePatch( remoteRequest, parameters, locale );
        }
        catch ( IOException e )
        {
            throw prepareUpdateException( e, responseType, identifier );
        }

        return mapper.map( source, responseType );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> SingleRequest<T> delete( @Nonnull Class<T> resource, @Nonnull Identifier identifier )
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        RestExecutorAdaptee<Object, Object, Object> adaptee = adaptee( resource );
        Object remoteRequest;
        try
        {
            remoteRequest = adaptee.prepareDelete( identifier );
        }
        catch ( IOException e )
        {
            throw new ClientErrorException( 400, e.getMessage() );
        }

        return ( SingleRequest<T> ) new DeleteRequest( resource, identifier, this, adaptee, remoteRequest );
    }

    Void callbackExecuteDelete( @Nonnull RestExecutorAdaptee<Object, Object, Object> adaptee,
                                @Nonnull Object remoteRequest,
                                @Nonnull Class resource,
                                @Nonnull Object identifier,
                                @Nullable Locale locale )
    {
        checkNotNull( identifier );

        try
        {
            adaptee.executeDelete( remoteRequest, locale );
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
        String statusMessage = null;

        RuntimeException toBeThrown;

        if ( e instanceof HttpResponseException )
        {
            statusCode = ( ( HttpResponseException ) e ).getStatusCode();
            statusMessage = ( ( HttpResponseException ) e ).getStatusMessage();
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

    private Class<?> getSourceClassFor( Class<?> clazz )
    {
        Class<?> destinationClass = binder.getSourceClassFor( clazz );
        if ( destinationClass == null )
        {
            throw new IllegalArgumentException( "Missing mapped destination class for " + clazz );
        }

        return destinationClass;
    }

    private RestExecutorAdaptee<Object, Object, Object> adaptee( Class<?> clazz )
    {
        RestExecutorAdaptee<Object, Object, Object> adaptee = binder.adaptee( clazz );
        if ( adaptee == null )
        {
            throw new IllegalArgumentException( "Missing mapped adaptee for " + clazz );
        }

        return adaptee;
    }
}
