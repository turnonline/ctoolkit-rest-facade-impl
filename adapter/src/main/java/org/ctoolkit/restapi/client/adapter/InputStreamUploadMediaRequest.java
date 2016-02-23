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
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.SingleRequest;
import org.ctoolkit.restapi.client.UploadMediaRequest;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The concrete implementation that works with {@link AbstractInputStreamContent}.
 *
 * @author <a href="mailto:aurel.medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class InputStreamUploadMediaRequest<T>
        implements UploadMediaRequest<T>
{
    private final ResourceFacadeAdapter adapter;

    private final T resource;

    private final MediaProvider<AbstractInputStreamContent> provider;

    /**
     * Creates an instance based on the given input.
     *
     * @param adapter  the adapter to callback execute method
     * @param resource the resource instance to be associated with provided media content
     * @param provider the provider to provide concrete instance of media content
     */
    InputStreamUploadMediaRequest( @Nonnull ResourceFacadeAdapter adapter,
                                   @Nonnull T resource,
                                   @Nonnull MediaProvider<AbstractInputStreamContent> provider )
    {
        this.adapter = checkNotNull( adapter );
        this.resource = checkNotNull( resource );
        this.provider = checkNotNull( provider );
    }

    @Override
    public SingleRequest<T> insert()
    {
        return adapter.internalInsert( resource, null, provider );
    }

    @Override
    public SingleRequest<T> insert( @Nullable Identifier parent )
    {
        return adapter.internalInsert( resource, parent, provider );
    }

    @Override
    public SingleRequest<T> update( @Nonnull Identifier identifier )
    {
        return adapter.internalUpdate( resource, identifier, provider );
    }
}
