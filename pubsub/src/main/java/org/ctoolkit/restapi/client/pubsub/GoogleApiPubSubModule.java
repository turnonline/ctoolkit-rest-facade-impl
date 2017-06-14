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

package org.ctoolkit.restapi.client.pubsub;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.PubsubScopes;
import com.google.api.services.pubsub.model.PublishRequest;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.ctoolkit.restapi.client.RemoteServerErrorException;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.ctoolkit.restapi.client.pubsub.adaptee.PublishAdaptee;
import org.ctoolkit.restapi.client.pubsub.adaptee.TopicMessageAdaptee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

/**
 * The Google Pub/Sub guice module as a default configuration.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GoogleApiPubSubModule
        extends AbstractModule
{
    public static final String API_PREFIX = "pubsub";

    private static final Logger logger = LoggerFactory.getLogger( GoogleApiPubSubModule.class );

    @Override
    protected void configure()
    {
        // adaptee for PublishRequest
        bind( new TypeLiteral<InsertExecutorAdaptee<PublishRequest>>()
        {
        } ).to( PublishAdaptee.class ).in( Singleton.class );

        // adaptee for TopicMessage
        bind( new TypeLiteral<InsertExecutorAdaptee<TopicMessage>>()
        {
        } ).to( TopicMessageAdaptee.class ).in( Singleton.class );
    }

    @Provides
    @Singleton
    Pubsub providePubsub( GoogleApiProxyFactory factory )
    {
        Set<String> scopes = PubsubScopes.all();
        Pubsub.Builder builder;

        try
        {
            HttpRequestInitializer credential = factory.authorize( scopes, null, API_PREFIX );
            builder = new Pubsub.Builder( factory.getHttpTransport(), factory.getJsonFactory(), credential );
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
}
