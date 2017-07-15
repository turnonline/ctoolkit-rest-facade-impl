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

package org.ctoolkit.restapi.client.googleapis;

import com.google.api.client.http.HttpRequestInitializer;

import java.io.Serializable;
import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The credential wrapper to get access to the token data specific to concrete API.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public abstract class ApiToken<T extends HttpRequestInitializer>
{
    private final T initializer;

    public ApiToken( T initializer )
    {
        this.initializer = checkNotNull( initializer );
    }

    /**
     * Returns the OAuth2 API access token and its remaining lifetime ({@code null} if unknown).
     * <p>
     * Default implementation will try to refresh the access token (a new token from the authorization endpoint)
     * if there is no access token or if we are 1 minute away from expiration.
     *
     * @return the API access token or {@code null} for none.
     */
    public abstract Data getTokenData();

    /**
     * Returns the credential instance specific to concrete API.
     *
     * @return the credential
     */
    public T getCredential()
    {
        return initializer;
    }

    /**
     * The access token and token expiration time wrapper.
     */
    public static class Data
            implements Serializable
    {
        private static final long serialVersionUID = 7167727815990334365L;

        private final String accessToken;

        private final Date expirationTime;

        public Data( String accessToken, Date expirationTime )
        {
            this.accessToken = accessToken;
            this.expirationTime = expirationTime;
        }

        /**
         * Returns the access token.
         *
         * @return the access token
         */
        public String getAccessToken()
        {
            return accessToken;
        }

        /**
         * Returns the token remaining lifetime.
         *
         * @return the token remaining lifetime
         */
        public Date getExpirationTime()
        {
            return expirationTime;
        }
    }
}
