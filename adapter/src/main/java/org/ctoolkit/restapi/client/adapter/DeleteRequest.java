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

import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.SingleRequest;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The DELETE request implementation that delegates callback to related adapter.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class DeleteRequest
        implements SingleRequest<Void>
{
    private final Class resource;

    private final Object identifier;

    private final ResourceFacadeAdapter adapter;

    private final DeleteExecutorAdaptee adaptee;

    private final Object remoteRequest;

    private RequestCredential credential;

    DeleteRequest( @Nonnull Class resource,
                   @Nonnull Object identifier,
                   @Nonnull ResourceFacadeAdapter adapter,
                   @Nonnull DeleteExecutorAdaptee adaptee,
                   @Nonnull Object remoteRequest )
    {
        this.resource = checkNotNull( resource );
        this.identifier = checkNotNull( identifier );
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
    public Void execute()
    {
        return execute( null, null );
    }

    @Override
    public Void execute( Map<String, Object> parameters )
    {
        return execute( parameters, null );
    }

    @Override
    public Void execute( Locale locale )
    {
        return execute( null, locale );
    }

    @Override
    public Void execute( Map<String, Object> parameters, Locale locale )
    {
        if ( credential != null )
        {
            parameters = credential.populate( parameters );
        }
        return adapter.callbackExecuteDelete( adaptee, remoteRequest, resource, identifier, parameters, locale );
    }

    @Override
    public Request<Void> config( RequestCredential credential )
    {
        this.credential = credential;
        return this;
    }
}
