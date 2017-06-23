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

import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.RetrievalRequest;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The GET request implementation that delegates callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class GetRequest<T>
        implements RetrievalRequest<T>
{
    private final Class<T> resource;

    private final Object identifier;

    private final RestFacadeAdapter adapter;

    private final GetExecutorAdaptee adaptee;

    private final Object remoteRequest;

    private Map<String, Object> params;

    private Locale withLocale;

    GetRequest( @Nonnull Class<T> resource,
                @Nonnull Object identifier,
                @Nonnull RestFacadeAdapter adapter,
                @Nonnull GetExecutorAdaptee adaptee,
                @Nonnull Object remoteRequest )
    {
        this.resource = checkNotNull( resource );
        this.identifier = checkNotNull( identifier );
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.remoteRequest = checkNotNull( remoteRequest );
        this.params = new HashMap<>();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <U> U underlying( Class<U> type )
    {
        return ( U ) remoteRequest;
    }

    @Override
    public T finish()
    {
        return finish( null, withLocale );
    }

    @Override
    public T finish( @Nonnull RequestCredential credential )
    {
        checkNotNull( credential );
        credential.populate( this.params );
        return finish();
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
        if ( parameters != null )
        {
            params.putAll( parameters );
        }

        return adapter.callbackExecuteGet( adaptee, remoteRequest, resource, identifier, params, locale );
    }

    @Override
    public Request<T> configWith( @Nonnull Properties properties )
    {
        checkNotNull( properties );
        RequestCredential.populate( properties, this.params );
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
}
