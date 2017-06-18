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

import org.ctoolkit.restapi.client.PayloadRequest;
import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The UPDATE request implementation that delegates callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class UpdateRequest<T>
        implements PayloadRequest<T>
{
    private final Class<T> resource;

    private final Object identifier;

    private final RestFacadeAdapter adapter;

    private final UpdateExecutorAdaptee adaptee;

    private final Object remoteRequest;

    private RequestCredential credential;

    private Map<String, Object> params;

    private Locale withLocale;

    UpdateRequest( @Nonnull Class<T> resource,
                   @Nonnull Object identifier,
                   @Nonnull RestFacadeAdapter adapter,
                   @Nonnull UpdateExecutorAdaptee adaptee,
                   @Nonnull Object remoteRequest )
    {
        this.resource = checkNotNull( resource );
        this.identifier = checkNotNull( identifier );
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.remoteRequest = checkNotNull( remoteRequest );
        this.params = new HashMap<>();
    }

    @Override
    public T finish()
    {
        return finish( null, withLocale );
    }

    @Override
    public T finish( @Nullable Map<String, Object> parameters )
    {
        return finish( parameters, withLocale );
    }

    @Override
    public T finish( @Nullable Locale locale )
    {
        return finish( null, locale );
    }

    @Override
    public T finish( @Nullable Map<String, Object> parameters, @Nullable Locale locale )
    {
        if ( credential != null )
        {
            parameters = credential.populate( parameters );
        }
        if ( parameters != null )
        {
            params.putAll( parameters );
        }

        return adapter.callbackExecuteUpdate( adaptee, remoteRequest, resource, identifier, params, locale );
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
        return new UpdateRequest<>( type, identifier, adapter, adaptee, remoteRequest );
    }

    @Override
    public <R> R finish( @Nonnull Class<R> type )
    {
        return answerBy( type ).finish();
    }
}
