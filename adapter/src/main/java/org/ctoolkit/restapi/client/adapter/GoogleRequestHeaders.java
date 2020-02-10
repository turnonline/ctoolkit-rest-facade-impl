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
import org.ctoolkit.restapi.client.AuthRequest;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.provider.TokenProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

/**
 * The class with convenient methods to populate {@link HttpHeaders} with values.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class GoogleRequestHeaders
{
    private final HttpHeaders headers;

    private final RestFacadeAdapter adapter;

    private AuthRequest.AuthScheme authScheme;

    private TokenProvider<Object> provider;

    private Object onBehalfOf;

    GoogleRequestHeaders( RestFacadeAdapter adapter, Object remoteRequest )
    {
        this.adapter = adapter;

        if ( remoteRequest instanceof AbstractGoogleClientRequest )
        {
            this.headers = ( ( AbstractGoogleClientRequest<?> ) remoteRequest ).getRequestHeaders();
        }
        else
        {
            this.headers = new HttpHeaders();
        }
    }

    GoogleRequestHeaders( RestFacadeAdapter adapter )
    {
        this.adapter = adapter;
        this.headers = new HttpHeaders();
    }

    /**
     * Apply optional Accept-Language to this request headers.
     *
     * @param locale the optional locale to be set, {@code null} value will be ignored
     */
    void acceptLanguage( @Nullable Locale locale )
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
    void contentType( @Nullable String type )
    {
        if ( type != null )
        {
            headers.setContentType( type );
        }
    }

    /**
     * Sets the authentication scheme as a prefix to the authorization token.
     *
     * @param authScheme the the authentication scheme to be set
     */
    void setAuthScheme( AuthRequest.AuthScheme authScheme )
    {
        this.authScheme = authScheme;
    }

    void setOnBehalfOf( Object onBehalfOf )
    {
        this.onBehalfOf = onBehalfOf;
    }

    /**
     * Sets the 'Authorization' header if there is an explicit configuration to this request,
     * otherwise the underlying authorization mechanism will be used.
     */
    void setAuthorizationIf()
    {
        if ( provider == null && onBehalfOf != null )
        {
            // Impl of TokenProvider is required, search for one. Expected to be possible to inject.
            provider = adapter.getTokenProvider( onBehalfOf.getClass() );
            if ( provider == null )
            {
                String msg = "Missing binding between TokenProvider and on behalf of user: "
                        + TokenProvider.class.getSimpleName()
                        + "<"
                        + onBehalfOf.getClass().getName()
                        + ">";

                throw new IllegalArgumentException( msg );
            }
        }

        if ( provider != null )
        {
            String token = provider.token( authScheme, onBehalfOf );
            if ( token != null )
            {
                headers.setAuthorization( token );
            }

            Map<String, String> headers = provider.headers( onBehalfOf );
            if ( headers != null )
            {
                this.headers.putAll( headers );
            }
        }
    }

    /**
     * Sets the custom implementation that provides a token for 'Authorization' header.
     * Additional optional headers provided by {@link TokenProvider#headers(Object)}.
     */
    @SuppressWarnings( "unchecked" )
    void setTokenCreator( @Nullable TokenProvider<?> provider )
    {
        this.provider = ( TokenProvider<Object> ) provider;
    }

    /**
     * Add header to the request.
     *
     * @param header the name of the header
     * @param value  the header value
     */
    void addHeader( @Nonnull String header, @Nonnull String value )
    {
        headers.put( header, value );
    }

    void fillInCredential( @Nullable Map<String, Object> params )
    {
        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( params, false );

        String apiKey = credential.getApiKey();
        if ( !Strings.isNullOrEmpty( apiKey ) )
        {
            provider = ( FinalTokenProvider ) () -> apiKey;
        }
    }

    HttpHeaders getHeaders()
    {
        return headers;
    }
}
