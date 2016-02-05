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

package org.ctoolkit.restapi.client.identity.verifier;

import com.google.api.services.identitytoolkit.IdentityToolkit;
import com.google.identitytoolkit.HttpSender;
import com.google.identitytoolkit.JsonTokenHelper;
import com.google.identitytoolkit.RpcHelper;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.ctoolkit.restapi.client.TokenVerifier;
import org.ctoolkit.restapi.client.googleapis.GoogleApiCredentialFactory;
import org.ctoolkit.restapi.client.identity.Identity;

import javax.inject.Singleton;
import java.io.InputStream;

/**
 * The verifier guice module.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class VerifierModule
        extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( new TypeLiteral<TokenVerifier<Identity>>()
        {
        } ).to( IdentityTokenVerifier.class ).asEagerSingleton();
    }

    @Provides
    @Singleton
    JsonTokenHelper provideJsonTokenHelper( GoogleApiCredentialFactory factory, Injector injector )
    {
        HttpSender sender = injector.getInstance( HttpSender.class );
        InputStream stream = factory.getServiceAccountPrivateKeyP12Stream();
        String serviceAccount = factory.getServiceAccountEmail();

        RpcHelper rpcHelper = new RpcHelper( sender, IdentityToolkit.DEFAULT_BASE_URL, serviceAccount, stream );

        return new JsonTokenHelper( rpcHelper, factory.getApiKey(), factory.getProjectId() );
    }
}
