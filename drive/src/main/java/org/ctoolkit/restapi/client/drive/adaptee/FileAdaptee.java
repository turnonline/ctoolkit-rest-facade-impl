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

package org.ctoolkit.restapi.client.drive.adaptee;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adaptee.UnderlyingExecutorAdaptee;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Drive's {@link File} adaptee implementation.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class FileAdaptee
        extends AbstractGoogleClientAdaptee<Drive, File>
        implements InsertExecutorAdaptee<File>, DeleteExecutorAdaptee<File>, UnderlyingExecutorAdaptee<Drive.Files>
{
    @Inject
    public FileAdaptee( Drive client )
    {
        super( client );
    }

    @Override
    public Drive.Files.Create prepareInsert( @Nonnull File resource,
                                             @Nullable Identifier parentKey,
                                             @Nullable MediaProvider<?> provider )
            throws IOException
    {
        checkNotNull( resource );

        Drive.Files.Create insert;
        if ( provider == null )
        {
            insert = client().files().create( resource );
        }
        else
        {
            insert = client().files().create( resource, media( provider ) );
        }

        return insert;
    }

    @Override
    public File executeInsert( @Nonnull Object request,
                               @Nullable Map<String, Object> parameters,
                               @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        fill( get( request ), parameters, locale );
        return execute( request );
    }

    @Override
    public Drive.Files.Delete prepareDelete( @Nonnull Identifier identifier )
            throws IOException
    {
        checkNotNull( identifier );
        return client().files().delete( identifier.getString() );
    }

    @Override
    public void executeDelete( @Nonnull Object request,
                               @Nullable Map<String, Object> parameters,
                               @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        acceptLanguage( get( request ), locale );
        execute( request );
    }

    @Override
    public Drive.Files prepareUnderlying( @Nullable Object resource, @Nullable Identifier identifier, @Nullable Map<String, Object> parameters )
            throws IOException
    {
        return client().files();
    }

    @Override
    public Object executeUnderlying( @Nonnull Drive.Files request, @Nullable Map<String, Object> parameters, @Nullable Locale locale )
            throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
