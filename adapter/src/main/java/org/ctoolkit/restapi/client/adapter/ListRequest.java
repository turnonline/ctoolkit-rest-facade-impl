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

import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.RetrievalRequest;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The LIST request implementation that delegates callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class ListRequest<T>
        implements RetrievalRequest<T>
{
    private final Class<T> resource;

    private final ResourceFacadeAdapter adapter;

    private final ListExecutorAdaptee adaptee;

    private final Object remoteRequest;

    private RequestCredential credential;

    private Map<String, Object> params;

    private Locale withLocale;

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
        this.params = new HashMap<>();
    }

    @Override
    public List<T> finish()
    {
        return finish( null, withLocale );
    }

    @Override
    public List<T> finish( int start, int length )
    {
        if ( start < 0 || length < 0 )
        {
            String msg = "start: '" + start + "' or length: '" + length + "' property cannot have negative values.";
            throw new IllegalArgumentException( msg );
        }
        this.start = start;
        this.length = length;
        return finish( null, null );
    }

    @Override
    public List<T> finish( @Nullable Map<String, Object> criteria )
    {
        return finish( criteria, withLocale );
    }

    @Override
    public List<T> finish( @Nullable Locale locale )
    {
        return finish( null, locale );
    }

    @Override
    public List<T> finish( @Nullable Map<String, Object> parameters, @Nullable Locale locale )
    {
        if ( credential != null )
        {
            parameters = credential.populate( parameters );
        }
        if ( parameters != null )
        {
            params.putAll( parameters );
        }

        return adapter.callbackExecuteList( adaptee, remoteRequest, resource, params, locale, start, length );
    }

    @Override
    public ListRequest<T> configWith( @Nonnull RequestCredential credential )
    {
        this.credential = checkNotNull( credential );
        return this;
    }

    @Override
    public RetrievalRequest<T> forLang( @Nonnull Locale locale )
    {
        this.withLocale = checkNotNull( locale );
        return this;
    }

    @Override
    public Request<List<T>> add( @Nonnull String name, @Nonnull Object value )
    {
        checkNotNull( name );
        checkNotNull( value );

        params.put( name, value );
        return this;
    }

    @Override
    public Request<List<T>> add( @Nonnull String name, @Nonnull String value )
    {
        checkNotNull( name );
        checkNotNull( value );

        params.put( name, value );
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
