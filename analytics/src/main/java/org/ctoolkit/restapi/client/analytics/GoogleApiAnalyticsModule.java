/*
 * Copyright (c) 2018 Comvai, s.r.o. All Rights Reserved.
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

package org.ctoolkit.restapi.client.analytics;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.ctoolkit.restapi.client.AccessToken;
import org.ctoolkit.restapi.client.ApiToken;
import org.ctoolkit.restapi.client.RemoteServerErrorException;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

/**
 * The Google Analytics guice module as a default configuration.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GoogleApiAnalyticsModule
        extends AbstractModule
{
    public static final String API_PREFIX = "analytics";

    private static final Logger logger = LoggerFactory.getLogger( GoogleApiAnalyticsModule.class );

    private ApiToken<? extends HttpRequestInitializer> initialized;

    @Override
    protected void configure()
    {
    }

    @Provides
    @Singleton
    Analytics provideAnalytics( GoogleApiProxyFactory factory )
    {
        Set<String> scopes = AnalyticsScopes.all();
        Analytics.Builder builder;

        try
        {
            initialized = factory.authorize( scopes, null, API_PREFIX );
            HttpRequestInitializer credential = initialized.getCredential();
            builder = new Analytics.Builder( factory.getHttpTransport(), factory.getJsonFactory(), credential );
            builder.setApplicationName( factory.getApplicationName( API_PREFIX ) );
        }
        catch ( GeneralSecurityException e )
        {
            logger.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + factory.getApplicationName( API_PREFIX )
                    + " Service account: " + factory.getServiceAccountEmail( API_PREFIX ), e );
            throw new UnauthorizedException( e.getMessage() );
        }
        catch ( IOException e )
        {
            logger.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + factory.getApplicationName( API_PREFIX )
                    + " Service account: " + factory.getServiceAccountEmail( API_PREFIX ), e );

            throw new RemoteServerErrorException( HttpStatusCodes.STATUS_CODE_SERVER_ERROR, e.getMessage() );
        }

        return builder.build();
    }

    @Provides
    @AccessToken( apiName = API_PREFIX )
    ApiToken.Data provideAnalyticsTokenData( Analytics client )
    {
        initialized.setServiceUrl( client.getBaseUrl() );
        return initialized.getTokenData();
    }
}
