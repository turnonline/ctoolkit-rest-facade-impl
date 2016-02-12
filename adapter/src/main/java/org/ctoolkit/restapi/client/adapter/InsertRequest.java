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

import org.ctoolkit.restapi.client.SingleRequest;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The INSERT request implementation that delegates callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class InsertRequest<T>
        implements SingleRequest<T>
{
    private final Class<T> resource;

    private final ResourceFacadeAdapter adapter;

    private final InsertExecutorAdaptee adaptee;

    private final Object remoteRequest;

    private Object parentKey;

    InsertRequest( @Nonnull Class<T> resource,
                   @Nullable Object parentKey,
                   @Nonnull ResourceFacadeAdapter adapter,
                   @Nonnull InsertExecutorAdaptee adaptee,
                   @Nonnull Object remoteRequest )
    {
        this.resource = checkNotNull( resource );
        this.parentKey = parentKey;
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.remoteRequest = checkNotNull( remoteRequest );
    }


    @Override
    @SuppressWarnings( "unchecked" )
    public <Q> Q query( Class<Q> type )
    {
        return ( Q ) remoteRequest;
    }

    @Override
    public T execute()
    {
        return execute( null, null );
    }

    @Override
    public T execute( Map<String, Object> parameters )
    {
        return execute( parameters, null );
    }

    @Override
    public T execute( Locale locale )
    {
        return execute( null, locale );
    }

    @Override
    public T execute( Map<String, Object> parameters, Locale locale )
    {
        return adapter.callbackExecuteInsert( adaptee, remoteRequest, resource, parentKey, parameters, locale );
    }
}
