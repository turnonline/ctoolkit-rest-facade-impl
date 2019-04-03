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

package org.ctoolkit.restapi.client.appengine.adapter;

import org.ctoolkit.restapi.client.ApiCredential;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.PayloadRequest;
import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RestFacade;
import org.ctoolkit.restapi.client.RetrievalRequest;
import org.ctoolkit.restapi.client.adapter.GoogleApiProxyFactory;
import org.ctoolkit.restapi.client.appengine.BackendServiceTestCase;
import org.ctoolkit.restapi.client.appengine.adapter.model.Foo;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteOnly;
import org.ctoolkit.restapi.client.appengine.adapter.model.UnderlyingClient;
import org.testng.annotations.Test;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * The adapter running on local AppEngine.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class AdapterAppEngineTest
        extends BackendServiceTestCase
{
    @Inject
    @Named( "credential.default.projectId" )
    String projectId;

    @Inject
    @Named( "credential.default.clientId" )
    String clientId;

    @Inject
    @Named( "credential.default.scopes" )
    String scopes;

    @Inject
    @Named( "credential.default.disableGZipContent" )
    String disableGZipContent;

    @Inject
    @Named( "credential.default.serviceAccountEmail" )
    String serviceAccountEmail;

    @Inject
    @Named( "credential.default.fileName" )
    String fileName;

    @Inject
    @Named( "credential.default.fileNameJson" )
    String fileNameJson;

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
    private RestFacade resources;

    @Inject
    private GoogleApiProxyFactory builder;

    @Test
    public void facadeEndToEnd() throws IOException
    {
        RetrievalRequest<RemoteOnly> request = resources.get( RemoteOnly.class ).identifiedBy( new Identifier( 1L ) );
        assertNotNull( request );
        assertNotNull( request.finish() );

        PayloadRequest<Foo> payloadRequest = resources.newInstance( Foo.class );
        assertNotNull( payloadRequest );
        assertNotNull( payloadRequest.finish() );

        RetrievalRequest singleRetrievalRequest = resources.get( Foo.class ).identifiedBy( new Identifier( 1L ) );
        assertNotNull( singleRetrievalRequest );
        assertNotNull( singleRetrievalRequest.finish() );

        singleRetrievalRequest = resources.get( Foo.class ).identifiedBy( 1L );
        assertNotNull( singleRetrievalRequest );
        assertNotNull( singleRetrievalRequest.finish() );

        singleRetrievalRequest = resources.get( Foo.class ).identifiedBy( "identifier" );
        assertNotNull( singleRetrievalRequest );
        assertNotNull( singleRetrievalRequest.finish() );

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        String type = "application/pdf";
        Request dr = resources.download( Foo.class ).to( content ).ofType( type ).identifiedBy( 1L );
        assertNotNull( dr.finish( Locale.GERMANY ) );
        String errorMessage = "Output stream has expected to be populated by downloaded content!";
        assertTrue( content.size() > 0, errorMessage );

        Foo foo = new Foo();
        foo.setName( "John Foo" );
        payloadRequest = resources.insert( foo );
        assertNotNull( payloadRequest );
        assertNotNull( payloadRequest.finish() );

        foo = new Foo();
        foo.setName( "Michal Foo" );
        payloadRequest = resources.update( foo ).identifiedBy( new Identifier( 1L ) );
        assertNotNull( payloadRequest );
        assertNotNull( payloadRequest.finish() );

        payloadRequest = resources.update( foo ).identifiedBy( 1L );
        assertNotNull( payloadRequest );
        assertNotNull( payloadRequest.finish() );

        payloadRequest = resources.update( foo ).identifiedBy( "identifier" );
        assertNotNull( payloadRequest );
        assertNotNull( payloadRequest.finish() );

        resources.client( UnderlyingClient.class ).export().execute();

        PayloadRequest<Foo> deleteRequest = resources.delete( Foo.class ).identifiedBy( new Identifier( 1L ) );
        assertNotNull( deleteRequest );
        deleteRequest.finish();

        deleteRequest = resources.delete( Foo.class ).identifiedBy( 1L );
        assertNotNull( deleteRequest );
        deleteRequest.finish();

        deleteRequest = resources.delete( Foo.class ).identifiedBy( "identifier" );
        assertNotNull( deleteRequest );
        deleteRequest.finish();
    }

    @Test
    public void defaultCredentialConfig()
    {
        String projectId = builder.getProjectId( null );
        assertEquals( projectId, this.projectId );

        String clientId = builder.getClientId( null );
        assertEquals( clientId, this.clientId );

        List<String> scopes = builder.getScopes( null );
        assertThat( scopes )
                .containsAllOf( "https://www.googleapis.com/auth/drive",
                        "https://www.googleapis.com/auth/drive.metadata" );

        boolean disableGZipContent = builder.isDisableGZipContent( null );
        assertEquals( disableGZipContent, Boolean.valueOf( this.disableGZipContent ).booleanValue() );

        String serviceEmail = builder.getServiceAccountEmail( null );
        assertEquals( serviceEmail, this.serviceAccountEmail );

        // default app name test, not configured by client
        String applicationName = builder.getApplicationName( null );
        assertEquals( applicationName, ApiCredential.DEFAULT_APP_NAME );

        String fileName = builder.getFileName( null );
        assertEquals( fileName, this.fileName );

        assertTrue( builder.isJsonConfiguration( null ) );

        String fileNameJson = builder.getFileNameJson( null );
        assertEquals( fileNameJson, this.fileNameJson );

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

        List<String> scopes = builder.getScopes( prefix );
        assertThat( scopes )
                .containsAllOf( "https://www.googleapis.com/auth/drive",
                        "https://www.googleapis.com/auth/drive.metadata" );

        boolean disableGZipContent = builder.isDisableGZipContent( prefix );
        assertTrue( disableGZipContent );

        String serviceEmail = builder.getServiceAccountEmail( prefix );
        assertEquals( serviceEmail, "service.account@googleusercontent.com" );

        String applicationName = builder.getApplicationName( prefix );
        assertEquals( applicationName, "puf-muf" );

        String fileName = builder.getFileName( prefix );
        assertEquals( fileName, "/org/ctoolkit/restapi/key.p12" );

        assertTrue( builder.isJsonConfiguration( prefix ) );

        String fileNameJson = builder.getFileNameJson( prefix );
        assertEquals( fileNameJson, "/org/ctoolkit/restapi/key.json" );

        String apiKey = builder.getApiKey( prefix );
        assertEquals( apiKey, "AIzaSzXYbn" );

        String endpointUrl = builder.getEndpointUrl( prefix );
        assertEquals( endpointUrl, "http://drive.localhost:8990/_ah/api/" );

        boolean credentialOn = builder.isCredentialOn( prefix );
        assertFalse( credentialOn );

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

        List<String> scopes = builder.getScopes( prefix );
        assertThat( scopes )
                .containsAllOf( "https://www.googleapis.com/auth/drive",
                        "https://www.googleapis.com/auth/drive.metadata" );

        boolean disableGZipContent = builder.isDisableGZipContent( prefix );
        assertEquals( disableGZipContent, Boolean.valueOf( this.disableGZipContent ).booleanValue() );

        String serviceEmail = builder.getServiceAccountEmail( prefix );
        assertEquals( serviceEmail, this.serviceAccountEmail );

        String applicationName = builder.getApplicationName( prefix );
        assertEquals( applicationName, ApiCredential.DEFAULT_APP_NAME );

        String fileName = builder.getFileName( prefix );
        assertEquals( fileName, this.fileName );

        assertTrue( builder.isJsonConfiguration( prefix ) );

        String fileNameJson = builder.getFileNameJson( prefix );
        assertEquals( fileNameJson, this.fileNameJson );

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
