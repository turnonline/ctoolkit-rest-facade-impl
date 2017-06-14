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

import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.UnderlyingRequest;
import org.ctoolkit.restapi.client.adaptee.UnderlyingExecutorAdaptee;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The request implementation that collects input parameters and then delegates a callback
 * to related adapter in order to prepare underlying request.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class UnderlyingRequestPreparation<U>
        implements UnderlyingRequest<U>
{
    private final RestFacadeAdapter adapter;

    private final UnderlyingExecutorAdaptee<U> adaptee;

    private Identifier identifier;

    private Object resource;

    private Map<String, Object> parameters;

    private U remoteRequest;

    UnderlyingRequestPreparation( @Nonnull RestFacadeAdapter adapter,
                                  @Nonnull UnderlyingExecutorAdaptee<U> adaptee )
    {
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.parameters = new HashMap<>();
    }

    @Override
    public U build()
    {
        if ( remoteRequest == null )
        {
            remoteRequest = adapter.callbackPrepareUnderlying( adaptee, resource, identifier, parameters );
        }
        return remoteRequest;
    }

    @Override
    public <R> Request<R> answerBy( @Nonnull Class<R> type )
    {
        return new UnderlyingRequestExecutor<>( type, adapter, adaptee, build() );
    }

    @Override
    public <R> Request<R> withPayload( @Nonnull R resource )
    {
        checkNotNull( resource );

        Class<?> remoteResource = adapter.evaluateRemoteResource( resource.getClass() );
        Object source;

        if ( resource.getClass() == remoteResource )
        {
            source = resource;
        }
        else
        {
            source = adapter.getMapper().map( resource, remoteResource );
        }

        this.resource = source;
        @SuppressWarnings( "unchecked" )
        Class<R> resourceType = ( Class<R> ) source.getClass();
        return new UnderlyingRequestExecutor<>( resourceType, adapter, adaptee, build() );
    }

    @Override
    public UnderlyingRequest<U> identifiedBy( @Nonnull Identifier identifier )
    {
        this.identifier = identifier;
        return this;
    }

    @Override
    public UnderlyingRequest<U> addParams( @Nonnull Map<String, Object> parameters )
    {
        this.parameters.putAll( checkNotNull( parameters ) );
        return this;
    }

    @Override
    public UnderlyingRequest<U> add( @Nonnull String name, @Nonnull Object value )
    {
        checkNotNull( name );
        checkNotNull( value );

        parameters.put( name, value );
        return this;
    }

    @Override
    public UnderlyingRequest<U> add( @Nonnull String name, @Nonnull String value )
    {
        checkNotNull( name );
        checkNotNull( value );

        parameters.put( name, value );
        return this;
    }
}
