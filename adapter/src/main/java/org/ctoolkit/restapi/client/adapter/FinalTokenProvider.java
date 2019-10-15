/*
 * Copyright (c) 2019 Comvai, s.r.o. All Rights Reserved.
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

import com.google.common.base.Strings;
import org.ctoolkit.restapi.client.AuthRequest;
import org.ctoolkit.restapi.client.provider.TokenProvider;

import javax.annotation.Nullable;

/**
 * Suitable for use cases if client has a ready to use token.
 * <p>
 * {@code ( FinalTokenProvider ) () -> token}
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
interface FinalTokenProvider
        extends TokenProvider<Object>
{
    /**
     * If token provided by {@link #token()} is already prepended by auth scheme,
     * an already provided configuration will be ignored.
     *
     * @param authScheme the authorization scheme or {@code null} if no scheme to be prepended to the token
     * @return the final token to be set as a 'Authorization' header
     */
    @Override
    default String token( @Nullable AuthRequest.AuthScheme authScheme, Object of )
    {
        String token = token();
        String finalToken = null;
        boolean hasAuthScheme = false;

        if ( !Strings.isNullOrEmpty( token ) )
        {
            for ( AuthRequest.AuthScheme scheme : AuthRequest.AuthScheme.values() )
            {
                if ( token.startsWith( scheme.getValue() ) )
                {
                    finalToken = token;
                    hasAuthScheme = true;
                    break;
                }
            }

            if ( !hasAuthScheme )
            {
                finalToken = authScheme == null ? token : authScheme.getValue() + " " + token;
            }
        }
        return finalToken;
    }

    String token();
}
