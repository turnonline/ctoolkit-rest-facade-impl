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

import java.io.IOException;

/**
 * {@link com.google.api.client.auth.oauth2.Credential} credential wrapper.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class CredentialInitialized
        extends Initialized<com.google.api.client.auth.oauth2.Credential>
{
    public CredentialInitialized( com.google.api.client.auth.oauth2.Credential initializer )
    {
        super( initializer );
    }

    @Override
    public String getAccessToken()
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

        return accessToken;
    }
}
