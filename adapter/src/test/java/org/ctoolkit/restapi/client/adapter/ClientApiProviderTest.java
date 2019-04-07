/*
 * Copyright (c) 2019 Comvai, s.r.o. All Rights Reserved.
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

package org.ctoolkit.restapi.client.adapter;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.common.collect.Lists;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.ctoolkit.restapi.client.ServiceUnavailableException;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.truth.Truth.assertThat;
import static org.ctoolkit.restapi.client.adapter.MockedClientApiProvider.FakeClient.API;
import static org.ctoolkit.restapi.client.adapter.MockedClientApiProvider.SCOPE;

/**
 * {@link ClientApiProvider} unit testing.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class ClientApiProviderTest
{
    @Tested
    private MockedClientApiProvider tested;

    @Injectable
    private GoogleApiProxyFactory factory;

    @Mocked
    private HttpRequestInitializer credential;

    @Test
    public void init_Default() throws GeneralSecurityException, IOException
    {
        tested.get();

        new Verifications()
        {
            {
                Collection<String> scopes;
                String email;
                factory.authorize( scopes = withCapture(), email = withCapture(), API );
                assertThat( email ).isNull();
                assertThat( scopes ).hasSize( 1 );
                assertThat( scopes ).contains( SCOPE );
            }
        };
    }

    @Test
    public void init_WithSpecificConfig() throws GeneralSecurityException, IOException
    {
        String scope = "https://www.googleapis.com/auth/fake";
        ArrayList<String> scopes = Lists.newArrayList( SCOPE, scope );
        String userEmail = "specific@turnonline.biz";

        // test call
        tested.init( scopes, userEmail );
        MockedClientApiProvider.FakeClient client = tested.get();

        assertThat( client ).isNotNull();
        assertThat( client.getCredential() ).isSameAs( credential );

        new Verifications()
        {
            {
                Collection<String> scopes;
                String email;
                factory.authorize( scopes = withCapture(), email = withCapture(), API );
                assertThat( email ).isEqualTo( userEmail );
                assertThat( scopes ).hasSize( 2 );
                assertThat( scopes ).containsAllOf( SCOPE, scope );
            }
        };
    }

    @Test( expectedExceptions = UnauthorizedException.class )
    public void init_SecurityFailure() throws GeneralSecurityException, IOException
    {
        new Expectations()
        {
            {
                factory.getHttpTransport();
                result = new GeneralSecurityException();
            }
        };

        tested.init( Lists.newArrayList( SCOPE ), "specific@turnonline.biz" );
    }

    @Test( expectedExceptions = ServiceUnavailableException.class )
    public void init_ConnectionError() throws GeneralSecurityException, IOException
    {
        new Expectations()
        {
            {
                factory.getHttpTransport();
                result = new IOException();
            }
        };

        tested.init( Lists.newArrayList( SCOPE ), "specific@turnonline.biz" );
    }
}