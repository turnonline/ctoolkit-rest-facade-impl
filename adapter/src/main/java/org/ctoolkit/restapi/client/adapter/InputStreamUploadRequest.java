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

import com.google.api.client.http.AbstractInputStreamContent;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.PayloadRequest;
import org.ctoolkit.restapi.client.SingleUploadMediaRequest;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The concrete implementation that works with {@link AbstractInputStreamContent}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class InputStreamUploadRequest<T>
        implements SingleUploadMediaRequest<T>
{
    private final RestFacadeAdapter adapter;

    private final T resource;

    private final MediaProvider provider;

    private Identifier identifier;

    /**
     * Creates an instance based on the given input.
     *
     * @param adapter  the adapter to interact with
     * @param resource the resource instance to be associated with provided media content
     * @param provider the provider to provide concrete instance of media content
     */
    InputStreamUploadRequest( @Nonnull RestFacadeAdapter adapter,
                              @Nonnull T resource,
                              @Nonnull MediaProvider provider )
    {
        this.adapter = checkNotNull( adapter );
        this.resource = checkNotNull( resource );
        this.provider = checkNotNull( provider );
    }

    @Override
    public PayloadRequest<T> insert()
    {
        return adapter.internalInsert( resource, identifier, provider );
    }

    @Override
    public SingleUploadMediaRequest<T> ofType( @Nonnull String type )
    {
        this.provider.setType( checkNotNull( type ) );
        return this;
    }

    @Override
    public SingleUploadMediaRequest<T> closeStreamAtTheEnd( boolean closeStream )
    {
        this.provider.setCloseInputStream( closeStream );
        return this;
    }

    @Override
    public SingleUploadMediaRequest<T> identifiedBy( @Nonnull Identifier identifier )
    {
        this.identifier = checkNotNull( identifier );
        return this;
    }

    @Override
    public SingleUploadMediaRequest<T> identifiedBy( @Nonnull String identifier )
    {
        checkNotNull( identifier );
        return identifiedBy( new Identifier( identifier ) );
    }

    @Override
    public SingleUploadMediaRequest<T> identifiedBy( @Nonnull Long identifier )
    {
        checkNotNull( identifier );
        return identifiedBy( new Identifier( identifier ) );
    }

    @Override
    public PayloadRequest<T> update()
    {
        if ( identifier == null )
        {
            throw new IllegalArgumentException( "For update operation the identifier is being required!" );
        }

        return adapter.internalUpdate( resource, identifier, provider );
    }
}
