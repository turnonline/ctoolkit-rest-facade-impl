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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Collection;

import static org.ctoolkit.restapi.client.adapter.MockedClientApiProvider.FakeClient.API;

/**
 * {@link ClientApiProvider} impl. for testing purpose.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class MockedClientApiProvider
        extends ClientApiProvider<MockedClientApiProvider.FakeClient>
{
    static final String SCOPE = "https://www.googleapis.com/auth/fake.readonly";

    public MockedClientApiProvider( GoogleApiProxyFactory factory )
    {
        super( factory );
    }

    @Override
    protected Collection<String> defaultScopes()
    {
        return Lists.newArrayList( SCOPE );
    }

    @Override
    protected String api()
    {
        return API;
    }

    @Override
    protected FakeClient build( @Nonnull GoogleApiProxyFactory factory,
                                @Nonnull HttpTransport transport,
                                @Nonnull JsonFactory jsonFactory,
                                @Nonnull HttpRequestInitializer credential,
                                @Nonnull String api )
    {
        return new FakeClient( credential );
    }

    class FakeClient
    {
        static final String API = "fake";

        private HttpRequestInitializer credential;

        FakeClient( HttpRequestInitializer credential )
        {
            this.credential = credential;
        }

        HttpRequestInitializer getCredential()
        {
            return credential;
        }
    }
}
