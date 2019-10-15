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

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.Collection;

/**
 * The manager that helps to provide an underlying API client instance either with default or specific configuration.
 * A standard use case is to provide default client configuration.
 * Once {@link #init(Collection, String)} has been called, a newly built client instance
 * with stated configuration will be valid as long as the thread is alive.
 *
 * @param <C> the concrete type of API client to be managed
 */
public interface ClientApi<C>
        extends Provider<C>
{
    /**
     * Initialize a client API instance with specified parameters and sets
     * that instance in to thread local to be consumed in current thread.
     *
     * @param scopes    the scopes for use with API
     * @param userEmail the email address of the user to impersonate
     * @return the just initialized API client instance
     */
    C init( @Nullable Collection<String> scopes, @Nullable String userEmail );
}
