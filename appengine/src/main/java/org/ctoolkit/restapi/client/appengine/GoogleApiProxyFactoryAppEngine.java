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
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import org.ctoolkit.restapi.client.Credential;
import org.ctoolkit.restapi.client.adapter.BeforeRequestEvent;
import org.ctoolkit.restapi.client.adapter.GoogleApiProxyFactory;
import org.ctoolkit.restapi.client.provider.AuthKeyProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    protected GoogleApiProxyFactoryAppEngine( @Credential Map<String, String> properties, EventBus eventBus )
    {
        super( properties, eventBus );
    }

    @Override
    public HttpRequestInitializer authorize( @Nonnull Collection<String> scopes,
                                             @Nullable String userAccount,
                                             @Nonnull String prefix )
            throws GeneralSecurityException, IOException
    {
        if ( super.isCredentialOn( prefix ) )
        {
            return super.authorize( scopes, userAccount, prefix );
        }
        else
        {
            if ( !Strings.isNullOrEmpty( userAccount ) )
            {
                String msg = "User account impersonate is not supported by AppIdentityCredential '" + userAccount + "'";
                throw new IllegalArgumentException( msg );
            }
            return new ConfiguredAppIdentityCredential( scopes, prefix );
        }
    }

    @com.google.inject.Inject( optional = true )
    public void setKeyProvider( AuthKeyProvider keyProvider )
    {
        super.setKeyProvider( keyProvider );
    }

    private class ConfiguredAppIdentityCredential
            extends AppIdentityCredential
    {
        private final int numberOfRetries;

        private final int readTimeout;

        ConfiguredAppIdentityCredential( Collection<String> scopes, String prefix )
        {
            super( scopes );
            this.numberOfRetries = getNumberOfRetries( prefix );
            this.readTimeout = getReadTimeout( prefix );
        }

        @Override
        public void intercept( HttpRequest request ) throws IOException
        {
            String authorization = request.getHeaders().getAuthorization();
            // the authorization header set by facade client has a preference, see Request#authBy(String)
            if ( authorization == null )
            {
                super.intercept( request );
            }
            eventBus.post( new BeforeRequestEvent( request ) );
        }

        @Override
        public void initialize( HttpRequest request ) throws IOException
        {
            super.initialize( request );
            configureHttpRequest( request, numberOfRetries, readTimeout );
        }
    }
}
