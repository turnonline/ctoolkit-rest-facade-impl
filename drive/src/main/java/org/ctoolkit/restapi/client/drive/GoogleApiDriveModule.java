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

package org.ctoolkit.restapi.client.drive;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.ctoolkit.restapi.client.RemoteServerErrorException;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.PatchAdaptee;
import org.ctoolkit.restapi.client.drive.adaptee.FileAdaptee;
import org.ctoolkit.restapi.client.googleapis.GoogleApiCredentialFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

/**
 * The Google Drive guice module as a default configuration.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GoogleApiDriveModule
        extends AbstractModule
{
    private static final Logger logger = LoggerFactory.getLogger( GoogleApiDriveModule.class );

    @Override
    protected void configure()
    {
        bind( new TypeLiteral<InsertExecutorAdaptee<File>>()
        {
        } ).to( FileAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<File>>()
        {
        } ).to( FileAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<PatchAdaptee<Drive.Files>>()
        {
        } ).to( FileAdaptee.class ).in( Singleton.class );
    }

    @Provides
    @Singleton
    Drive provideDrive( GoogleApiCredentialFactory factory )
    {
        Set<String> scopes = DriveScopes.all();
        Drive.Builder builder;

        try
        {
            HttpRequestInitializer credential = factory.authorize( scopes, null );
            builder = new Drive.Builder( factory.getHttpTransport(), factory.getJsonFactory(), credential );
            builder.setApplicationName( factory.getApplicationName() );
        }
        catch ( GeneralSecurityException e )
        {
            logger.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + factory.getApplicationName()
                    + " Service account: " + factory.getServiceAccountEmail(), e );
            throw new UnauthorizedException( e.getMessage() );
        }
        catch ( IOException e )
        {
            logger.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + factory.getApplicationName()
                    + " Service account: " + factory.getServiceAccountEmail(), e );

            throw new RemoteServerErrorException( HttpStatusCodes.STATUS_CODE_SERVER_ERROR, e.getMessage() );
        }

        return builder.build();
    }
}
