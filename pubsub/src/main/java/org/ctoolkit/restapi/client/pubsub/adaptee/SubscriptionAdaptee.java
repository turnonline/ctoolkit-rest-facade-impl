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

package org.ctoolkit.restapi.client.pubsub.adaptee;

import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.model.Subscription;
import com.google.common.base.Strings;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The Pub/Sub's {@link Subscription} adaptee implementation.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
public class SubscriptionAdaptee
        extends AbstractGoogleClientAdaptee<Pubsub>
        implements GetExecutorAdaptee<Subscription>, ListExecutorAdaptee<Subscription>,
        InsertExecutorAdaptee<Subscription>, DeleteExecutorAdaptee<Subscription>
{
    @Inject
    public SubscriptionAdaptee( Provider<Pubsub> client )
    {
        super( client );
    }

    @Override
    public Object prepareGet( @Nonnull Identifier identifier ) throws IOException
    {
        return client().projects().subscriptions().get( identifier.getString() );
    }

    @Override
    public Subscription executeGet( @Nonnull Object request,
                                    @Nullable Map<String, Object> parameters,
                                    @Nullable Locale locale )
            throws IOException
    {
        return Subscription.class.cast( execute( request, parameters ) );
    }

    @Override
    public Object prepareList( @Nullable Identifier parentKey )
            throws IOException
    {
        if ( parentKey == null || Strings.isNullOrEmpty( parentKey.getString() ) )
        {
            String message = "The name of the cloud project that subscriptions belong to is required."
                    + " Use parent Identifier. Format is 'projects/{project}'.";
            throw new IOException( message );
        }

        return client().projects().subscriptions().list( parentKey.getString() );
    }

    @Override
    public List<Subscription> executeList( @Nonnull Object request,
                                           @Nullable Map<String, Object> parameters,
                                           @Nullable Locale locale,
                                           @Nullable Integer start,
                                           @Nullable Integer length,
                                           @Nullable String orderBy,
                                           @Nullable Boolean ascending )
            throws IOException
    {
        Pubsub.Projects.Subscriptions.List list = ( Pubsub.Projects.Subscriptions.List ) request;
        if ( length != null && length > 0 )
        {
            list.setPageSize( length );
        }

        fill( request, parameters );
        return list.execute().getSubscriptions();
    }

    @Override
    public Object prepareInsert( @Nonnull Subscription resource,
                                 @Nullable Identifier parentKey,
                                 @Nullable MediaProvider provider )
            throws IOException
    {
        if ( parentKey == null || Strings.isNullOrEmpty( parentKey.getString() ) )
        {
            String message = "Name of the subscription is required and must be defined by the client." +
                    " Format is 'projects/{project}/subscriptions/{subscription}'." +
                    " Use parent Identifier in order to configure subscription name.";
            throw new IOException( message );
        }

        // API doc says create, however under the hood PUT operation is responsible to create a subscription
        return client().projects().subscriptions().create( parentKey.getString(), resource );
    }

    @Override
    public Object executeInsert( @Nonnull Object request,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
            throws IOException
    {
        return execute( request, parameters );
    }

    @Override
    public Object prepareDelete( @Nonnull Identifier identifier )
            throws IOException
    {
        return client().projects().subscriptions().delete( identifier.getString() );
    }

    @Override
    public Object executeDelete( @Nonnull Object request,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
            throws IOException
    {
        return execute( request, parameters );
    }
}
