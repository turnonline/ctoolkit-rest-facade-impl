/*
 * Copyright (c) 2018 Comvai, s.r.o. All Rights Reserved.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The authentication request implementation, it wraps the original request instance
 * and implements authentication related methods.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class AuthRequestImpl<T>
        implements AuthRequest<T>
{
    private final Request<T> request;

    private GoogleRequestHeaders filler;

    AuthRequestImpl( Request<T> request, GoogleRequestHeaders filler )
    {
        this.request = checkNotNull( request );
        this.filler = checkNotNull( filler );
    }

    @Override
    public Request<T> bearer()
    {
        filler.setAuthScheme( GoogleRequestHeaders.AuthScheme.BEARER );
        return this;
    }

    @Override
    public Request<T> oauth()
    {
        filler.setAuthScheme( GoogleRequestHeaders.AuthScheme.OAUTH );
        return this;
    }

    @Override
    public T finish()
    {
        return request.finish();
    }

    @Override
    public T finish( @Nonnull RequestCredential credential )
    {
        return request.finish( credential );
    }

    @Override
    public T finish( @Nullable Map<String, Object> parameters )
    {
        return request.finish( parameters );
    }

    @Override
    public T finish( @Nullable Locale locale )
    {
        return request.finish( locale );
    }

    @Override
    public T finish( @Nullable Map<String, Object> parameters, @Nullable Locale locale )
    {
        return request.finish( parameters, locale );
    }

    @Override
    public Request<T> configWith( @Nonnull Properties properties )
    {
        return request.configWith( properties );
    }

    @Override
    public Request<T> forLang( @Nonnull Locale locale )
    {
        return request.forLang( locale );
    }

    @Override
    public Request<T> add( @Nonnull String name, @Nonnull Object value )
    {
        return request.add( name, value );
    }

    @Override
    public Request<T> add( @Nonnull String name, @Nonnull String value )
    {
        return request.add( name, value );
    }

    @Override
    public Request<T> addHeader( @Nonnull String header, @Nonnull String value )
    {
        return request.addHeader( header, value );
    }

    @Override
    public Request<T> onBehalf( @Nonnull String email, @Nonnull String identityId )
    {
        return addHeader( Request.ON_BEHALF_OF_EMAIL, email ).addHeader( Request.ON_BEHALF_OF_USER_ID, identityId );
    }

    @Override
    public AuthRequest<T> authBy( @Nonnull String token )
    {
        return request.authBy( token );
    }
}
