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

package org.ctoolkit.restapi.client.appengine;

import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.appengine.api.appidentity.AppIdentityService;
import org.ctoolkit.restapi.client.ApiToken;

import java.util.Collection;
import java.util.Date;

/**
 * {@link AppIdentityCredential} credential wrapper.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class AppIdentityCredentialApiToken
        extends ApiToken<AppIdentityCredential>
{
    public AppIdentityCredentialApiToken( AppIdentityCredential initializer )
    {
        super( initializer );
    }

    @Override
    public Data getTokenData()
    {
        AppIdentityCredential credential = getCredential();
        Collection<String> scopes = credential.getScopes();

        AppIdentityService.GetAccessTokenResult result = credential.getAppIdentityService().getAccessToken( scopes );
        String accessToken = result.getAccessToken();
        Date expirationTime = result.getExpirationTime();

        return new Data( accessToken, expirationTime );
    }
}
