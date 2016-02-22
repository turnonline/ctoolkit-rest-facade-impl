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

package org.ctoolkit.restapi.client.appengine.adapter;

import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.ResourceFacade;
import org.ctoolkit.restapi.client.SingleRequest;
import org.ctoolkit.restapi.client.appengine.GuiceTestCase;
import org.ctoolkit.restapi.client.appengine.adapter.model.Foo;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteOnly;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.testng.Assert.assertNotNull;

/**
 * The adapter running on local AppEngine.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class AdapterAppEngineTest
        extends GuiceTestCase
{
    @Inject
    private ResourceFacade resources;

    @Test
    public void test()
    {
        SingleRequest<RemoteOnly> request = resources.get( RemoteOnly.class, new Identifier( 1L ) );
        assertNotNull( request );
        assertNotNull( request.execute() );

        SingleRequest<Foo> singleRequest = resources.newInstance( Foo.class );
        assertNotNull( singleRequest );
        assertNotNull( singleRequest.execute() );

        singleRequest = resources.get( Foo.class, new Identifier( 1L ) );
        assertNotNull( singleRequest );
        assertNotNull( singleRequest.execute() );

        Foo foo = new Foo();
        foo.setName( "John Foo" );
        singleRequest = resources.insert( foo );
        assertNotNull( singleRequest );
        assertNotNull( singleRequest.execute() );

        foo = new Foo();
        foo.setName( "Michal Foo" );
        singleRequest = resources.update( foo, new Identifier( 1L ) );
        assertNotNull( singleRequest );
        assertNotNull( singleRequest.execute() );

        Foo.InnerFoo inner = new Foo.InnerFoo();
        singleRequest = resources.patch( inner, new Identifier( 1L ) );
        assertNotNull( singleRequest );
        assertNotNull( singleRequest.execute() );

        singleRequest = resources.delete( Foo.class, new Identifier( 1L ) );
        assertNotNull( singleRequest );
        singleRequest.execute();
    }
}
