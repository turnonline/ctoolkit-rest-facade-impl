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

package org.ctoolkit.restapi.client.appengine;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.ctoolkit.restapi.client.adapter.BeanMapperConfig;
import org.ctoolkit.restapi.client.adapter.DateTimeToDateConverter;

import javax.inject.Singleton;
import java.util.Set;

/**
 * The default Orika{@link MapperFacade} configuration.
 * <p>
 * If contribution to the orika bean mapper {@link MapperFactory} configuration is being required,
 * implement your own {@link BeanMapperConfig} and register it via guice multi binder as the example below.
 * This module will configure the shared mapper factory during instantiation.
 * <pre>
 * Multibinder&#60;BeanMapperConfig&#62; multibinder = Multibinder.newSetBinder( binder(), BeanMapperConfig.class );
 * multibinder.addBinding().to( MyBeanMapperConfigImpl.class );
 * </pre>
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 * @see BeanMapperConfig
 */
public class DefaultOrikaMapperFactoryModule
        extends AbstractModule
{
    @Provides
    @Singleton
    MapperFactory provideMapperFactory()
    {
        return new DefaultMapperFactory.Builder()
                .dumpStateOnException( false )
                // this is important in order to support HTTP PATCH functionality
                .mapNulls( false )
                .useBuiltinConverters( true )
                .build();
    }

    @Provides
    @Singleton
    MapperFacade provideMapperFacade( MapperFactory factory, Set<BeanMapperConfig> configs )
    {
        ConverterFactory converterFactory = factory.getConverterFactory();
        converterFactory.registerConverter( new DateTimeToDateConverter() );

        for ( BeanMapperConfig next : configs )
        {
            next.config( factory );
        }

        return factory.getMapperFacade();
    }

    @Override
    protected void configure()
    {
        Multibinder.newSetBinder( binder(), BeanMapperConfig.class );
    }
}
