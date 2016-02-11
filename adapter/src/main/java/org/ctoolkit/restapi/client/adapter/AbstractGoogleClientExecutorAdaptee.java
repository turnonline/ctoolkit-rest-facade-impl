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

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.json.GenericJson;
import org.ctoolkit.restapi.client.adaptee.RestExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Google Client API specific abstract implementation of the {@link RestExecutorAdaptee}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public abstract class AbstractGoogleClientExecutorAdaptee<M extends GenericJson, R extends AbstractGoogleJsonClientRequest, K>
        implements RestExecutorAdaptee<M, R, K>
{
    /**
     * Accept optional language.
     *
     * @param request the Google API client request
     * @param locale  the optional locale to be set
     */
    protected void acceptLanguage( @Nonnull AbstractGoogleJsonClientRequest request, @Nullable Locale locale )
    {
        checkNotNull( request );
        acceptLanguage( request.getRequestHeaders(), locale );
    }

    /**
     * Accept optional language.
     *
     * @param headers the Google API client HTTP headers
     * @param locale  the optional locale to be set
     */
    protected void acceptLanguage( @Nonnull HttpHeaders headers, @Nullable Locale locale )
    {
        checkNotNull( headers );

        if ( locale != null )
        {
            String languageTag = new java.util.Locale( locale.getLanguage(), locale.getCountry() ).toLanguageTag();
            headers.put( com.google.common.net.HttpHeaders.ACCEPT_LANGUAGE, languageTag );
        }
    }

    /**
     * Fill optional resource parameters or criteria.
     *
     * @param request  the Google API client request
     * @param criteria the optional resource parameters
     */
    protected void fillCriteria( @Nonnull AbstractGoogleJsonClientRequest request,
                                 @Nullable Map<String, Object> criteria )
    {
        checkNotNull( request );

        if ( criteria != null )
        {
            for ( Map.Entry<String, Object> entrySet : criteria.entrySet() )
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

    /**
     * Fill {@link #fillCriteria(AbstractGoogleJsonClientRequest, Map)} and {@link #acceptLanguage(HttpHeaders, Locale)}
     *
     * @param request  the Google API client request
     * @param criteria the optional resource parameters
     * @param locale   the optional locale to be set
     */
    protected void fill( @Nonnull AbstractGoogleJsonClientRequest request,
                         @Nullable Map<String, Object> criteria,
                         @Nullable Locale locale )
    {
        checkNotNull( request );

        fillCriteria( request, criteria );
        acceptLanguage( request, locale );
    }
}
