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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The credential wrapper to get access to the token refresher specific to concrete API.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public abstract class Initialized<T extends HttpRequestInitializer>
{
    private final T initializer;

    public Initialized( T initializer )
    {
        this.initializer = checkNotNull( initializer );
    }

    /**
     * Returns the OAuth2 access token. If there is no token, it will return {@code null}.
     * <p>
     * Default implementation is to try to refresh the access token (a new token from the authorization endpoint)
     * if there is no access token or if we are 1 minute away from expiration.
     *
     * @return the access token or {@code null} for none.
     */
    public abstract String getAccessToken();

    /**
     * Returns the credential instance specific to concrete API.
     *
     * @return the credential
     */
    public T getCredential()
    {
        return initializer;
    }
}
