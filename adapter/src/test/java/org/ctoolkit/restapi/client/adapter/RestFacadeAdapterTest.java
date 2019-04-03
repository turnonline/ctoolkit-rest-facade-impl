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
import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.ctoolkit.restapi.client.DownloadRequest;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.DownloadExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.provider.LocalResourceProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

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
    private Injector injector;

    @Injectable
    private GoogleApiProxyFactory apiFactory;

    @Mocked
    private AbstractGoogleClient googleClient;

    @Mocked
    private NewExecutorAdaptee newAdaptee;

    @Mocked
    private GetExecutorAdaptee getAdaptee;

    @Mocked
    private ListExecutorAdaptee listAdaptee;

    @Mocked
    private InsertExecutorAdaptee insertAdaptee;

    @Mocked
    private UpdateExecutorAdaptee updateAdaptee;

    @Mocked
    private DeleteExecutorAdaptee deleteAdaptee;

    @Mocked
    private DownloadExecutorAdaptee downloadAdaptee;

    @Mocked
    private MediaHttpDownloader downloader;

    @Mocked
    private DownloadResponseInterceptor interceptor;

    @Mocked
    private ResourceNoMapping inputResource;

    @Mocked
    private ResourceNoMapping responseResource;

    @Mocked
    private LocalResourceProvider provider;

    @Mocked
    private OutputStream output;

    @Mocked
    private HttpContent httpContent;

    @Mocked
    private ClientApiProvider apiProvider;

    private RemoteRequest remoteRequest;

    @BeforeMethod
    public void before()
    {
        remoteRequest = new RemoteRequest( googleClient, "", "", httpContent, ResourceNoMapping.class );
    }

    @Test
    public void noResourceMappingNewInstance()
            throws IOException
    {
        new Expectations()
        {
            {
                tested.getExecutorAdaptee( NewExecutorAdaptee.class, ResourceNoMapping.class );
                result = newAdaptee;

                newAdaptee.prepareNew( anyString );
                result = remoteRequest;

                newAdaptee.executeNew( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = responseResource;
            }
        };

        tested.newInstance( ResourceNoMapping.class ).finish();

        noMappingVerifications();
    }

    @Test
    public void noResourceMappingGet()
            throws IOException
    {
        new Expectations( tested )
        {
            {
                tested.getExecutorAdaptee( GetExecutorAdaptee.class, ResourceNoMapping.class );
                result = getAdaptee;

                getAdaptee.prepareGet( ( Identifier ) any );
                result = remoteRequest;

                tested.getExistingResourceProvider( ( Class<Object> ) any );
                result = null;

                getAdaptee.executeGet( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = responseResource;
            }
        };

        tested.get( ResourceNoMapping.class ).identifiedBy( new Identifier( 1L ) ).finish();

        noMappingVerifications();
    }

    @Test
    public void noResourceMappingEmptyResponseList()
            throws IOException
    {
        new Expectations( tested )
        {
            {
                tested.getExecutorAdaptee( ListExecutorAdaptee.class, ResourceNoMapping.class );
                result = listAdaptee;

                listAdaptee.prepareList( ( Identifier ) any );
                result = remoteRequest;

                listAdaptee.executeList( any, ( Map<String, Object> ) any, ( Locale ) any, -1, -1,
                        anyString, anyBoolean );
                result = new ArrayList<>();

                // returns null to make sure no provider is being injected
                tested.getExistingListResourceProvider( ( Class ) any );
                result = null;
            }
        };

        tested.list( ResourceNoMapping.class ).finish();

        noMappingVerifications();
    }

    @Test
    public void noResourceMappingList()
            throws IOException
    {
        final List<ResourceNoMapping> resources = new ArrayList<>();
        resources.add( responseResource );

        new Expectations( tested )
        {
            {
                tested.getExecutorAdaptee( ListExecutorAdaptee.class, ResourceNoMapping.class );
                result = listAdaptee;

                listAdaptee.prepareList( ( Identifier ) any );
                result = remoteRequest;

                listAdaptee.executeList( any, ( Map<String, Object> ) any, ( Locale ) any, -1, -1,
                        anyString, anyBoolean );
                result = resources;

                // returns null to make sure no provider is being injected
                tested.getExistingListResourceProvider( ( Class ) any );
                result = null;
            }
        };

        tested.list( ResourceNoMapping.class ).finish();

        noMappingVerifications();
    }

    @Test
    public void noResourceMappingInsert()
            throws IOException
    {
        new Expectations()
        {
            {
                tested.getExecutorAdaptee( InsertExecutorAdaptee.class, ResourceNoMapping.class );
                result = insertAdaptee;

                insertAdaptee.prepareInsert( inputResource, ( Identifier ) any, ( MediaProvider ) any );
                result = remoteRequest;

                insertAdaptee.executeInsert( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = responseResource;
            }
        };

        tested.insert( inputResource, new Identifier( 1L ) ).finish();

        noMappingVerifications();
    }

    @Test
    public void noResourceMappingUpdate()
            throws IOException
    {
        new Expectations()
        {
            {
                tested.getExecutorAdaptee( UpdateExecutorAdaptee.class, ResourceNoMapping.class );
                result = updateAdaptee;

                updateAdaptee.prepareUpdate( inputResource, ( Identifier ) any, ( MediaProvider ) any );
                result = remoteRequest;

                updateAdaptee.executeUpdate( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = responseResource;
            }
        };

        tested.update( inputResource ).identifiedBy( new Identifier( 1L ) ).finish();

        noMappingVerifications();
    }

    @Test
    public void noResourceMappingDelete()
            throws IOException
    {
        new Expectations()
        {
            {
                tested.getExecutorAdaptee( DeleteExecutorAdaptee.class, ResourceNoMapping.class );
                result = deleteAdaptee;

                deleteAdaptee.prepareDelete( ( Identifier ) any );
                result = remoteRequest;
            }
        };

        tested.delete( ResourceNoMapping.class ).identifiedBy( new Identifier( 1L ) ).finish();

        noMappingVerifications();
    }

    @Test
    public void insertReturnsNoContent()
            throws IOException
    {
        new Expectations()
        {
            {
                tested.getExecutorAdaptee( InsertExecutorAdaptee.class, ResourceNoMapping.class );
                result = insertAdaptee;

                insertAdaptee.prepareInsert( inputResource, ( Identifier ) any, ( MediaProvider ) any );
                result = remoteRequest;

                insertAdaptee.executeInsert( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = null;
            }
        };

        assertThat( tested.insert( inputResource, new Identifier( 1L ) ).finish() ).isNull();
    }

    @Test
    public void updateReturnsNoContent()
            throws IOException
    {
        new Expectations()
        {
            {
                tested.getExecutorAdaptee( UpdateExecutorAdaptee.class, ResourceNoMapping.class );
                result = updateAdaptee;

                updateAdaptee.prepareUpdate( inputResource, ( Identifier ) any, ( MediaProvider ) any );
                result = remoteRequest;

                updateAdaptee.executeUpdate( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = null;
            }
        };

        assertThat( tested.update( inputResource ).identifiedBy( new Identifier( 1L ) ).finish() ).isNull();
    }

    @Test
    public void prepareDownloadRequest()
            throws GeneralSecurityException, IOException
    {
        final String prefix = "myapi";

        new Expectations()
        {
            {
                tested.getExecutorAdaptee( DownloadExecutorAdaptee.class, ResourceNoMapping.class );
                result = downloadAdaptee;

                downloadAdaptee.getApiPrefix();
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
                apiFactory.newRequestConfig( prefix, withNotNull() );
                times = 1;

                apiFactory.getHttpTransport();
                times = 1;
            }
        };
    }

    @Test( expectedExceptions = IllegalArgumentException.class )
    public void executeDownloadNullUrl()
    {
        new Expectations()
        {
            {
                downloadAdaptee.prepareDownloadUrl( ( Identifier ) any, anyString, ( Map<String, Object> ) any, ( Locale ) any );
                result = null;
            }
        };

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        Identifier id = new Identifier( 1L );

        tested.executeDownload( downloader, downloadAdaptee, ResourceNoMapping.class, id, content, interceptor, null,
                null, null );
    }

    @Test( expectedExceptions = RuntimeException.class )
    public void executeDownloadException()
            throws IOException
    {
        final URL url = new URL( "https://www.ctoolkit.org/download" );

        new Expectations()
        {
            {
                downloadAdaptee.prepareDownloadUrl( ( Identifier ) any, anyString, ( Map<String, Object> ) any, ( Locale ) any );
                result = url;

                downloader.download( ( GenericUrl ) any, ( HttpHeaders ) any, ( OutputStream ) any );
                result = new IOException();
            }
        };

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        final Identifier id = new Identifier( 1L );

        tested.executeDownload( downloader, downloadAdaptee, ResourceNoMapping.class, id, content, interceptor, null,
                null, null );
    }

    @Test
    public void executeDownload()
            throws IOException
    {
        final Identifier id = new Identifier( 1L );
        // expected content type
        final String type = "application/pdf";
        final Locale locale = Locale.GERMANY;
        final URL url = new URL( "https://www.ctoolkit.org/download" );
        final HttpHeaders headers = new HttpHeaders();
        final Map<String, Object> params = new HashMap<>();

        headers.setContentType( type );

        new Expectations()
        {
            {
                downloadAdaptee.prepareDownloadUrl( id, type, params, locale );
                result = url;
            }
        };

        final ByteArrayOutputStream content = new ByteArrayOutputStream();
        Class<ResourceNoMapping> resource = ResourceNoMapping.class;
        tested.executeDownload( downloader, downloadAdaptee, resource, id, content, interceptor, headers, params, locale );

        new Verifications()
        {
            {
                downloader.download( new GenericUrl( url ), headers, content );
                times = 1;
            }
        };
    }

    @Test
    public void executeDownloadRootIdentifier()
            throws MalformedURLException
    {
        final Identifier identifier = new Identifier( 1L, 99L ).leaf();
        new Expectations()
        {
            {
                downloadAdaptee.prepareDownloadUrl( ( Identifier ) any, anyString, null, null );
                result = new URL( "http://localhost/download" );
            }
        };

        tested.executeDownload( downloader, downloadAdaptee, ResourceNoMapping.class, identifier, output,
                interceptor, null, null, null );

        new Verifications()
        {
            {
                Identifier root;
                downloadAdaptee.prepareDownloadUrl( root = withCapture(), null, null, null );
                assertEquals( root.getLong(), Long.valueOf( 1L ), "Root Identifier" );
            }
        };
    }

    @Test
    public void callbackExecuteGetRootIdentifier() throws IOException
    {
        final Identifier identifier = new Identifier( 1L, 99L ).leaf();
        new Expectations( tested )
        {
            {
                tested.getExistingResourceProvider( ( Class<Object> ) any );
                result = provider;

                getAdaptee.executeGet( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = responseResource;

                provider.get( ( Identifier ) any, null, null );
                result = null;
            }
        };

        tested.callbackExecuteGet( getAdaptee, new Object(), ResourceNoMapping.class, identifier, null, null );

        new Verifications()
        {
            {
                Identifier root;
                provider.get( root = withCapture(), null, null );
                assertEquals( root.getLong(), Long.valueOf( 1L ), "Root Identifier" );

                provider.persist( any, root = withCapture(), null, null, null );
                assertEquals( root.getLong(), Long.valueOf( 1L ), "Root Identifier" );
            }
        };
    }

    @Test
    public void listRequestRootIdentifier()
            throws IOException
    {
        final Identifier identifier = new Identifier( 1L, 99L ).leaf();
        new Expectations()
        {
            {
                tested.getExecutorAdaptee( ListExecutorAdaptee.class, ResourceNoMapping.class );
                result = listAdaptee;

                listAdaptee.prepareList( ( Identifier ) any );
                result = remoteRequest;
            }
        };

        tested.list( ResourceNoMapping.class, identifier );

        new Verifications()
        {
            {
                Identifier root;
                listAdaptee.prepareList( root = withCapture() );
                assertEquals( root.getLong(), Long.valueOf( 1L ), "Root Identifier" );
            }
        };
    }

    @Test
    public void internalInsertRequestRootIdentifier()
            throws IOException
    {
        final Identifier identifier = new Identifier( 1L, 99L ).leaf();
        new Expectations()
        {
            {
                tested.getExecutorAdaptee( InsertExecutorAdaptee.class, ResourceNoMapping.class );
                result = insertAdaptee;

                insertAdaptee.prepareInsert( any, null, null );
                result = remoteRequest;
            }
        };

        tested.internalInsert( inputResource, identifier, null );

        new Verifications()
        {
            {
                Identifier root;
                insertAdaptee.prepareInsert( any, root = withCapture(), null );
                assertEquals( root.getLong(), Long.valueOf( 1L ), "Root Identifier" );
            }
        };
    }

    @Test
    public void internalUpdateRequestRootIdentifier()
            throws IOException
    {
        final Identifier identifier = new Identifier( 1L, 99L ).leaf();
        new Expectations()
        {
            {
                tested.getExecutorAdaptee( UpdateExecutorAdaptee.class, ResourceNoMapping.class );
                result = updateAdaptee;

                updateAdaptee.prepareUpdate( any, ( Identifier ) any, null );
                result = remoteRequest;
            }
        };

        tested.internalUpdate( inputResource, identifier, null );

        new Verifications()
        {
            {
                Identifier root;
                updateAdaptee.prepareUpdate( any, root = withCapture(), null );
                assertEquals( root.getLong(), Long.valueOf( 1L ), "Root Identifier" );
            }
        };
    }

    @Test
    public void internalDeleteRequestRootIdentifier()
            throws IOException
    {
        final Identifier identifier = new Identifier( 1L, 99L ).leaf();
        new Expectations()
        {
            {
                tested.getExecutorAdaptee( DeleteExecutorAdaptee.class, ResourceNoMapping.class );
                result = deleteAdaptee;

                deleteAdaptee.prepareDelete( ( Identifier ) any );
                result = remoteRequest;
            }
        };

        tested.internalDelete( ResourceNoMapping.class, identifier );

        new Verifications()
        {
            {
                Identifier root;
                deleteAdaptee.prepareDelete( root = withCapture() );
                assertEquals( root.getLong(), Long.valueOf( 1L ), "Root Identifier" );
            }
        };
    }

    @Test
    public void impersonate_Ok()
    {
        String apiName = "drive";
        String userEmail = "email@turnonline.biz";

        new Expectations()
        {
            {
                apiFactory.getClientApi( apiName );
                apiProvider.init( ( Collection<String> ) any, userEmail );
            }
        };

        tested.impersonate( Lists.newArrayList(), userEmail, apiName );
    }

    @Test( expectedExceptions = IllegalArgumentException.class )
    public void impersonate_ClientApiNotFound()
    {
        String apiName = "drive";
        String userEmail = "email@turnonline.biz";

        new Expectations()
        {
            {
                apiFactory.getClientApi( apiName );
                result = null;

                apiProvider.init( ( Collection<String> ) any, userEmail );
                times = 0;
            }
        };

        tested.impersonate( Lists.newArrayList(), userEmail, apiName );
    }

    private void noMappingVerifications()
    {
        new Verifications()
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
        };
    }
}