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

import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.SingleDownloadMediaRequest;
import org.ctoolkit.restapi.client.SingleRequest;

import javax.annotation.Nonnull;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The concrete implementation that works with {@link OutputStream}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class OutputStreamDownloadRequest
        implements SingleDownloadMediaRequest
{
    private final RestFacadeAdapter adapter;

    private final Class resource;

    private final OutputStream output;

    private final String type;

    /**
     * Creates an instance based on the given input.
     *
     * @param adapter  the resource facade adapter instance
     * @param resource the type of resource to download as a media
     * @param output   the output stream where desired content will be downloaded to.
     */
    OutputStreamDownloadRequest( @Nonnull RestFacadeAdapter adapter,
                                 @Nonnull Class resource,
                                 @Nonnull OutputStream output )
    {
        this( adapter, resource, output, null );
    }

    /**
     * Creates an instance based on the given input.
     *
     * @param adapter  the resource facade adapter instance
     * @param resource the type of resource to download as a media
     * @param output   the output stream where desired content will be downloaded to.
     */
    OutputStreamDownloadRequest( @Nonnull RestFacadeAdapter adapter,
                                 @Nonnull Class resource,
                                 @Nonnull OutputStream output,
                                 String type )
    {
        this.adapter = checkNotNull( adapter );
        this.resource = checkNotNull( resource );
        this.output = checkNotNull( output );
        this.type = type;
    }


    @Override
    public SingleRequest identifiedBy( @Nonnull Identifier identifier )
    {
        checkNotNull( identifier );
        return adapter.prepareDownloadRequest( resource, identifier, output, type );
    }

    @Override
    public SingleRequest identifiedBy( @Nonnull String identifier )
    {
        checkNotNull( identifier );
        return identifiedBy( new Identifier( identifier ) );
    }

    @Override
    public SingleRequest identifiedBy( @Nonnull Long identifier )
    {
        checkNotNull( identifier );
        return identifiedBy( new Identifier( identifier ) );
    }
}
