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

package org.ctoolkit.restapi.client.drive;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UnderlyingClientAdaptee;
import org.ctoolkit.restapi.client.adapter.ClientApi;
import org.ctoolkit.restapi.client.drive.adaptee.FileAdaptee;

import javax.inject.Singleton;

/**
 * The Google Drive guice module as a default configuration.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GoogleApiDriveModule
        extends AbstractModule
{
    public static final String API_PREFIX = "drive";

    @Override
    protected void configure()
    {
        // ClientApi config
        bind( Drive.class ).toProvider( DriveProvider.class );

        MapBinder<String, ClientApi> mapBinder;
        mapBinder = MapBinder.newMapBinder( binder(), String.class, ClientApi.class );
        mapBinder.addBinding( API_PREFIX ).to( DriveProvider.class );
        // ClientApi config end

        bind( new TypeLiteral<InsertExecutorAdaptee<File>>()
        {
        } ).to( FileAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<File>>()
        {
        } ).to( FileAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UnderlyingClientAdaptee<Drive>>()
        {
        } ).to( FileAdaptee.class ).in( Singleton.class );
    }
}
