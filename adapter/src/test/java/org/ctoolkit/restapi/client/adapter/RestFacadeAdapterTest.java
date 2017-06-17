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

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.DownloadExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UnderlyingExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Unit tests to test {@link RestFacadeAdapter}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@SuppressWarnings( {"unchecked", "ConstantConditions"} )
public class RestFacadeAdapterTest
{
    @Tested
    private RestFacadeAdapter tested;

    @Injectable
    private MapperFacade mapper;

    @Injectable
    private MapperFactory factory;

    @Injectable
    private ResourceProviderInjector injector;

    @Injectable
    private GoogleApiProxyFactory apiFactory;

    @Test
    public void noResourceMappingNewInstance( @Mocked final NewExecutorAdaptee adaptee,
                                              @Mocked final RemoteRequest request,
                                              @Mocked final ResourceNoMapping resource )
            throws IOException
    {
        new Expectations()
        {
            {
                injector.getExecutorAdaptee( NewExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareNew( anyString, ( Map<String, Object> ) any, ( Locale ) any );
                result = request;

                adaptee.executeNew( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = resource;
            }
        };

        tested.newInstance( ResourceNoMapping.class ).finish();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingGet( @Mocked final GetExecutorAdaptee adaptee,
                                      @Mocked final RemoteRequest request,
                                      @Mocked final ResourceNoMapping resource )
            throws IOException
    {
        new Expectations()
        {
            {
                injector.getExecutorAdaptee( GetExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareGet( ( Identifier ) any );
                result = request;

                injector.getExistingResourceProvider( ( Class<Object> ) any );
                result = null;

                adaptee.executeGet( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = resource;
            }
        };

        tested.get( ResourceNoMapping.class, new Identifier( 1L ) ).finish();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingEmptyResponseList( @Mocked final ListExecutorAdaptee adaptee,
                                                    @Mocked final RemoteRequest request )
            throws IOException
    {
        new Expectations()
        {
            {
                injector.getExecutorAdaptee( ListExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareList( ( Identifier ) any );
                result = request;

                adaptee.executeList( any, ( Map<String, Object> ) any, ( Locale ) any, -1, -1 );
                result = new ArrayList<>();

                // returns null to make sure no provider is being injected
                injector.getExistingResourceProvider( ( Class ) any );
                result = null;
            }
        };

        tested.list( ResourceNoMapping.class ).finish();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingList( @Mocked final ListExecutorAdaptee adaptee,
                                       @Mocked final RemoteRequest request,
                                       @Mocked final ResourceNoMapping responseResource )
            throws IOException
    {
        final List<ResourceNoMapping> resources = new ArrayList<>();
        resources.add( responseResource );

        new Expectations()
        {
            {
                injector.getExecutorAdaptee( ListExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareList( ( Identifier ) any );
                result = request;

                adaptee.executeList( any, ( Map<String, Object> ) any, ( Locale ) any, -1, -1 );
                result = resources;

                // returns null to make sure no provider is being injected
                injector.getExistingResourceProvider( ( Class ) any );
                result = null;
            }
        };

        tested.list( ResourceNoMapping.class ).finish();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingInsert( @Mocked final InsertExecutorAdaptee adaptee,
                                         @Mocked final RemoteRequest request,
                                         @Mocked final ResourceNoMapping inputResource,
                                         @Mocked final ResourceNoMapping responseResource )
            throws IOException
    {
        new Expectations()
        {
            {
                injector.getExecutorAdaptee( InsertExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareInsert( inputResource, ( Identifier ) any, ( MediaProvider ) any );
                result = request;

                adaptee.executeInsert( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = responseResource;
            }
        };

        tested.insert( inputResource, new Identifier( 1L ) ).finish();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingUpdate( @Mocked final UpdateExecutorAdaptee adaptee,
                                         @Mocked final RemoteRequest request,
                                         @Mocked final ResourceNoMapping inputResource,
                                         @Mocked final ResourceNoMapping responseResource )
            throws IOException
    {
        new Expectations()
        {
            {
                injector.getExecutorAdaptee( UpdateExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareUpdate( inputResource, ( Identifier ) any, ( MediaProvider ) any );
                result = request;

                adaptee.executeUpdate( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = responseResource;
            }
        };

        tested.update( inputResource, new Identifier( 1L ) ).finish();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingUnderlying( @Mocked final UnderlyingExecutorAdaptee adaptee,
                                             @Mocked final RemoteRequest request,
                                             @Mocked final ResourceNoMapping responseResource )
            throws IOException
    {
        new Expectations()
        {
            {
                injector.getExecutorAdaptee( UnderlyingExecutorAdaptee.class, UnderlyingRequest.class );
                result = adaptee;

                adaptee.prepareUnderlying( any, ( Identifier ) any, ( Map<String, Object> ) any );
                result = request;

                adaptee.executeUnderlying( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = responseResource;
            }
        };

        tested.underlying( UnderlyingRequest.class ).answerBy( ResourceNoMapping.class ).finish();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingDelete( @Mocked final DeleteExecutorAdaptee adaptee,
                                         @Mocked final RemoteRequest request )
            throws IOException
    {
        new Expectations()
        {
            {
                injector.getExecutorAdaptee( DeleteExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareDelete( ( Identifier ) any );
                result = request;
            }
        };

        tested.delete( ResourceNoMapping.class, new Identifier( 1L ) ).finish();

        new NoMappingVerifications();
    }

    @Test
    public void insertReturnsNoContent( @Mocked final InsertExecutorAdaptee adaptee,
                                        @Mocked final RemoteRequest request,
                                        @Mocked final ResourceNoMapping inputResource )
            throws IOException
    {
        new Expectations()
        {
            {
                injector.getExecutorAdaptee( InsertExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareInsert( inputResource, ( Identifier ) any, ( MediaProvider ) any );
                result = request;

                adaptee.executeInsert( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = null;
            }
        };

        assertNull( tested.insert( inputResource, new Identifier( 1L ) ).finish() );
    }

    @Test
    public void updateReturnsNoContent( @Mocked final UpdateExecutorAdaptee adaptee,
                                        @Mocked final RemoteRequest request,
                                        @Mocked final ResourceNoMapping inputResource )
            throws IOException
    {
        new Expectations()
        {
            {
                injector.getExecutorAdaptee( UpdateExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareUpdate( inputResource, ( Identifier ) any, ( MediaProvider ) any );
                result = request;

                adaptee.executeUpdate( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = null;
            }
        };

        assertNull( tested.update( inputResource, new Identifier( 1L ) ).finish() );
    }

    @Test
    public void underlyingReturnsNoContent( @Mocked final UnderlyingExecutorAdaptee adaptee,
                                            @Mocked final RemoteRequest request,
                                            @Mocked final ResourceNoMapping inputResource )
            throws IOException
    {
        new Expectations()
        {
            {
                injector.getExecutorAdaptee( UnderlyingExecutorAdaptee.class, UnderlyingRequest.class );
                result = adaptee;

                adaptee.prepareUnderlying( any, ( Identifier ) any, ( Map<String, Object> ) any );
                result = request;

                adaptee.executeUnderlying( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = null;
            }
        };

        assertNull( tested.underlying( UnderlyingRequest.class ).withPayload( inputResource ).finish() );
    }

    @Test
    public void prepareDownloadRequest( @Mocked final DownloadExecutorAdaptee adaptee )
            throws GeneralSecurityException, IOException
    {
        final String prefix = "myapi";

        new Expectations()
        {
            {
                injector.getExecutorAdaptee( DownloadExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.getApiPrefix();
                result = prefix;
            }
        };

        final ByteArrayOutputStream content = new ByteArrayOutputStream();
        final Identifier id = new Identifier( 1L );

        DownloadRequest request = tested.prepareDownloadRequest( ResourceNoMapping.class, id, content, null );
        assertNotNull( request );

        new Verifications()
        {
            {
                apiFactory.newRequestConfig( prefix );
                times = 1;

                apiFactory.getHttpTransport();
                times = 1;
            }
        };
    }

    @Test( expectedExceptions = IllegalArgumentException.class )
    public void executeDownloadNullUrl( @Mocked final MediaHttpDownloader downloader,
                                        @Mocked final DownloadExecutorAdaptee adaptee )
            throws IOException
    {
        new Expectations()
        {
            {
                adaptee.prepareDownloadUrl( ( Identifier ) any, anyString, ( Map<String, Object> ) any, ( Locale ) any );
                result = null;
            }
        };

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        Identifier id = new Identifier( 1L );

        tested.executeDownload( downloader, adaptee, ResourceNoMapping.class, id, content, null, null, null );
    }

    @Test( expectedExceptions = RuntimeException.class )
    public void executeDownloadException( @Mocked final MediaHttpDownloader downloader,
                                          @Mocked final DownloadExecutorAdaptee adaptee )
            throws IOException
    {
        final URL url = new URL( "https://www.ctoolkit.org/download" );

        new Expectations()
        {
            {
                adaptee.prepareDownloadUrl( ( Identifier ) any, anyString, ( Map<String, Object> ) any, ( Locale ) any );
                result = url;

                downloader.download( ( GenericUrl ) any, ( HttpHeaders ) any, ( OutputStream ) any );
                result = new IOException();
            }
        };

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        final Identifier id = new Identifier( 1L );

        tested.executeDownload( downloader, adaptee, ResourceNoMapping.class, id, content, null, null, null );
    }

    @Test
    public void executeDownload( @Mocked final MediaHttpDownloader downloader,
                                 @Mocked final DownloadExecutorAdaptee adaptee )
            throws IOException
    {
        final Identifier id = new Identifier( 1L );
        // expected content type
        final String type = "application/pdf";
        final Locale locale = Locale.GERMANY;
        final URL url = new URL( "https://www.ctoolkit.org/download" );
        final HttpHeaders headers = new HttpHeaders();
        final Map<String, Object> params = new HashMap<>();

        //noinspection MismatchedQueryAndUpdateOfCollection
        RequestCredential credential = new RequestCredential();
        credential.setApiKey( "SecretABC" );
        credential.populate( params );

        new Expectations( tested )
        {
            {
                adaptee.prepareDownloadUrl( id, type, params, locale );
                result = url;

                tested.createHttpHeaders();
                result = headers;
            }
        };

        final ByteArrayOutputStream content = new ByteArrayOutputStream();
        tested.executeDownload( downloader, adaptee, ResourceNoMapping.class, id, content, type, params, locale );

        String languageTag = ( String ) headers.get( com.google.common.net.HttpHeaders.ACCEPT_LANGUAGE );
        assertEquals( languageTag, "de-DE", "Accept Language" );

        String contentType = headers.getContentType();
        assertEquals( contentType, type, "Content Type" );

        String authorization = headers.getAuthorization();
        assertEquals( authorization, "SecretABC", "Authorization" );

        new Verifications()
        {
            {
                downloader.download( new GenericUrl( url ), headers, content );
                times = 1;
            }
        };
    }

    @SuppressWarnings( "ConstantConditions" )
    private class NoMappingVerifications
            extends Verifications
    {
        {
            // make sure no mapping is being invoked
            mapper.map( any, ( Class ) any );
            times = 0;

            mapper.map( any, ( Class ) any, ( MappingContext ) any );
            times = 0;

            mapper.map( any, any );
            times = 0;

            mapper.map( any, any, ( MappingContext ) any );
            times = 0;

            mapper.mapAsList( ( List ) any, ( Class ) any );
            times = 0;

            mapper.mapAsList( ( List ) any, ( Class ) any, ( MappingContext ) any );
            times = 0;
        }
    }
}