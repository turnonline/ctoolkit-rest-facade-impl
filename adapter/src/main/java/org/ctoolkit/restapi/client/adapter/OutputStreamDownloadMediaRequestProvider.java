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

import org.ctoolkit.restapi.client.DownloadMediaProvider;
import org.ctoolkit.restapi.client.SingleDownloadMediaRequest;

import javax.annotation.Nonnull;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The concrete download media request provider implementation that works with {@link OutputStream}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class OutputStreamDownloadMediaRequestProvider
        implements DownloadMediaProvider
{
    private final RestFacadeAdapter adapter;

    private final Class resource;

    OutputStreamDownloadMediaRequestProvider( @Nonnull RestFacadeAdapter adapter, @Nonnull Class resource )
    {
        this.adapter = checkNotNull( adapter );
        this.resource = checkNotNull( resource );
    }

    @Override
    public SingleDownloadMediaRequest to( @Nonnull OutputStream output )
    {
        return new OutputStreamDownloadRequest( adapter, resource, output );
    }
}
