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

package org.ctoolkit.restapi.client.adapter;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.PatchExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Unit tests to test {@link ResourceFacadeAdapter}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@SuppressWarnings( {"unchecked", "ConstantConditions"} )
public class ResourceFacadeAdapterTest
{
    @Tested
    private ResourceFacadeAdapter tested;

    @Injectable
    private MapperFacade mapper;

    @Injectable
    private MapperFactory factory;

    @Injectable
    private ResourceProviderInjector injector;

    @Test
    public void noResourceMappingNewInstance( @Mocked final NewExecutorAdaptee adaptee,
                                              @Mocked final RemoteRequest request,
                                              @Mocked final ResourceNoMapping resource )
            throws IOException
    {
        new NonStrictExpectations()
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

        tested.newInstance( ResourceNoMapping.class ).execute();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingGet( @Mocked final GetExecutorAdaptee adaptee,
                                      @Mocked final RemoteRequest request,
                                      @Mocked final ResourceNoMapping resource )
            throws IOException
    {
        new NonStrictExpectations()
        {
            {
                injector.getExecutorAdaptee( GetExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareGet( ( Identifier ) any );
                result = request;

                adaptee.executeGet( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = resource;
            }
        };

        tested.get( ResourceNoMapping.class, new Identifier( 1L ) ).execute();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingEmptyResponseList( @Mocked final ListExecutorAdaptee adaptee,
                                                    @Mocked final RemoteRequest request )
            throws IOException
    {
        new NonStrictExpectations()
        {
            {
                injector.getExecutorAdaptee( ListExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareList( ( Identifier ) any );
                result = request;

                adaptee.executeList( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = new ArrayList<>();

                // returns null to make sure no provider is being injected
                injector.getExistingResourceProvider( ( Class ) any );
                result = null;
            }
        };

        tested.list( ResourceNoMapping.class ).execute();

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

        new NonStrictExpectations()
        {
            {
                injector.getExecutorAdaptee( ListExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareList( ( Identifier ) any );
                result = request;

                adaptee.executeList( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = resources;

                // returns null to make sure no provider is being injected
                injector.getExistingResourceProvider( ( Class ) any );
                result = null;
            }
        };

        tested.list( ResourceNoMapping.class ).execute();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingInsert( @Mocked final InsertExecutorAdaptee adaptee,
                                         @Mocked final RemoteRequest request,
                                         @Mocked final ResourceNoMapping inputResource,
                                         @Mocked final ResourceNoMapping responseResource )
            throws IOException
    {
        new NonStrictExpectations()
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

        tested.insert( inputResource, new Identifier( 1L ) ).execute();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingUpdate( @Mocked final UpdateExecutorAdaptee adaptee,
                                         @Mocked final RemoteRequest request,
                                         @Mocked final ResourceNoMapping inputResource,
                                         @Mocked final ResourceNoMapping responseResource )
            throws IOException
    {
        new NonStrictExpectations()
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

        tested.update( inputResource, new Identifier( 1L ) ).execute();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingPatch( @Mocked final PatchExecutorAdaptee adaptee,
                                        @Mocked final RemoteRequest request,
                                        @Mocked final PatchResourceNoMapping inputResource,
                                        @Mocked final ResourceNoMapping responseResource )
            throws IOException
    {
        new NonStrictExpectations()
        {
            {
                injector.getExecutorAdaptee( PatchExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.preparePatch( any, ( Identifier ) any, anyString );
                result = request;

                adaptee.executePatch( any, ( Map<String, Object> ) any, ( Locale ) any );
                result = responseResource;

                inputResource.type();
                result = ResourceNoMapping.class;
            }
        };

        tested.patch( inputResource, new Identifier( 1L ) ).execute();

        new NoMappingVerifications();
    }

    @Test
    public void noResourceMappingDelete( @Mocked final DeleteExecutorAdaptee adaptee,
                                         @Mocked final RemoteRequest request )
            throws IOException
    {
        new NonStrictExpectations()
        {
            {
                injector.getExecutorAdaptee( DeleteExecutorAdaptee.class, ResourceNoMapping.class );
                result = adaptee;

                adaptee.prepareDelete( ( Identifier ) any );
                result = request;
            }
        };

        tested.delete( ResourceNoMapping.class, new Identifier( 1L ) ).execute();

        new NoMappingVerifications();
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