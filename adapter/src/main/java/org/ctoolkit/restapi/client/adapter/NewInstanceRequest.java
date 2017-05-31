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

import org.ctoolkit.restapi.client.ClientErrorException;
import org.ctoolkit.restapi.client.PayloadRequest;
import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The NEW request implementation that delegates callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class NewInstanceRequest<T>
        implements PayloadRequest<T>
{
    private final Class<T> resource;

    private final ResourceFacadeAdapter adapter;

    private final NewExecutorAdaptee adaptee;

    private final Object remoteRequest;

    private RequestCredential credential;

    private Map<String, Object> params;

    private Locale withLocale;

    NewInstanceRequest( @Nonnull Class<T> resource,
                        @Nonnull ResourceFacadeAdapter adapter,
                        @Nonnull NewExecutorAdaptee adaptee,
                        @Nullable Object remoteRequest )
    {
        this.resource = checkNotNull( resource );
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.remoteRequest = remoteRequest;
        this.params = new HashMap<>();
    }

    @Override
    public T finish()
    {
        return finish( null, withLocale );
    }

    @Override
    public T finish( @Nullable Map<String, Object> criteria )
    {
        return finish( criteria, withLocale );
    }

    @Override
    public T finish( @Nullable Locale locale )
    {
        return finish( null, locale );
    }

    @Override
    public T finish( @Nullable Map<String, Object> parameters, @Nullable Locale locale )
    {
        if ( remoteRequest == null )
        {
            try
            {
                return resource.newInstance();
            }
            catch ( InstantiationException | IllegalAccessException e )
            {
                throw new ClientErrorException( 400, e.getMessage() );
            }
        }

        if ( credential != null )
        {
            parameters = credential.populate( parameters );
        }
        if ( parameters != null )
        {
            params.putAll( parameters );
        }

        return adapter.callbackNewInstance( adaptee, remoteRequest, resource, params, locale );
    }

    @Override
    public Request<T> configWith( @Nonnull RequestCredential credential )
    {
        this.credential = checkNotNull( credential );
        return this;
    }

    @Override
    public Request<T> forLang( @Nonnull Locale locale )
    {
        this.withLocale = checkNotNull( locale );
        return this;
    }

    @Override
    public Request<T> add( @Nonnull String name, @Nonnull Object value )
    {
        checkNotNull( name );
        checkNotNull( value );

        params.put( name, value );
        return this;
    }

    @Override
    public Request<T> add( @Nonnull String name, @Nonnull String value )
    {
        checkNotNull( name );
        checkNotNull( value );

        params.put( name, value );
        return this;
    }

    @Override
    public <R> Request<R> answerBy( @Nonnull Class<R> type )
    {
        return new NewInstanceRequest<>( type, adapter, adaptee, remoteRequest );
    }

    @Override
    public <R> R finish( @Nullable Class<R> type )
    {
        return answerBy( type ).finish();
    }
}
