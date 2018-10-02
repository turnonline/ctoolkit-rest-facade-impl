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

package org.ctoolkit.restapi.client.adapter;

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.GenericUrl;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.DownloadExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

/**
 * {@link RestFacadeAdapter} unit testing with injected {@link Substitute}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class AdapterWithSubstituteTest
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

    @Injectable
    private Substitute substitute;

    @Mocked
    private GetExecutorAdaptee getAdaptee;

    @Mocked
    private NewExecutorAdaptee newAdaptee;

    @Mocked
    private InsertExecutorAdaptee insertAdaptee;

    @Mocked
    private UpdateExecutorAdaptee updateAdaptee;

    @Mocked
    private ListExecutorAdaptee listAdaptee;

    @Mocked
    private DeleteExecutorAdaptee deleteAdaptee;

    @Mocked
    private DownloadExecutorAdaptee downloadAdaptee;

    @Mocked
    private MediaHttpDownloader downloader;

    @Mocked
    private DownloadResponseInterceptor interceptor;

    @Test
    public void callbackNewInstance()
    {
        Object response = tested.callbackNewInstance( newAdaptee, new Object(), NewResource.class,
                null, null );

        assertThat( response ).isInstanceOf( NewResource.class );
    }

    @Test
    public void callbackNewInstance_Skip() throws IOException
    {
        new Expectations()
        {
            {
                //noinspection ConstantConditions
                substitute.newInstance( any, RemoteResource.class, null, null );
                result = new Substitute.ProceedWithRemoteCall();

                newAdaptee.executeNew( any, null, null );
                result = new RemoteResource();
            }
        };

        Object response = tested.callbackNewInstance( newAdaptee, new Object(), RemoteResource.class,
                null, null );

        assertThat( response ).isInstanceOf( RemoteResource.class );
    }

    @Test
    public void callbackExecuteGet()
    {
        Identifier identifier = new Identifier( 1L );
        Object response = tested.callbackExecuteGet( getAdaptee, new Object(), GetResource.class, identifier,
                null, null );

        assertThat( response ).isInstanceOf( GetResource.class );
    }

    @Test
    public void callbackExecuteGet_Skip() throws IOException
    {
        Identifier identifier = new Identifier( 1L );

        new Expectations()
        {
            {
                //noinspection ConstantConditions
                substitute.get( any, RemoteResource.class, identifier, null, null );
                result = new Substitute.ProceedWithRemoteCall();

                //noinspection unchecked
                getAdaptee.executeGet( any, ( Map<String, Object> ) any, null );
                result = new RemoteResource();
            }
        };

        Object response = tested.callbackExecuteGet( getAdaptee, new Object(), RemoteResource.class, identifier,
                null, null );

        assertThat( response ).isInstanceOf( RemoteResource.class );
    }

    @Test
    public void callbackExecuteList()
    {
        new Expectations()
        {
            {
                //noinspection unchecked,ConstantConditions
                substitute.list( any, GetResource.class, ( Map ) any, ( Locale ) any,
                        1, 10, null, null );
                result = Lists.newArrayList( new GetResource() );
            }
        };

        List<GetResource> response = tested.callbackExecuteList( listAdaptee, new Object(), GetResource.class,
                null, null, 1, 10, null, false );

        assertThat( response ).hasSize( 1 );
        assertThat( response.get( 0 ) ).isInstanceOf( GetResource.class );
    }

    @Test
    public void callbackExecuteList_Skip() throws IOException
    {
        new Expectations()
        {
            {
                //noinspection unchecked,ConstantConditions
                substitute.list( any, RemoteResource.class, ( Map ) any, ( Locale ) any,
                        1, 10, null, null );
                result = new Substitute.ProceedWithRemoteCall();

                //noinspection unchecked,ConstantConditions
                listAdaptee.executeList( any, ( Map<String, Object> ) any, ( Locale ) any,
                        1, 10, null, false );
                result = Lists.newArrayList( new RemoteResource() );
            }
        };

        List<RemoteResource> response = tested.callbackExecuteList( listAdaptee, new Object(), RemoteResource.class,
                null, null, 1, 10, null, false );

        assertThat( response ).hasSize( 1 );
        assertThat( response.get( 0 ) ).isInstanceOf( RemoteResource.class );
    }

    @Test
    public void callbackExecuteInsert()
    {
        Object response = tested.callbackExecuteInsert( insertAdaptee, new Object(), InsertResource.class,
                null, null, null );

        assertThat( response ).isInstanceOf( InsertResource.class );
    }

    @Test
    public void callbackExecuteInsert_Skip() throws IOException
    {
        new Expectations()
        {
            {
                //noinspection ConstantConditions
                substitute.insert( any, RemoteResource.class, null, null, null );
                result = new Substitute.ProceedWithRemoteCall();

                insertAdaptee.executeInsert( any, null, ( Locale ) any );
                result = new RemoteResource();
            }
        };

        Object response = tested.callbackExecuteInsert( insertAdaptee, new Object(), RemoteResource.class,
                null, null, null );

        assertThat( response ).isInstanceOf( RemoteResource.class );
    }

    @Test
    public void callbackExecuteUpdate()
    {
        Identifier identifier = new Identifier( 1L );
        Object response = tested.callbackExecuteUpdate( updateAdaptee, new Object(), UpdateResource.class,
                identifier, null, null );

        assertThat( response ).isInstanceOf( UpdateResource.class );
    }

    @Test
    public void callbackExecuteUpdate_Skip() throws IOException
    {
        Identifier identifier = new Identifier( 1L );
        new Expectations()
        {
            {
                //noinspection ConstantConditions
                substitute.update( any, RemoteResource.class, identifier, null, ( Locale ) any );
                result = new Substitute.ProceedWithRemoteCall();

                updateAdaptee.executeUpdate( any, null, ( Locale ) any );
                result = new RemoteResource();

            }
        };

        Object response = tested.callbackExecuteUpdate( updateAdaptee, new Object(), RemoteResource.class,
                identifier, null, null );

        assertThat( response ).isInstanceOf( RemoteResource.class );
    }

    @Test
    public void callbackExecuteDelete()
    {
        Identifier identifier = new Identifier( 1L );
        Object response = tested.callbackExecuteDelete( deleteAdaptee, new Object(), identifier,
                DeleteResource.class, null, null );

        assertThat( response ).isInstanceOf( DeleteResource.class );
    }

    @Test
    public void callbackExecuteDelete_Skip() throws IOException
    {
        Identifier identifier = new Identifier( 1L );
        new Expectations()
        {
            {
                //noinspection ConstantConditions
                substitute.delete( any, identifier, RemoteResource.class, null, ( Locale ) any );
                result = new Substitute.ProceedWithRemoteCall();

                deleteAdaptee.executeDelete( any, null, ( Locale ) any );
                result = new RemoteResource();
            }
        };

        Object response = tested.callbackExecuteDelete( deleteAdaptee, new Object(), identifier,
                RemoteResource.class, null, null );

        assertThat( response ).isInstanceOf( RemoteResource.class );
    }

    @Test
    public void executeDownload() throws IOException
    {
        Identifier identifier = new Identifier( 1L );
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        tested.executeDownload( downloader, downloadAdaptee, DownloadResource.class,
                identifier, output, interceptor, null, null, null );

        new Verifications()
        {
            {
                substitute.download( DownloadResource.class, identifier, output,
                        null, null, null );

                //noinspection ConstantConditions
                downloader.download( ( GenericUrl ) any, null, ( OutputStream ) any );
                times = 0;
            }
        };
    }

    @Test
    public void executeDownload_Skip() throws IOException
    {
        Identifier identifier = new Identifier( 1L );
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new Expectations()
        {
            {
                downloadAdaptee.prepareDownloadUrl( identifier.root(), anyString, null, ( Locale ) any );
                result = new URL( "http://localhost:8080" );

                substitute.download( DownloadResource.class, identifier, output,
                        null, null, ( Locale ) any );
                result = new Substitute.ProceedWithRemoteCall();
            }
        };

        tested.executeDownload( downloader, downloadAdaptee, DownloadResource.class,
                identifier, output, interceptor, null, null, null );

        new Verifications()
        {
            {
                //noinspection ConstantConditions
                downloader.download( ( GenericUrl ) any, null, ( OutputStream ) any );
            }
        };
    }
}
