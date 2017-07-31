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
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.common.eventbus.EventBus;
import org.ctoolkit.restapi.client.ApiToken;
import org.ctoolkit.restapi.client.adapter.BeforeRequestEvent;
import org.ctoolkit.restapi.client.googleapis.Credential;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.ctoolkit.restapi.client.provider.AuthKeyProvider;

import javax.inject.Inject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Map;

/**
 * The AppEngine specific factory to build credential instance.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class GoogleApiProxyFactoryAppEngine
        extends GoogleApiProxyFactory
{
    /**
     * Create factory instance.
     */
    @Inject
    protected GoogleApiProxyFactoryAppEngine( @Credential Map<String, String> properties,
                                              EventBus eventBus,
                                              FacadeApiInit facadeInit )
    {
        super( properties, eventBus, facadeInit.keyProvider );
    }

    @Override
    public ApiToken<? extends HttpRequestInitializer> authorize( Collection<String> scopes,
                                                                 String userAccount,
                                                                 final String prefix )
            throws GeneralSecurityException, IOException
    {
        if ( super.isCredentialOn( prefix ) )
        {
            // for local development (outside of the AppEngine) call standard authorization
            return super.authorize( scopes, userAccount, prefix );
        }
        else
        {
            AppIdentityCredential credential = new AppIdentityCredential( scopes )
            {
                @Override
                public void intercept( HttpRequest request ) throws IOException
                {
                    super.intercept( request );
                    eventBus.post( new BeforeRequestEvent( request ) );
                }

                @Override
                public void initialize( HttpRequest request ) throws IOException
                {
                    super.initialize( request );
                    request.setNumberOfRetries( getNumberOfRetries( prefix ) );
                }
            };
            return new AppIdentityCredentialApiToken( credential );
        }
    }

    static class FacadeApiInit
    {
        @com.google.inject.Inject( optional = true )
        AuthKeyProvider keyProvider = null;
    }
}
