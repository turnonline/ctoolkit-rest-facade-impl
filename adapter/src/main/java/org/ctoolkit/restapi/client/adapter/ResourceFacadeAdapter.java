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
import org.ctoolkit.restapi.client.LocalResourceProvider;
import org.ctoolkit.restapi.client.Patch;
import org.ctoolkit.restapi.client.ResourceFacade;
import org.ctoolkit.restapi.client.RestExecutorAdaptee;
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
    public <T> T newInstance( @Nonnull Class<T> resource )
    {
        return newInstance( resource, null, null );
    }

    @Override
    public <T> T newInstance( @Nonnull Class<T> resource, @Nullable Locale locale )
    {
        return newInstance( resource, null, locale );
    }

    @Override
    public <T> T newInstance( @Nonnull Class<T> resource, @Nullable Map<String, Object> parameters )
    {
        return newInstance( resource, parameters, null );
    }

    @Override
    public <T> T newInstance( @Nonnull Class<T> resource,
                              @Nullable Map<String, Object> parameters,
                              @Nullable Locale locale )
    {
        checkNotNull( resource );

        Object remoteInstance;
        try
        {
            remoteInstance = adaptee( resource ).executeNew( locale, resource.getSimpleName(), parameters );
        }
        catch ( IOException e )
        {
            logger.warn( "Resource " + resource.getName(), e );
            throw new RuntimeException( e );
        }

        // null means no specific call processed to create default instance
        if ( remoteInstance == null )
        {
            try
            {
                return resource.newInstance();
            }
            catch ( InstantiationException | IllegalAccessException e )
            {
                throw new IllegalArgumentException( e );
            }
        }

        return mapper.map( remoteInstance, resource );
    }

    @Override
    public <T> T get( @Nonnull Class<T> resource, @Nonnull Object identifier )
    {
        checkNotNull( identifier );

        return get( resource, identifier, new HashMap<String, Object>(), null );
    }

    @Override
    public <T> T get( @Nonnull Class<T> resource, @Nonnull Object identifier, @Nullable Locale locale )
    {
        checkNotNull( identifier );

        return get( resource, identifier, new HashMap<String, Object>(), locale );
    }

    @Override
    public <T> T get( @Nonnull Class<T> resource, @Nonnull Map<String, Object> parameters )
    {
        return get( resource, null, parameters, null );
    }

    @Override
    public <T> T get( @Nonnull Class<T> resource, @Nonnull Map<String, Object> parameters, @Nullable Locale locale )
    {
        return get( resource, null, parameters, locale );
    }

    @Override
    public <T> T get( @Nonnull Class<T> resource, @Nonnull Object identifier, @Nonnull Map<String, Object> parameters )
    {
        checkNotNull( identifier );
        return get( resource, identifier, parameters, null );
    }

    @Override
    public <T> T get( @Nonnull Class<T> resource,
                      @Nullable Object identifier,
                      @Nullable Map<String, Object> parameters,
                      @Nullable Locale locale )
    {
        checkNotNull( resource );
        checkNotNull( parameters );

        // looking for LocalResourceProvider optional implementation for given resource type
        LocalResourceProvider<T> provider = injector.getExistingResourceProvider( resource );
        T response = null;

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
                remoteObject = adaptee( resource ).executeGet( identifier, parameters, locale );
            }
            catch ( IOException e )
            {
                if ( e instanceof HttpResponseException )
                {
                    int statusCode = ( ( HttpResponseException ) e ).getStatusCode();
                    if ( HttpStatusCodes.STATUS_CODE_NOT_FOUND == statusCode )
                    {
                        return null;
                    }
                }

                logger.warn( "Resource " + resource.getName() + ", identifier: " + identifier, e );
                throw new RuntimeException( e );
            }
            response = mapper.map( remoteObject, resource );
        }

        if ( requestForPersist && response != null )
        {
            // provide remote resource instance to be either persisted or cached
            provider.persist( response, identifier, parameters, locale );
        }
        return response;
    }

    @Override
    public <T> List<T> list( @Nonnull Class<T> resource, @Nonnull Map<String, Object> criteria )
    {
        checkNotNull( criteria );

        return internalExecuteList( resource, criteria, null );
    }

    @Override
    public <T> List<T> list( @Nonnull Class<T> resource,
                             @Nonnull Map<String, Object> criteria,
                             @Nullable Locale locale )
    {
        checkNotNull( criteria );

        return internalExecuteList( resource, criteria, locale );
    }

    private <T> List<T> internalExecuteList( @Nonnull Class<T> resource,
                                             @Nullable Map<String, Object> criteria,
                                             @Nullable Locale locale )
    {
        checkNotNull( resource );

        if ( criteria == null )
        {
            criteria = new HashMap<>();
        }

        // looking for LocalResourceProvider optional implementation for given resource type
        LocalResourceProvider<T> provider = injector.getExistingResourceProvider( resource );
        List<T> response = null;

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
                remoteList = adaptee( resource ).executeList( criteria, locale );
            }
            catch ( IOException e )
            {
                logger.warn( "Resource " + resource.getName(), e );
                throw new RuntimeException( e );
            }
            if ( remoteList == null )
            {
                response = Lists.newArrayList();
            }
            else
            {
                response = mapper.mapAsList( remoteList, resource );
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
    public <T> T insert( @Nonnull T resource )
    {
        return insert( resource, null );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> T insert( @Nonnull T resource, @Nullable Object parentKey )
    {
        checkNotNull( resource );

        Object source = mapper.map( resource, getSourceClassFor( resource.getClass() ) );
        try
        {
            source = adaptee( resource.getClass() ).executeInsert( source, parentKey );
        }
        catch ( IOException e )
        {
            logger.warn( "Resource " + resource.getClass().getName() + ", parent key: " + parentKey, e );
            throw new RuntimeException( e );
        }

        return ( T ) mapper.map( source, resource.getClass() );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public <T> T update( @Nonnull T resource, @Nonnull Object identifier )
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        Object source = mapper.map( resource, getSourceClassFor( resource.getClass() ) );
        try
        {
            source = adaptee( resource.getClass() ).executeUpdate( source, identifier );
        }
        catch ( IOException e )
        {
            logger.warn( "Resource " + resource.getClass().getName() + ", identifier: " + identifier, e );
            throw new RuntimeException( e );
        }

        return ( T ) mapper.map( source, resource.getClass() );
    }

    @Override
    public <T> T patch( @Nonnull Patch<T> resource, @Nonnull Object identifier )
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        Object source = mapper.map( resource, getSourceClassFor( resource.getClass() ) );
        try
        {
            String alias = resource.getClass().getSimpleName();
            source = adaptee( resource.getClass() ).executePatch( source, identifier, alias );
        }
        catch ( IOException e )
        {
            logger.warn( "Resource " + resource.getClass().getName() + ", identifier: " + identifier, e );
            throw new RuntimeException( e );
        }

        return mapper.map( source, resource.type() );
    }

    @Override
    public <T> void delete( @Nonnull Class<T> resource, @Nonnull Object identifier )
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        try
        {
            adaptee( resource ).executeDelete( identifier );
        }
        catch ( IOException e )
        {
            logger.warn( "Resource " + resource.getName() + ", identifier: " + identifier, e );
            throw new RuntimeException( e );
        }
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

    @SuppressWarnings( "unchecked" )
    private <T, I> RestExecutorAdaptee<T, I> adaptee( Class<?> clazz )
    {
        RestExecutorAdaptee adaptee = binder.adaptee( clazz );
        if ( adaptee == null )
        {
            throw new IllegalArgumentException( "Missing mapped adaptee for " + clazz );
        }

        return ( RestExecutorAdaptee<T, I> ) adaptee;
    }
}
