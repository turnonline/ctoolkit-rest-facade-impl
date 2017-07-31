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

import com.google.common.base.Strings;
import org.ctoolkit.restapi.client.ApiToken;

import java.io.IOException;
import java.util.Date;

/**
 * {@link com.google.api.client.auth.oauth2.Credential} credential wrapper.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class CredentialApiToken
        extends ApiToken<com.google.api.client.auth.oauth2.Credential>
{
    public CredentialApiToken( com.google.api.client.auth.oauth2.Credential initializer )
    {
        super( initializer );
    }

    @Override
    public Data getTokenData()
    {
        com.google.api.client.auth.oauth2.Credential credential;
        credential = getCredential();

        String accessToken = credential.getAccessToken();

        try
        {
            Long expiresIn = credential.getExpiresInSeconds();
            // check if token will expire in a minute
            if ( accessToken == null || expiresIn != null && expiresIn <= 60 )
            {
                if ( credential.refreshToken() )
                {
                    accessToken = credential.getAccessToken();
                }
                else
                {
                    return null;
                }
            }
        }
        catch ( IOException e )
        {
            return null;
        }

        if ( Strings.isNullOrEmpty( accessToken ) )
        {
            return null;
        }

        Date expirationTime = null;
        Long expirationTimeMilliseconds = credential.getExpirationTimeMilliseconds();
        if ( expirationTimeMilliseconds != null )
        {
            expirationTime = new Date( expirationTimeMilliseconds );
        }

        return new Data( accessToken, expirationTime );
    }
}
