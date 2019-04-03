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
import org.ctoolkit.restapi.client.ServiceUnavailableException;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Provider;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The manager that helps to provide an underlying API client instance either with default or specific configuration.
 * A standard use case is to provide default client configuration.
 * Once {@link #init(Collection, String)} has been called, a newly built client instance
 * with stated configuration will be valid while current thread is in use.
 *
 * @param <C> the concrete type of API client to be managed
 */
public abstract class ClientApiProvider<C>
        implements Provider<C>
{
    private static final Logger LOGGER = LoggerFactory.getLogger( ClientApiProvider.class );

    private final GoogleApiProxyFactory factory;

    private ThreadLocal<C> threadLocal = new ThreadLocal<>();

    private AtomicReference<C> defaultClient = new AtomicReference<>();

    public ClientApiProvider( GoogleApiProxyFactory factory )
    {
        this.factory = factory;
        this.factory.put( apiPrefix(), this );
        defaultClient.set( init( defaultScopes(), null ) );
    }

    @Override
    public C get()
    {
        C client = threadLocal.get();
        return client == null ? defaultClient.get() : client;
    }


    /**
     * Initialize a client API instance with specified parameters and sets
     * that instance in to thread local to be consumed in current thread.
     *
     * @param scopes    the scopes for use with API
     * @param userEmail the email address of the user to impersonate
     * @return the just initialized API client instance
     */
    C init( @Nonnull Collection<String> scopes, @Nullable String userEmail )
    {
        String prefix = checkNotNull( apiPrefix(), "API short name is mandatory" );
        String applicationName = factory.getApplicationName( prefix );
        String serviceAccountEmail = factory.getServiceAccountEmail( prefix );

        try
        {
            HttpRequestInitializer credential = factory.authorize( scopes, userEmail, prefix );

            C client = build( factory,
                    factory.getHttpTransport(),
                    factory.getJsonFactory(),
                    credential,
                    prefix );

            threadLocal.set( client );
            return client;
        }
        catch ( GeneralSecurityException e )
        {
            LOGGER.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + applicationName
                    + " Service account: " + serviceAccountEmail, e );
            throw new UnauthorizedException( e.getMessage() );
        }
        catch ( IOException e )
        {
            LOGGER.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + applicationName
                    + " Service account: " + serviceAccountEmail, e );

            throw new ServiceUnavailableException( e.getMessage() );
        }
    }

    /**
     * The list of API scopes to be used to initialize default API client instance.
     *
     * @return the list of API default scopes
     */
    protected abstract Collection<String> defaultScopes();

    /**
     * The the short name of this API.
     *
     * @return the short API name
     */
    protected abstract String apiPrefix();

    /**
     * Builds the API specific client instance.
     *
     * @param factory     API proxy factory
     * @param transport   the singleton instance of the HTTP transport
     * @param jsonFactory the singleton instance of the JSON factory
     * @param credential  the initialized API credential
     * @param api         the short name of this API
     * @return the newly built client instance
     */
    protected abstract C build( @Nonnull GoogleApiProxyFactory factory,
                                @Nonnull HttpTransport transport,
                                @Nonnull JsonFactory jsonFactory,
                                @Nonnull HttpRequestInitializer credential,
                                @Nonnull String api );
}
