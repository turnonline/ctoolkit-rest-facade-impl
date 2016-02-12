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

package org.ctoolkit.restapi.client.appengine;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.PatchExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.BeeGetListAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooDeleteAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooGetAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooInsertAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooListAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooNewAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooPatchAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.FooUpdateAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.RemoteOnlyAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.model.Bee;
import org.ctoolkit.restapi.client.appengine.adapter.model.Foo;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteBee;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteFoo;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteInnerFoo;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteOnly;

import javax.inject.Singleton;

/**
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class AdapterAppEngineModule
        extends AbstractModule
{
    @Override
    protected void configure()
    {
        // Foo adaptee mapping per type
        bind( new TypeLiteral<NewExecutorAdaptee<RemoteFoo>>()
        {
        } ).to( FooNewAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<GetExecutorAdaptee<RemoteFoo>>()
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

        bind( new TypeLiteral<PatchExecutorAdaptee<RemoteFoo>>()
        {
        } ).to( FooPatchAdaptee.class ).in( Singleton.class );

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
    }

    @Provides
    @Singleton
    MapperFactory provideMapperFactory()
    {
        return new DefaultMapperFactory.Builder()
                .dumpStateOnException( false )
                .mapNulls( false )
                .useBuiltinConverters( true )
                .build();
    }

    @Provides
    @Singleton
    MapperFacade provideMapperFacade( MapperFactory factory )
    {
        factory.classMap( Foo.class, RemoteFoo.class ).byDefault().register();
        factory.classMap( Foo.InnerFoo.class, RemoteInnerFoo.class ).byDefault().register();
        factory.classMap( Bee.class, RemoteBee.class ).byDefault().register();

        return factory.getMapperFacade();
    }
}
