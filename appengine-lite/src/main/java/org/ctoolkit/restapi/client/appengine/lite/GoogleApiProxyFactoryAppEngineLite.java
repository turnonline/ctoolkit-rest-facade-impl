package org.ctoolkit.restapi.client.appengine.lite;

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
 * The AppEngine specific factory to build credential instance (Lite).
 */
class GoogleApiProxyFactoryAppEngineLite
        extends GoogleApiProxyFactory
{
    @Inject
    protected GoogleApiProxyFactoryAppEngineLite( @Credential Map<String, String> properties, EventBus eventBus )
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
