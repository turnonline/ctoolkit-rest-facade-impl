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

import com.google.api.client.http.AbstractInputStreamContent;
import org.ctoolkit.restapi.client.MediaRequest;
import org.ctoolkit.restapi.client.UploadMediaRequest;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The concrete implementation that works with {@link AbstractInputStreamContent}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class InputStreamMediaRequest<T>
        implements MediaRequest<T>
{
    private final ResourceFacadeAdapter adapter;

    private final T resource;

    /**
     * Creates an instance based on the given input.
     *
     * @param adapter  the adapter to callback execute method
     * @param resource the resource instance to associate with media content
     */
    InputStreamMediaRequest( @Nonnull ResourceFacadeAdapter adapter, @Nonnull T resource )
    {
        this.adapter = checkNotNull( adapter );
        this.resource = checkNotNull( resource );
    }

    @Override
    public UploadMediaRequest<T> upload( File file, String type )
    {
        return upload( new InputStreamMediaProvider( file, type ) );
    }

    @Override
    public UploadMediaRequest<T> upload( InputStream inputStream, String type )
    {
        return upload( new InputStreamMediaProvider( inputStream, type ) );
    }

    @Override
    public UploadMediaRequest<T> upload( byte[] media, String type )
    {
        return upload( new InputStreamMediaProvider( media, type ) );
    }

    @Override
    public UploadMediaRequest<T> upload( byte[] array, int offset, int length, String type )
    {
        return upload( new InputStreamMediaProvider( array, offset, length, type ) );
    }

    private UploadMediaRequest<T> upload( InputStreamMediaProvider provider )
    {
        return new InputStreamUploadMediaRequest<>( adapter, resource, provider );
    }
}
