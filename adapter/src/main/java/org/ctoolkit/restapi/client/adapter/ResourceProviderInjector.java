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

package org.ctoolkit.restapi.client.adapter;

import org.ctoolkit.restapi.client.provider.LocalResourceProvider;

import javax.annotation.Nonnull;

/**
 * The {@link LocalResourceProvider} injector abstraction to shade from the concrete implementation of the injection.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public interface ResourceProviderInjector
{
    /**
     * Returns the binding if it already exists, or null if does not exist.
     *
     * @param resource the type of resource to get
     * @param <T>      the concrete type of the resource
     * @return the resource provider
     */
    <T> LocalResourceProvider<T> getExistingResourceProvider( @Nonnull Class<T> resource );

    /**
     * Returns the binding if it already exists, or null if does not exist.
     *
     * @param adapteeType the adaptee type to get
     * @param resource    the generic type of adaptee to get
     * @return the adaptee implementation for given arguments if any
     */
    <A> A getExecutorAdaptee( @Nonnull Class<A> adapteeType, @Nonnull Class<?> resource );
}
