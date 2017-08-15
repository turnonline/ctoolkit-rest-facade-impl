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

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.common.base.Strings;
import org.ctoolkit.restapi.client.RequestCredential;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class with convenient methods to populate {@link HttpHeaders} with values.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GoogleRequestHeadersFiller
{
    private final HttpHeaders headers;

    public GoogleRequestHeadersFiller( Object remoteRequest )
    {
        if ( remoteRequest instanceof AbstractGoogleClientRequest )
        {
            this.headers = ( ( AbstractGoogleClientRequest ) remoteRequest ).getRequestHeaders();
        }
        else
        {
            this.headers = new HttpHeaders();
        }
    }

    public GoogleRequestHeadersFiller( HttpHeaders headers )
    {
        this.headers = checkNotNull( headers );
    }

    /**
     * Apply optional Accept-Language to this request headers.
     *
     * @param locale the optional locale to be set, {@code null} value will be ignored
     */
    public void acceptLanguage( @Nullable Locale locale )
    {
        if ( locale != null )
        {
            String languageTag = new java.util.Locale( locale.getLanguage(), locale.getCountry() ).toLanguageTag();
            headers.put( com.google.common.net.HttpHeaders.ACCEPT_LANGUAGE, languageTag );
        }
    }

    /**
     * Apply content type to this request headers.
     *
     * @param type the optional content type to be set, {@code null} value will be ignored
     */
    public void contentType( @Nullable String type )
    {
        if ( type != null )
        {
            headers.setContentType( type );
        }
    }

    /**
     * Sets the {@code "Authorization"} header to this request.
     *
     * @param token the authorization token to be set, {@code null} value will be ignored
     */
    public void authorization( String token )
    {
        if ( !Strings.isNullOrEmpty( token ) )
        {
            headers.setAuthorization( token );
        }
    }

    /**
     * Add header to the request.
     *
     * @param header the name of the header
     * @param value  the header value
     */
    public void addHeader( @Nonnull String header, @Nonnull String value )
    {
        headers.put( header, value );
    }

    public void fillInCredential( @Nullable Map<String, Object> params )
    {
        //noinspection MismatchedQueryAndUpdateOfCollection
        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( params, false );

        authorization( credential.getApiKey() );
    }

    public HttpHeaders getHeaders()
    {
        return headers;
    }
}
