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

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Strings;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.common.base.Preconditions;
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

import static com.google.api.client.util.Preconditions.checkNotNull;

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
        Collection<String> checkedScopes = Preconditions.checkNotNull( scopes, "Scopes is mandatory" );
        if ( super.isCredentialOn( prefix ) )
        {
            return super.authorize( checkedScopes, userAccount, prefix );
        }
        else
        {
            if ( !Strings.isNullOrEmpty( userAccount ) )
            {
                String msg = "User account impersonate is not supported by AppIdentityCredential '" + userAccount + "'";
                throw new IllegalArgumentException( msg );
            }
            return new CredentialWrapper( checkedScopes, getHttpTransport(), getJsonFactory(), userAccount, prefix );
        }
    }

    @com.google.inject.Inject( optional = true )
    public void setKeyProvider( AuthKeyProvider keyProvider )
    {
        super.setKeyProvider( keyProvider );
    }

    private class CredentialWrapper
            extends GoogleCredential
    {
        private final AppIdentityCredential appIdentity;

        private final boolean scopesRequired;

        private final String userAccount;

        private final String prefix;

        private final int numberOfRetries;

        private final int readTimeout;

        /**
         * Constructs the wrapper using the default AppIdentityService.
         */
        CredentialWrapper( @Nonnull Collection<String> scopes,
                           @Nonnull HttpTransport transport,
                           @Nonnull JsonFactory jsonFactory,
                           @Nullable String userAccount,
                           @Nonnull String prefix )
        {
            this( new AppIdentityCredential( scopes ), transport, jsonFactory, userAccount, prefix );
        }

        private CredentialWrapper( AppIdentityCredential appIdentity,
                                   HttpTransport transport,
                                   JsonFactory jsonFactory,
                                   String userAccount,
                                   String prefix )
        {
            super( new GoogleCredential.Builder()
                    .setRequestInitializer( appIdentity )
                    .setTransport( checkNotNull( transport ) )
                    .setJsonFactory( checkNotNull( jsonFactory ) )
                    .setServiceAccountUser( userAccount ) );

            Collection<String> scopes = appIdentity.getScopes();
            this.scopesRequired = ( scopes == null || scopes.isEmpty() );
            this.appIdentity = appIdentity;
            this.userAccount = userAccount;
            this.prefix = checkNotNull( prefix );
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
                appIdentity.intercept( request );
            }
            eventBus.post( new BeforeRequestEvent( request ) );
        }

        @Override
        public void initialize( HttpRequest request ) throws IOException
        {
            appIdentity.initialize( request );
            configureHttpRequest( request, numberOfRetries, readTimeout );
        }

        public boolean createScopedRequired()
        {
            return scopesRequired;
        }

        public GoogleCredential createScoped( Collection<String> scopes )
        {
            return new CredentialWrapper(
                    new AppIdentityCredential.Builder( scopes )
                            .setAppIdentityService( appIdentity.getAppIdentityService() )
                            .build(),
                    getTransport(),
                    getJsonFactory(),
                    userAccount,
                    prefix );
        }

        @Override
        protected TokenResponse executeRefreshToken()
        {
            AppIdentityService.GetAccessTokenResult tokenResult = appIdentity.getAppIdentityService()
                    .getAccessToken( appIdentity.getScopes() );

            TokenResponse response = new TokenResponse();
            response.setAccessToken( tokenResult.getAccessToken() );

            long expiresInSeconds = ( tokenResult.getExpirationTime().getTime() - System.currentTimeMillis() ) / 1000;
            response.setExpiresInSeconds( expiresInSeconds );
            return response;
        }
    }
}
