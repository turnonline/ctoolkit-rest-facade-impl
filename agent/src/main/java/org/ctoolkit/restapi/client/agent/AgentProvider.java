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

package org.ctoolkit.restapi.client.agent;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.common.base.Strings;
import org.ctoolkit.api.agent.Agent;
import org.ctoolkit.api.agent.AgentScopes;
import org.ctoolkit.restapi.client.adapter.ClientApiProvider;
import org.ctoolkit.restapi.client.adapter.GoogleApiProxyFactory;
import org.ctoolkit.restapi.client.agent.adaptee.CustomizedCtoolkitAgent;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;

import static org.ctoolkit.restapi.client.agent.CtoolkitApiAgentModule.API_PREFIX;

/**
 * The {@link CustomizedCtoolkitAgent} API client implementation.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
class AgentProvider
        extends ClientApiProvider<CustomizedCtoolkitAgent>
{
    @Inject
    AgentProvider( GoogleApiProxyFactory factory )
    {
        super( factory );
    }

    @Override
    protected Collection<String> defaultScopes()
    {
        return AgentScopes.all();
    }

    @Override
    protected String api()
    {
        return API_PREFIX;
    }

    @Override
    protected CustomizedCtoolkitAgent build( @Nonnull GoogleApiProxyFactory factory,
                                             @Nonnull HttpTransport transport,
                                             @Nonnull JsonFactory jsonFactory,
                                             @Nonnull HttpRequestInitializer credential,
                                             @Nonnull String api )
    {
        String applicationName = factory.getApplicationName( api );
        String endpointUrl = factory.getEndpointUrl( api );

        CustomizedCtoolkitAgent.Builder builder = new CustomizedCtoolkitAgent.Builder( transport, jsonFactory, credential );
        builder.setApplicationName( applicationName ).setServicePath( Agent.DEFAULT_SERVICE_PATH );

        if ( !Strings.isNullOrEmpty( endpointUrl ) )
        {
            builder.setRootUrl( endpointUrl );
        }

        return builder.build();
    }
}
