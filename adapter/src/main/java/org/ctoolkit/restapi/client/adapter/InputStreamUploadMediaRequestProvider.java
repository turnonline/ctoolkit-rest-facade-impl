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

import org.ctoolkit.restapi.client.SingleUploadMediaRequest;
import org.ctoolkit.restapi.client.UploadMediaProvider;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The concrete upload media request provider implementation that works with {@link InputStream}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class InputStreamUploadMediaRequestProvider<T>
        implements UploadMediaProvider<T>
{
    private final RestFacadeAdapter adapter;

    private final T resource;

    /**
     * Creates an instance based on the given input.
     *
     * @param adapter  the adapter to callback execute method
     * @param resource the resource instance to associate with media content
     */
    InputStreamUploadMediaRequestProvider( @Nonnull RestFacadeAdapter adapter, @Nonnull T resource )
    {
        this.adapter = checkNotNull( adapter );
        this.resource = checkNotNull( resource );
    }

    @Override
    public SingleUploadMediaRequest<T> data( @Nonnull File file )
    {
        return data( new InputStreamMediaProvider( file ) );
    }

    @Override
    public SingleUploadMediaRequest<T> data( @Nonnull InputStream inputStream )
    {
        return data( new InputStreamMediaProvider( inputStream ) );
    }

    @Override
    public SingleUploadMediaRequest<T> data( @Nonnull byte[] array )
    {
        return data( new InputStreamMediaProvider( array ) );
    }

    @Override
    public SingleUploadMediaRequest<T> data( @Nonnull byte[] array, int offset, int length )
    {
        return data( new InputStreamMediaProvider( array, offset, length ) );
    }

    private SingleUploadMediaRequest<T> data( InputStreamMediaProvider provider )
    {
        return new InputStreamUploadRequest<>( adapter, resource, provider );
    }
}
