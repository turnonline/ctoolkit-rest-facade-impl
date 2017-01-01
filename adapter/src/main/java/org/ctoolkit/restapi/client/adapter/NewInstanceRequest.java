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

import org.ctoolkit.restapi.client.ClientErrorException;
import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.SingleRequest;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The NEW request implementation that delegates callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class NewInstanceRequest<T>
        implements SingleRequest<T>
{
    private final Class<T> resource;

    private final ResourceFacadeAdapter adapter;

    private final NewExecutorAdaptee adaptee;

    private final Object remoteRequest;

    private RequestCredential credential;

    NewInstanceRequest( @Nonnull Class<T> resource,
                        @Nonnull ResourceFacadeAdapter adapter,
                        @Nonnull NewExecutorAdaptee adaptee,
                        @Nullable Object remoteRequest )
    {
        this.resource = checkNotNull( resource );
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.remoteRequest = remoteRequest;
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
    public T execute( Map<String, Object> criteria )
    {
        return execute( criteria, null );
    }

    @Override
    public T execute( Locale locale )
    {
        return execute( null, locale );
    }

    @Override
    public T execute( Map<String, Object> parameters, Locale locale )
    {
        if ( remoteRequest == null )
        {
            try
            {
                return resource.newInstance();
            }
            catch ( InstantiationException | IllegalAccessException e )
            {
                throw new ClientErrorException( 400, e.getMessage() );
            }
        }

        if ( credential != null )
        {
            parameters = credential.populate( parameters );
        }
        return adapter.callbackNewInstance( adaptee, remoteRequest, resource, parameters, locale );
    }

    @Override
    public Request<T> config( RequestCredential credential )
    {
        this.credential = credential;
        return this;
    }
}
