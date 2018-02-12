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

package org.ctoolkit.restapi.client.firebase;

import com.google.common.testing.TearDown;
import com.google.guiceberry.GuiceBerryModule;
import com.google.guiceberry.testng.TestNgGuiceBerry;
import com.google.inject.Provides;
import org.ctoolkit.restapi.client.appengine.CtoolkitRestFacadeAppEngineModule;
import org.ctoolkit.restapi.client.appengine.DefaultOrikaMapperFactoryModule;
import org.ctoolkit.test.appengine.ServiceConfigModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Base guice berry configuration for unit tests fully working with DI.
 *
 * @author <a href="mailto:medvegy@comvai.com">Aurel Medvegy</a>
 */
public class GuiceTestCase
        extends ServiceConfigModule
{
    private TearDown toTearDown;

    public GuiceTestCase()
    {
    }

    @BeforeMethod
    public void setUp( Method m )
    {
        // Make this the call to TestNgGuiceBerry.setUp as early as possible
        toTearDown = TestNgGuiceBerry.setUp( this, m, this.getClass() );
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
        // Make this the call to TestNgGuiceBerry.tearDown as late as possible
        toTearDown.tearDown();
    }

    @Override
    public void configureTestBinder()
    {
        // setting the SystemProperty.Environment.Value.Development
        System.setProperty( "com.google.appengine.runtime.environment", "Development" );

        install( new GuiceBerryModule() );
        install( new DefaultOrikaMapperFactoryModule() );
        install( new CtoolkitRestFacadeAppEngineModule() );
    }

    @Provides
    HttpServletRequest provideHttpServletRequest()
    {
        throw new UnsupportedOperationException( "Not implemented as not needed yet" );
    }
}
