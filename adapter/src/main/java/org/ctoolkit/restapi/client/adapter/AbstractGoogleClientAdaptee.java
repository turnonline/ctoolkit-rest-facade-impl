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

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.AbstractInputStreamContent;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The base adaptee implementation.
 *
 * @param <C> the concrete type of the client instance
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class AbstractGoogleClientAdaptee<C>
{
    private final Provider<C> client;

    public AbstractGoogleClientAdaptee( Provider<C> client )
    {
        this.client = client;
    }

    protected final C client()
    {
        return client.get();
    }

    /**
     * Fill request with optional resource parameters added as URL query parameters.
     *
     * @param remoteRequest the Google API client request
     * @param parameters    the optional resource parameters
     */
    protected void fill( @Nonnull Object remoteRequest,
                         @Nullable Map<String, Object> parameters )
    {
        checkNotNull( remoteRequest );

        AbstractGoogleJsonClientRequest request = ( AbstractGoogleJsonClientRequest ) remoteRequest;

        if ( parameters != null )
        {
            for ( Map.Entry<String, Object> entrySet : parameters.entrySet() )
            {
                Object value = entrySet.getValue();
                if ( value instanceof Enum )
                {
                    value = ( ( Enum ) value ).name();
                }
                request.set( entrySet.getKey(), value );
            }
        }
    }

    protected final AbstractInputStreamContent media( @Nullable MediaProvider provider )
    {
        if ( provider == null )
        {
            return null;
        }

        return ( AbstractInputStreamContent ) provider.getMedia();
    }

    /**
     * Fill request with optional resource parameters and execute a remote call.
     *
     * @param request    the Google API client request
     * @param parameters the optional resource (query) parameters
     * @return the response of the remote call
     * @throws IOException might be thrown during remote call execution
     */
    public Object execute( @Nonnull Object request,
                           @Nullable Map<String, Object> parameters )
            throws IOException
    {
        checkNotNull( request );

        fill( request, parameters );
        return ( ( AbstractGoogleJsonClientRequest ) request ).execute();
    }
}
