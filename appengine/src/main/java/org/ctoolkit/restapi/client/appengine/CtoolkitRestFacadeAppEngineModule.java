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

package org.ctoolkit.restapi.client.appengine;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import org.ctoolkit.restapi.client.Credential;
import org.ctoolkit.restapi.client.RestFacade;
import org.ctoolkit.restapi.client.adapter.GoogleApiProxyFactory;
import org.ctoolkit.restapi.client.adapter.RestFacadeAdapter;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The client facade API AppEngine guice module.
 * The module is being required once REST {@link RestFacade} is being used (injected)
 * at the client that is running on App Engine standard environment.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class CtoolkitRestFacadeAppEngineModule
        extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( RestFacade.class ).to( RestFacadeAdapter.class ).in( Singleton.class );
        bind( EventBus.class ).in( Singleton.class );
        bind( GoogleApiProxyFactory.class ).to( GoogleApiProxyFactoryAppEngine.class ).in( Singleton.class );
    }

    @Provides
    @Singleton
    @Credential
    Map<String, String> provideCredentialProperties( Injector injector )
    {
        return new CredentialMap( injector );
    }

    private static class CredentialMap
            implements Map<String, String>
    {
        private final Injector injector;

        CredentialMap( Injector injector )
        {
            this.injector = injector;
        }

        @Override
        public int size()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey( Object property )
        {
            checkNotNull( property );
            if ( Strings.isNullOrEmpty( property.toString() ) )
            {
                return false;
            }

            Key<String> key = Key.get( String.class, Names.named( property.toString() ) );
            Binding<String> binding = injector.getExistingBinding( key );
            return binding != null;
        }

        @Override
        public boolean containsValue( Object value )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public String get( Object property )
        {
            checkNotNull( property );
            if ( Strings.isNullOrEmpty( property.toString() ) )
            {
                return null;
            }

            Key<String> key = Key.get( String.class, Names.named( property.toString() ) );
            Binding<String> binding = injector.getExistingBinding( key );
            return binding == null ? null : binding.getProvider().get();
        }

        @Override
        public String put( String key, String value )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public String remove( Object key )
        {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings( "NullableProblems" )
        @Override
        public void putAll( Map<? extends String, ? extends String> m )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear()
        {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings( "NullableProblems" )
        @Override
        public Set<String> keySet()
        {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings( "NullableProblems" )
        @Override
        public Collection<String> values()
        {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings( "NullableProblems" )
        @Override
        public Set<Entry<String, String>> entrySet()
        {
            throw new UnsupportedOperationException();
        }
    }
}
