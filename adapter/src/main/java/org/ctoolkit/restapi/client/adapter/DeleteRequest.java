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

import org.ctoolkit.restapi.client.AuthRequest;
import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.SimpleRequest;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The DELETE request implementation that delegates callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class DeleteRequest
        implements SimpleRequest<Void>
{
    private final Class resource;

    private final Object identifier;

    private final RestFacadeAdapter adapter;

    private final DeleteExecutorAdaptee adaptee;

    private final Object remoteRequest;

    private Map<String, Object> params;

    private Locale withLocale;

    private GoogleRequestHeaders filler;

    private String token;

    DeleteRequest( @Nonnull Class resource,
                   @Nonnull Object identifier,
                   @Nonnull RestFacadeAdapter adapter,
                   @Nonnull DeleteExecutorAdaptee adaptee,
                   @Nonnull Object remoteRequest )
    {
        this.resource = checkNotNull( resource );
        this.identifier = checkNotNull( identifier );
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.remoteRequest = checkNotNull( remoteRequest );
        this.params = new HashMap<>();
        this.filler = new GoogleRequestHeaders( remoteRequest );
    }

    @Override
    public Void finish()
    {
        return finish( null, withLocale );
    }

    @Override
    public Void finish( @Nonnull RequestCredential credential )
    {
        checkNotNull( credential );
        credential.populate( this.params );
        return finish();
    }

    @Override
    public Void finish( @Nullable Map<String, Object> parameters )
    {
        return finish( parameters, withLocale );
    }

    @Override
    public Void finish( @Nullable Locale locale )
    {
        return finish( null, locale );
    }

    @Override
    public Void finish( @Nullable Map<String, Object> parameters, @Nullable Locale locale )
    {
        if ( parameters != null )
        {
            params.putAll( parameters );
        }

        filler.acceptLanguage( locale );
        if ( token != null )
        {
            filler.authorization( token );
        }

        return adapter.callbackExecuteDelete( adaptee, remoteRequest, resource, identifier, params, locale );
    }

    @Override
    public Request<Void> configWith( @Nonnull Properties properties )
    {
        checkNotNull( properties );
        RequestCredential.populate( properties, this.params );
        return this;
    }

    @Override
    public Request<Void> forLang( @Nonnull Locale locale )
    {
        this.withLocale = checkNotNull( locale );
        return this;
    }

    @Override
    public Request<Void> add( @Nonnull String name, @Nonnull Object value )
    {
        checkNotNull( name );
        checkNotNull( value );

        params.put( name, value );
        return this;
    }

    @Override
    public Request<Void> add( @Nonnull String name, @Nonnull String value )
    {
        checkNotNull( name );
        checkNotNull( value );

        params.put( name, value );
        return this;
    }

    @Override
    public Request<Void> addHeader( @Nonnull String header, @Nonnull String value )
    {
        checkNotNull( header );
        checkNotNull( value );

        filler.addHeader( header, value );
        return this;
    }

    @Override
    public AuthRequest<Void> authBy( @Nonnull String authorization )
    {
        checkNotNull( authorization );

        this.token = authorization;
        return new AuthRequestImpl<>( this, filler );
    }
}
