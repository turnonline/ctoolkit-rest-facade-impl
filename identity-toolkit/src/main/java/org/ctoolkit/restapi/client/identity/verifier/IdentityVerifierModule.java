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

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.services.identitytoolkit.IdentityToolkit;
import com.google.identitytoolkit.HttpSender;
import com.google.identitytoolkit.RpcHelper;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import net.oauth.jsontoken.Checker;
import org.ctoolkit.restapi.client.TokenVerifier;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.ctoolkit.restapi.client.identity.GoogleApiIdentityToolkitModule;
import org.ctoolkit.restapi.client.identity.Identity;

import javax.inject.Singleton;
import java.io.InputStream;

/**
 * The identity verifier standalone guice module configured
 * specifically for {@link org.ctoolkit.restapi.client.TokenVerifier}.
 * <p>
 * The {@link RpcHelper} might be instantiated with <code>null</code> service account email and no private key.
 * In case of public cert download {@link RpcHelper#downloadCerts()} we don't need those credential
 * (this token verification use case).
 * <p>
 * Optionally you can provide your own audience checker {@link Checker} implementation configured in client
 * guice module, for example
 * <pre>
 *   public class ClientModule
 *           extends AbstractModule {
 *
 *     &#064;Override
 *     protected void configure()
 *     {
 *         bind( Checker.class ).to( MyAudienceChecker.class );
 *         ...
 *     }
 *   }</pre>
 * <p>
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class IdentityVerifierModule
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
    GtokenVerifier provideGtokenVerifier( GoogleApiProxyFactory factory, RpcHelper rpcHelper, CheckerInit init )
    {
        Checker checker = init.checker;
        GtokenVerifier verifier;

        if ( checker == null )
        {
            String projectId = factory.getProjectId( GoogleApiIdentityToolkitModule.API_PREFIX );
            if ( Strings.isNullOrEmpty( projectId ) )
            {
                String message = "Project ID (audience) 'credential.default.projectId' must be configured " +
                        "or provide your own implementation of audience " + Checker.class.getName();

                throw new IllegalArgumentException( message );
            }
            verifier = new GtokenVerifier( rpcHelper, projectId );
        }
        else
        {
            verifier = new GtokenVerifier( rpcHelper, checker );
        }
        return verifier;
    }

    @Provides
    @Singleton
    RpcHelper provideRpcHelper( GoogleApiProxyFactory factory, Injector injector )
    {
        String serviceAccount = factory.getServiceAccountEmail( GoogleApiIdentityToolkitModule.API_PREFIX );
        InputStream stream = null;

        if ( factory.getFileName( GoogleApiIdentityToolkitModule.API_PREFIX ) != null )
        {
            stream = factory.getServiceAccountPrivateKeyP12Stream( GoogleApiIdentityToolkitModule.API_PREFIX );
        }

        HttpSender sender = injector.getInstance( HttpSender.class );
        return new RpcHelper( sender, IdentityToolkit.DEFAULT_BASE_URL, serviceAccount, stream );
    }

    static class CheckerInit
    {
        @Inject( optional = true )
        Checker checker = null;
    }
}
