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

import org.ctoolkit.restapi.client.ApiCredential;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.ResourceFacade;
import org.ctoolkit.restapi.client.SingleRequest;
import org.ctoolkit.restapi.client.appengine.GuiceTestCase;
import org.ctoolkit.restapi.client.appengine.adapter.model.Foo;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteOnly;
import org.ctoolkit.restapi.client.appengine.adapter.model.UnderlyingRequest;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * The adapter running on local AppEngine.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class AdapterAppEngineTest
        extends GuiceTestCase
{
    @Inject
    @Named( "credential.default.projectId" )
    String projectId;

    @Inject
    @Named( "credential.default.clientId" )
    String clientId;

    @Inject
    @Named( "credential.default.serviceAccountEmail" )
    String serviceAccountEmail;

    @Inject
    @Named( "credential.default.fileName" )
    String fileName;

    @Inject
    @Named( "credential.default.apiKey" )
    String apiKey;

    @Inject
    @Named( "credential.default.endpointUrl" )
    String endpointUrl;

    @Inject
    @Named( "credential.default.credentialOn" )
    String credentialOn;

    @Inject
    @Named( "credential.default.numberOfRetries" )
    String numberOfRetries;

    @Inject
    @Named( "credential.default.readTimeout" )
    String readTimeout;

    @Inject
    private ResourceFacade resources;

    @Inject
    private GoogleApiProxyFactory builder;

    @Test
    public void facadeEndToEnd() throws IOException
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

        singleRequest = resources.get( Foo.class, 1L );
        assertNotNull( singleRequest );
        assertNotNull( singleRequest.execute() );

        singleRequest = resources.get( Foo.class, "identifier" );
        assertNotNull( singleRequest );
        assertNotNull( singleRequest.execute() );

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        String type = "application/pdf";
        SingleRequest dr = resources.media( Foo.class ).downloadTo( content, type ).identifiedBy( 1L );
        assertNull( dr.execute( Locale.GERMANY ) );
        String errorMessage = "Output stream has expected to be populated by downloaded content!";
        assertTrue( content.size() > 0, errorMessage );

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

        singleRequest = resources.update( foo, 1L );
        assertNotNull( singleRequest );
        assertNotNull( singleRequest.execute() );

        singleRequest = resources.update( foo, "identifier" );
        assertNotNull( singleRequest );
        assertNotNull( singleRequest.execute() );

        Foo.InnerFoo inner = new Foo.InnerFoo();
        singleRequest = resources.patch( inner, new Identifier( 1L ) );
        assertNotNull( singleRequest );
        assertNotNull( singleRequest.execute() );

        resources.patch( UnderlyingRequest.class ).identifier( new Identifier( 1L ) ).build().export().execute();

        singleRequest = resources.delete( Foo.class, new Identifier( 1L ) );
        assertNotNull( singleRequest );
        singleRequest.execute();

        singleRequest = resources.delete( Foo.class, 1L );
        assertNotNull( singleRequest );
        singleRequest.execute();

        singleRequest = resources.delete( Foo.class, "identifier" );
        assertNotNull( singleRequest );
        singleRequest.execute();
    }

    @Test
    public void defaultCredentialConfig()
    {
        String projectId = builder.getProjectId( null );
        assertEquals( projectId, this.projectId );

        String clientId = builder.getClientId( null );
        assertEquals( clientId, this.clientId );

        String serviceEmail = builder.getServiceAccountEmail( null );
        assertEquals( serviceEmail, this.serviceAccountEmail );

        // default app name test, not configured by client
        String applicationName = builder.getApplicationName( null );
        assertEquals( applicationName, ApiCredential.DEFAULT_APP_NAME );

        String fileName = builder.getFileName( null );
        assertEquals( fileName, this.fileName );

        String apiKey = builder.getApiKey( null );
        assertEquals( apiKey, this.apiKey );

        String endpointUrl = builder.getEndpointUrl( null );
        assertEquals( endpointUrl, this.endpointUrl );

        boolean credentialOn = builder.isCredentialOn( null );
        assertEquals( credentialOn, Boolean.valueOf( this.credentialOn ).booleanValue() );

        int numberOfRetries = builder.getNumberOfRetries( null );
        assertEquals( numberOfRetries, Integer.valueOf( this.numberOfRetries ).intValue() );

        int readTimeout = builder.getReadTimeout( null );
        assertEquals( readTimeout, Integer.valueOf( this.readTimeout ).intValue() );
    }

    @Test
    public void specificCredentialConfig()
    {
        String prefix = "drive";

        String projectId = builder.getProjectId( prefix );
        assertEquals( projectId, "appid-3900" );

        String clientId = builder.getClientId( prefix );
        assertEquals( clientId, "clientId.apps.googleusercontent.com" );

        String serviceEmail = builder.getServiceAccountEmail( prefix );
        assertEquals( serviceEmail, "service.account@googleusercontent.com" );

        String applicationName = builder.getApplicationName( prefix );
        assertEquals( applicationName, "puf-muf" );

        String fileName = builder.getFileName( prefix );
        assertEquals( fileName, "/org/ctoolkit/restapi/key.json" );

        String apiKey = builder.getApiKey( prefix );
        assertEquals( apiKey, "AIzaSzXYbn" );

        String endpointUrl = builder.getEndpointUrl( prefix );
        assertEquals( endpointUrl, "http://drive.localhost:8990/_ah/api/" );

        boolean credentialOn = builder.isCredentialOn( prefix );
        assertEquals( credentialOn, false );

        int numberOfRetries = builder.getNumberOfRetries( prefix );
        assertEquals( numberOfRetries, 2 );

        int readTimeout = builder.getReadTimeout( prefix );
        assertEquals( readTimeout, 45000 );
    }

    @Test
    public void specificCredentialFallInDefault()
    {
        String prefix = "none";

        String projectId = builder.getProjectId( prefix );
        assertEquals( projectId, this.projectId );

        String clientId = builder.getClientId( prefix );
        assertEquals( clientId, this.clientId );

        String serviceEmail = builder.getServiceAccountEmail( prefix );
        assertEquals( serviceEmail, this.serviceAccountEmail );

        String applicationName = builder.getApplicationName( prefix );
        assertEquals( applicationName, ApiCredential.DEFAULT_APP_NAME );

        String fileName = builder.getFileName( prefix );
        assertEquals( fileName, this.fileName );

        String apiKey = builder.getApiKey( prefix );
        assertEquals( apiKey, this.apiKey );

        String endpointUrl = builder.getEndpointUrl( prefix );
        assertEquals( endpointUrl, this.endpointUrl );

        boolean credentialOn = builder.isCredentialOn( prefix );
        assertEquals( credentialOn, Boolean.valueOf( this.credentialOn ).booleanValue() );

        int numberOfRetries = builder.getNumberOfRetries( prefix );
        assertEquals( numberOfRetries, Integer.valueOf( this.numberOfRetries ).intValue() );

        int readTimeout = builder.getReadTimeout( prefix );
        assertEquals( readTimeout, Integer.valueOf( this.readTimeout ).intValue() );
    }
}
