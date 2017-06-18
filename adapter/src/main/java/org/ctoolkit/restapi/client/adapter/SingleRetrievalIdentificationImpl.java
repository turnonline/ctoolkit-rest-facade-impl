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
import org.ctoolkit.restapi.client.SingleRetrievalIdentification;
import org.ctoolkit.restapi.client.SingleRetrievalRequest;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Delegating call with newly defined identifier back to adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class SingleRetrievalIdentificationImpl<T>
        implements SingleRetrievalIdentification<T>
{
    private final RestFacadeAdapter adapter;

    private final Class<T> resource;

    SingleRetrievalIdentificationImpl( @Nonnull RestFacadeAdapter adapter, @Nonnull Class<T> resource )
    {
        this.adapter = checkNotNull( adapter );
        this.resource = checkNotNull( resource );
    }

    @Override
    public SingleRetrievalRequest<T> identifiedBy( @Nonnull Identifier identifier )
    {
        return adapter.internalGet( resource, identifier );
    }

    @Override
    public SingleRetrievalRequest<T> identifiedBy( @Nonnull String identifier )
    {
        return adapter.internalGet( resource, new Identifier( identifier ) );
    }

    @Override
    public SingleRetrievalRequest<T> identifiedBy( @Nonnull Long identifier )
    {
        return adapter.internalGet( resource, new Identifier( identifier ) );
    }
}
