/*
 * Copyright (c) 2016 Comvai, s.r.o. All Rights Reserved.
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
import com.google.api.services.identitytoolkit.IdentityToolkit;
import com.google.identitytoolkit.HttpSender;
import com.google.identitytoolkit.JsonTokenHelper;
import com.google.identitytoolkit.RpcHelper;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.ctoolkit.restapi.client.RemoteServerErrorException;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.ctoolkit.restapi.client.googleapis.GoogleApiCredentialFactory;
import org.ctoolkit.restapi.client.identity.verifier.VerifierModule;
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
    private static final Logger logger = LoggerFactory.getLogger( GoogleApiIdentityToolkitModule.class );

    private static final String IDENTITY_SCOPE = "https://www.googleapis.com/auth/identitytoolkit";

    @Override
    protected void configure()
    {
        install( new VerifierModule() );
    }

    @Provides
    @Singleton
    IdentityToolkit provideIdentityToolkit( GoogleApiCredentialFactory factory )
    {
        HashSet<String> set = new HashSet<>();
        set.add( IDENTITY_SCOPE );
        Collections.unmodifiableSet( set );
        Collection<String> scopes = Collections.unmodifiableSet( set );

        IdentityToolkit.Builder builder;

        try
        {
            HttpRequestInitializer credential = factory.authorize( scopes, null );
            builder = new IdentityToolkit.Builder( factory.getHttpTransport(), factory.getJsonFactory(), credential );
            builder.setApplicationName( factory.getApplicationName() );
        }
        catch ( GeneralSecurityException e )
        {
            logger.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + factory.getApplicationName()
                    + " Service account: " + factory.getServiceAccountEmail(), e );

            throw new UnauthorizedException( e.getMessage() );
        }
        catch ( IOException e )
        {
            logger.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + factory.getApplicationName()
                    + " Service account: " + factory.getServiceAccountEmail(), e );

            throw new RemoteServerErrorException( HttpStatusCodes.STATUS_CODE_SERVER_ERROR, e.getMessage() );
        }

        return builder.build();
    }

    @Provides
    @Singleton
    JsonTokenHelper provideJsonTokenHelper( GoogleApiCredentialFactory factory, RpcHelper rpcHelper )
    {
        return new JsonTokenHelper( rpcHelper, factory.getApiKey(), factory.getProjectId() );
    }

    @Provides
    @Singleton
    RpcHelper provideRpcHelper( GoogleApiCredentialFactory factory, Injector injector )
    {
        HttpSender sender = injector.getInstance( HttpSender.class );
        InputStream stream = factory.getServiceAccountPrivateKeyP12Stream();
        String serviceAccount = factory.getServiceAccountEmail();

        return new RpcHelper( sender, IdentityToolkit.DEFAULT_BASE_URL, serviceAccount, stream );
    }
}
