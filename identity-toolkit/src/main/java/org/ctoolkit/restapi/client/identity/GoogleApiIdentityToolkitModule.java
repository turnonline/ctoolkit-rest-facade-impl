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

package org.ctoolkit.restapi.client.identity;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.services.identitytoolkit.IdentityToolkit;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.ctoolkit.restapi.client.AccessToken;
import org.ctoolkit.restapi.client.ApiToken;
import org.ctoolkit.restapi.client.RemoteServerErrorException;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.ctoolkit.restapi.client.identity.verifier.IdentityVerifierModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * The Google Identity Toolkit guice module as a default configuration.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GoogleApiIdentityToolkitModule
        extends AbstractModule
{
    public static final String API_PREFIX = "identitytoolkit";

    private static final Logger logger = LoggerFactory.getLogger( GoogleApiIdentityToolkitModule.class );

    private static final String IDENTITY_SCOPE = "https://www.googleapis.com/auth/identitytoolkit";

    private ApiToken<? extends HttpRequestInitializer> initialized;

    @Override
    protected void configure()
    {
        install( new IdentityVerifierModule() );
    }

    @Provides
    @Singleton
    IdentityToolkit provideIdentityToolkit( GoogleApiProxyFactory factory )
    {
        InputStream stream = null;
        String fileNamePath = factory.getFileName( API_PREFIX );
        if ( fileNamePath != null )
        {
            stream = factory.getServiceAccountPrivateKeyP12Stream( API_PREFIX );
        }

        if ( stream == null )
        {
            String message;
            if ( fileNamePath == null )
            {
                message = "The private key (path to p12) 'credential.default.fileName' is mandatory to instantiate "
                        + IdentityToolkit.class.getSimpleName();
            }
            else
            {
                message = "Configured path to private key p12 is incorrect, no file has been found: " + fileNamePath;
            }

            throw new IllegalArgumentException( message );
        }

        String serviceAccount = factory.getServiceAccountEmail( API_PREFIX );
        if ( Strings.isNullOrEmpty( serviceAccount ) )
        {
            String message = "The service account email 'credential.default.serviceAccountEmail'" +
                    " is mandatory to instantiate " + IdentityToolkit.class.getSimpleName();

            throw new IllegalArgumentException( message );
        }

        HashSet<String> set = new HashSet<>();
        set.add( IDENTITY_SCOPE );
        Collections.unmodifiableSet( set );
        Collection<String> scopes = Collections.unmodifiableSet( set );

        IdentityToolkit.Builder builder;

        try
        {
            initialized = factory.authorize( scopes, null, API_PREFIX );
            HttpRequestInitializer credential = initialized.getCredential();
            builder = new IdentityToolkit.Builder( factory.getHttpTransport(), factory.getJsonFactory(), credential );
            builder.setApplicationName( factory.getApplicationName( API_PREFIX ) );
        }
        catch ( GeneralSecurityException e )
        {
            logger.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + factory.getApplicationName( API_PREFIX )
                    + " Service account: " + factory.getServiceAccountEmail( API_PREFIX ), e );

            throw new UnauthorizedException( e.getMessage() );
        }
        catch ( IOException e )
        {
            logger.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + factory.getApplicationName( API_PREFIX )
                    + " Service account: " + factory.getServiceAccountEmail( API_PREFIX ), e );

            throw new RemoteServerErrorException( HttpStatusCodes.STATUS_CODE_SERVER_ERROR, e.getMessage() );
        }

        return builder.build();
    }

    @Provides
    @AccessToken( apiName = API_PREFIX )
    ApiToken.Data provideIdentityToolkitTokenData( IdentityToolkit client )
    {
        initialized.setServiceUrl( client.getBaseUrl() );
        return initialized.getTokenData();
    }
}
