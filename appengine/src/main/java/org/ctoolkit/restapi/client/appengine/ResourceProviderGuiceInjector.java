/*
 * Copyright (c) 2015 Comvai, s.r.o. All Rights Reserved.
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

package org.ctoolkit.restapi.client.appengine;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import org.ctoolkit.restapi.client.LocalResourceProvider;
import org.ctoolkit.restapi.client.adapter.ResourceProviderInjector;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;

/**
 * The Guice implementation of the injector.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class ResourceProviderGuiceInjector
        implements ResourceProviderInjector
{
    private final Injector injector;

    @Inject
    public ResourceProviderGuiceInjector( Injector injector )
    {
        this.injector = injector;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <T> LocalResourceProvider<T> getExistingResourceProvider( @Nonnull Class<T> resource )
    {
        LocalResourceProvider<T> provider = null;

        ParameterizedType pt = Types.newParameterizedType( LocalResourceProvider.class, resource );
        Binding<?> binding = injector.getExistingBinding( Key.get( TypeLiteral.get( pt ) ) );

        if ( binding != null )
        {
            provider = ( LocalResourceProvider<T> ) binding.getProvider().get();
        }

        return provider;
    }
}
