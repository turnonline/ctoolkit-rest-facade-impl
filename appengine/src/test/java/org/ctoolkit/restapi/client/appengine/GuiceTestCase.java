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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.testing.TearDown;
import com.google.guiceberry.GuiceBerryModule;
import com.google.guiceberry.testng.TestNgGuiceBerry;
import com.google.inject.name.Names;
import org.ctoolkit.restapi.client.ApiCredential;
import org.ctoolkit.test.appengine.ServiceConfigModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @author <a href="mailto:medvegy@comvai.com">Aurel Medvegy</a>
 */
public class GuiceTestCase
        extends ServiceConfigModule
{
    private TearDown toTearDown;

    public GuiceTestCase()
    {
        construct( new LocalServiceTestHelper( new LocalMemcacheServiceTestConfig(),
                new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage( 0 ) ) );
    }

    @BeforeMethod
    public void setUp( Method m )
    {
        // Make this the call to TestNgGuiceBerry.setUp as early as possible
        toTearDown = TestNgGuiceBerry.setUp( this, m, GuiceTestCase.class );
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

        // default credential configuration
        ApiCredential credential = new ApiCredential();
        credential.setProjectId( "appid-103" );
        credential.setClientId( "4top4.apps.googleusercontent.com" );
        credential.setServiceAccountEmail( "service.account@cloud.com" );
        credential.setFileName( "/org/ctoolkit/restapi/private-key.p12" );
        credential.setApiKey( "AIzaSz" );
        credential.setEndpointUrl( "http://localhost:8990/_ah/api/" );
        credential.setCredentialOn( true );
        credential.setNumberOfRetries( 3 );
        credential.setRequestReadTimeout( 15000 );

        InputStream stream = GuiceTestCase.class.getResourceAsStream( "credential.properties" );
        Properties drive = new Properties();
        try
        {
            drive.load( stream );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        Names.bindProperties( binder(), credential );
        Names.bindProperties( binder(), drive );

        install( new FacadeAppEngineModule() );
        install( new GuiceBerryModule() );
        install( new AdapterAppEngineModule() );
    }
}
