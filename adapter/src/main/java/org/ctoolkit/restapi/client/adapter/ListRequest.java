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

import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.RetrievalRequest;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The LIST request implementation that delegates callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class ListRequest<T>
        implements RetrievalRequest<T>
{
    private final Class<T> resource;

    private final ResourceFacadeAdapter adapter;

    private final ListExecutorAdaptee adaptee;

    private final Object remoteRequest;

    private RequestCredential credential;

    private int start = -1;

    private int length = -1;

    ListRequest( @Nonnull Class<T> resource,
                 @Nonnull ResourceFacadeAdapter adapter,
                 @Nonnull ListExecutorAdaptee adaptee,
                 @Nonnull Object remoteRequest )
    {
        this.resource = checkNotNull( resource );
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.remoteRequest = checkNotNull( remoteRequest );
    }

    @Override
    public List<T> execute()
    {
        return execute( null, null );
    }

    @Override
    public List<T> execute( int start, int length )
    {
        if ( start < 0 || length < 0 )
        {
            String msg = "start: '" + start + "' or length: '" + length + "' property cannot have negative values.";
            throw new IllegalArgumentException( msg );
        }
        this.start = start;
        this.length = length;
        return execute( null, null );
    }

    @Override
    public List<T> execute( Map<String, Object> criteria )
    {
        return execute( criteria, null );
    }

    @Override
    public List<T> execute( Locale locale )
    {
        return execute( null, locale );
    }

    @Override
    public List<T> execute( Map<String, Object> parameters, Locale locale )
    {
        if ( credential != null )
        {
            parameters = credential.populate( parameters );
        }
        return adapter.callbackExecuteList( adaptee, remoteRequest, resource, parameters, locale, start, length );
    }

    @Override
    public ListRequest<T> config( RequestCredential credential )
    {
        this.credential = credential;
        return this;
    }

    @Override
    public ListRequest<T> start( int start )
    {
        if ( start < 0 )
        {
            String msg = "start: '" + start + "' property cannot have negative value.";
            throw new IllegalArgumentException( msg );
        }

        this.start = start;
        return this;
    }

    @Override
    public ListRequest<T> length( int length )
    {
        if ( length < 0 )
        {
            String msg = "length: '" + length + "' property cannot have negative value.";
            throw new IllegalArgumentException( msg );
        }
        this.length = length;
        return this;
    }
}
