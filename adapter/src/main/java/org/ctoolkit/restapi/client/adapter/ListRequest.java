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

import org.ctoolkit.restapi.client.AuthRequest;
import org.ctoolkit.restapi.client.ListRetrievalRequest;
import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The LIST request implementation that delegates callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class ListRequest<T>
        implements ListRetrievalRequest<T>
{
    private final Class<T> resource;

    private final RestFacadeAdapter adapter;

    private final ListExecutorAdaptee adaptee;

    private final Object remoteRequest;

    private Map<String, Object> params;

    private Locale withLocale;

    private int start = -1;

    private int length = -1;

    private String orderBy;

    private Boolean ascending;

    private GoogleRequestHeaders filler;

    private String token;

    ListRequest( @Nonnull Class<T> resource,
                 @Nonnull RestFacadeAdapter adapter,
                 @Nonnull ListExecutorAdaptee adaptee,
                 @Nonnull Object remoteRequest )
    {
        this.resource = checkNotNull( resource );
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.remoteRequest = checkNotNull( remoteRequest );
        this.params = new HashMap<>();
        this.filler = new GoogleRequestHeaders( remoteRequest );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <U> U underlying( Class<U> type )
    {
        return ( U ) remoteRequest;
    }

    @Override
    public List<T> finish()
    {
        return finish( null, withLocale );
    }

    @Override
    public List<T> finish( @Nonnull RequestCredential credential )
    {
        checkNotNull( credential );
        credential.populate( this.params );
        return finish();
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
        if ( parameters != null )
        {
            params.putAll( parameters );
        }

        filler.acceptLanguage( locale );
        if ( token != null )
        {
            filler.authorization( token );
        }

        return adapter.callbackExecuteList( adaptee, remoteRequest, resource, params, locale, start, length,
                orderBy, ascending );
    }

    @Override
    public ListRetrievalRequest<T> configWith( @Nonnull Properties properties )
    {
        checkNotNull( properties );
        RequestCredential.populate( properties, this.params );
        return this;
    }

    @Override
    public ListRetrievalRequest<T> forLang( @Nonnull Locale locale )
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
    public Request<List<T>> addHeader( @Nonnull String header, @Nonnull String value )
    {
        checkNotNull( header );
        checkNotNull( value );

        filler.addHeader( header, value );
        return this;
    }

    @Override
    public AuthRequest<List<T>> authBy( @Nonnull String authorization )
    {
        checkNotNull( authorization );

        this.token = authorization;
        return new AuthRequestImpl<>( this, filler );
    }

    @Override
    public ListRetrievalRequest<T> start( int start )
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
    public ListRetrievalRequest<T> length( int length )
    {
        if ( length < 0 )
        {
            String msg = "length: '" + length + "' property cannot have negative value.";
            throw new IllegalArgumentException( msg );
        }
        this.length = length;
        return this;
    }

    @Override
    public ListRetrievalRequest<T> orderBy( @Nullable String property )
    {
        this.orderBy = property;
        return this;
    }

    @Override
    public ListRetrievalRequest<T> sortAscending( boolean ascending )
    {
        this.ascending = ascending;
        return this;
    }
}
