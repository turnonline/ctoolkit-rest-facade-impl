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
import org.ctoolkit.restapi.client.PatchRequest;
import org.ctoolkit.restapi.client.adaptee.PatchAdaptee;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The PATCH request implementation that collects input parameters
 * and then delegates a callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class PatchRequestImpl<R>
        implements org.ctoolkit.restapi.client.PatchRequest<R>
{
    private final ResourceFacadeAdapter adapter;

    private final PatchAdaptee<R> adaptee;

    private Identifier identifier;

    private Object resource;

    PatchRequestImpl( @Nonnull ResourceFacadeAdapter adapter,
                      @Nonnull PatchAdaptee<R> adaptee )
    {
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
    }

    @Override
    public R build()
    {
        return adapter.callbackPatchAdaptee( adaptee, resource, identifier );
    }

    @Override
    public PatchRequest<R> resource( Object resource )
    {
        this.resource = resource;
        return this;
    }

    @Override
    public PatchRequest<R> identifier( Identifier identifier )
    {
        this.identifier = identifier;
        return this;
    }
}
