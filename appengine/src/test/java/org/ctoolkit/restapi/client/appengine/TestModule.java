/*
 * Copyright (c) 2018 Comvai, s.r.o. All Rights Reserved.
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

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import ma.glasnost.orika.MapperFactory;
import org.ctoolkit.restapi.client.ApiCredential;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.DownloadExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UnderlyingClientAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.adapter.BeanMapperConfig;
import org.ctoolkit.restapi.client.appengine.adapter.BeeGetListAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooClientAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooDeleteAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooGetAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooInsertAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooListAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooNewAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooUpdateAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.RemoteOnlyAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.model.Bee;
import org.ctoolkit.restapi.client.appengine.adapter.model.Foo;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteBee;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteFoo;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteOnly;
import org.ctoolkit.restapi.client.appengine.adapter.model.UnderlyingClient;
import org.ctoolkit.restapi.client.provider.AuthKeyProvider;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

/**
 * The guice module configuration for testing purpose only.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class TestModule
        extends AbstractModule
{
    @Override
    protected void configure()
    {
        install( new CtoolkitRestFacadeAppEngineModule() );
        install( new CtoolkitRestFacadeDefaultOrikaModule() );

        bind( AuthKeyProvider.class ).to( MyAuthKeyProvider.class ).in( Singleton.class );

        // Foo adaptee mapping per type
        bind( new TypeLiteral<NewExecutorAdaptee<RemoteFoo>>()
        {
        } ).to( FooNewAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<GetExecutorAdaptee<RemoteFoo>>()
        {
        } ).to( FooGetAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DownloadExecutorAdaptee<RemoteFoo>>()
        {
        } ).to( FooGetAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<ListExecutorAdaptee<RemoteFoo>>()
        {
        } ).to( FooListAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<RemoteFoo>>()
        {
        } ).to( FooInsertAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<RemoteFoo>>()
        {
        } ).to( FooUpdateAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UnderlyingClientAdaptee<UnderlyingClient>>()
        {
        } ).to( FooClientAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<RemoteFoo>>()
        {
        } ).to( FooDeleteAdaptee.class ).in( Singleton.class );

        // Bee adaptee mapping
        bind( new TypeLiteral<GetExecutorAdaptee<RemoteBee>>()
        {
        } ).to( BeeGetListAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<ListExecutorAdaptee<RemoteBee>>()
        {
        } ).to( BeeGetListAdaptee.class ).in( Singleton.class );

        // RemoteOnly adaptee mapping, no local resource counterpart
        bind( new TypeLiteral<GetExecutorAdaptee<RemoteOnly>>()
        {
        } ).to( RemoteOnlyAdaptee.class ).in( Singleton.class );


        // default credential configuration
        ApiCredential credential = new ApiCredential();
        credential.setProjectId( "appid-103" );
        credential.setClientId( "4top4.apps.googleusercontent.com" );
        credential.setScopes( "https://www.googleapis.com/auth/drive,https://www.googleapis.com/auth/drive.metadata" );
        credential.setDisableGZipContent( false );
        credential.setServiceAccountEmail( "service.account@cloud.com" );
        credential.setFileName( "/org/ctoolkit/restapi/private-key.p12" );
        credential.setFileNameJson( "/org/ctoolkit/restapi/private-key.json" );
        credential.setApiKey( "AIzaSz" );
        credential.setEndpointUrl( "http://localhost:8990/_ah/api/" );
        credential.setCredentialOn( true );
        credential.setNumberOfRetries( 3 );
        credential.setRequestReadTimeout( 15000 );
        credential.load( "/credential.properties" );

        Names.bindProperties( binder(), credential );

        Multibinder<BeanMapperConfig> multi = Multibinder.newSetBinder( binder(), BeanMapperConfig.class );
        multi.addBinding().to( InnerBeanMapperConfig.class );
    }

    @Provides
    HttpServletRequest provideHttpServletRequest()
    {
        throw new UnsupportedOperationException( "Not implemented as not needed yet" );
    }

    static class MyAuthKeyProvider
            implements AuthKeyProvider
    {
        @Override
        public InputStream get( @Nullable String prefix )
        {
            return null;
        }

        @Override
        public boolean isConfigured( @Nullable String prefix )
        {
            return false;
        }
    }

    private static class InnerBeanMapperConfig
            implements BeanMapperConfig
    {
        @Override
        public void config( MapperFactory factory )
        {
            factory.classMap( Foo.class, RemoteFoo.class ).byDefault().register();
            factory.classMap( Bee.class, RemoteBee.class ).byDefault().register();
        }
    }
}
