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

package org.ctoolkit.restapi.client.maps;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GaeRequestHandler;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApiRequest;
import org.ctoolkit.restapi.client.adapter.GoogleApiProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Google Maps API guice module as a default configuration.
 * Provides Geo API context to make a request to following maps APIs:
 * <ul>
 * <li>Directions API, ask to inject {@link DirectionsApiRequest}</li>
 * <li>Distance Matrix API, ask to inject {@link DistanceMatrixApiRequest}</li>
 * <li>Elevation API</li>
 * <li>Geocoding API, ask to inject {@link GeocodingApiRequest}</li>
 * <li>Places API</li>
 * <li>Roads API</li>
 * <li>Time Zone API</li>
 * </ul>
 * If property <b>{@code credential.maps.numberOfRetries}</b> value is less or equal to 0 (default value is 1)
 * retries will be disabled <b>{@code .disableRetries()}</b>.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 * @see GeoApiContext.Builder
 */
public class GoogleApiMapsModule
        extends AbstractModule
{
    public static final String API_PREFIX = "maps";

    private static final Logger logger = LoggerFactory.getLogger( GoogleApiMapsModule.class );

    private GeoApiContext context;

    @Override
    protected void configure()
    {
    }

    private GeoApiContext provideGeoApiContext( GoogleApiProxyFactory factory )
    {
        String apiKey = checkNotNull( factory.getApiKey( API_PREFIX ), "The API key is missing!" );

        int numberOfRetries = factory.getNumberOfRetries( API_PREFIX );
        boolean disableRetries = numberOfRetries <= 0;

        GeoApiContext.Builder builder = new GeoApiContext.Builder( new GaeRequestHandler.Builder() ).apiKey( apiKey );

        if ( disableRetries )
        {
            builder.disableRetries();
        }
        else
        {
            builder.maxRetries( numberOfRetries );
        }
        GeoApiContext context = builder.build();
        logger.info( "GeoApiContext has been initialized." );

        return context;
    }

    @Provides
    DirectionsApiRequest provideDirectionsApiRequest( GoogleApiProxyFactory factory )
    {
        if ( context == null )
        {
            context = provideGeoApiContext( factory );
        }
        return DirectionsApi.newRequest( context );
    }

    @Provides
    DistanceMatrixApiRequest provideDistanceMatrixApiRequest( GoogleApiProxyFactory factory )
    {
        if ( context == null )
        {
            context = provideGeoApiContext( factory );
        }
        return DistanceMatrixApi.newRequest( context );
    }

    @Provides
    GeocodingApiRequest provideGeocodingApiRequest( GoogleApiProxyFactory factory )
    {
        if ( context == null )
        {
            context = provideGeoApiContext( factory );
        }
        return new GeocodingApiRequest( context );
    }
}
