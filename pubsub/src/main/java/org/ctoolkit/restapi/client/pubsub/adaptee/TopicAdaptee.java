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
import com.google.api.services.pubsub.model.Topic;
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
 * The Pub/Sub's {@link Topic} adaptee implementation.
 * {@link org.ctoolkit.restapi.client.pubsub.TopicMessage#create(String, String)}
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
public class TopicAdaptee
        extends AbstractGoogleClientAdaptee<Pubsub>
        implements GetExecutorAdaptee<Topic>, ListExecutorAdaptee<Topic>,
        InsertExecutorAdaptee<Topic>, DeleteExecutorAdaptee<Topic>
{
    @Inject
    public TopicAdaptee( Provider<Pubsub> client )
    {
        super( client );
    }

    @Override
    public Object prepareGet( @Nonnull Identifier identifier ) throws IOException
    {
        return client().projects().topics().get( identifier.getString() );
    }

    @Override
    public Topic executeGet( @Nonnull Object request,
                             @Nullable Map<String, Object> parameters,
                             @Nullable Locale locale )
            throws IOException
    {
        return Topic.class.cast( execute( request, parameters ) );
    }

    @Override
    public Object prepareList( @Nullable Identifier parentKey ) throws IOException
    {
        if ( parentKey == null || Strings.isNullOrEmpty( parentKey.getString() ) )
        {
            String message = "The name of the cloud project that topics belong to is required" +
                    " and must be defined by the client." +
                    " Format is 'projects/{project}'." +
                    " Use parent Identifier in order configure topic name.";
            throw new IOException( message );
        }

        return client().projects().topics().list( parentKey.getString() );
    }

    @Override
    public List<Topic> executeList( @Nonnull Object request,
                                    @Nullable Map<String, Object> parameters,
                                    @Nullable Locale locale,
                                    @Nullable Integer start,
                                    @Nullable Integer length,
                                    @Nullable String orderBy,
                                    @Nullable Boolean ascending )
            throws IOException
    {
        Pubsub.Projects.Topics.List list = ( Pubsub.Projects.Topics.List ) request;
        if ( length != null && length > 0 )
        {
            list.setPageSize( length );
        }

        fill( request, parameters );
        return list.execute().getTopics();
    }

    @Override
    public Object prepareInsert( @Nonnull Topic resource,
                                 @Nullable Identifier parentKey,
                                 @Nullable MediaProvider provider )
            throws IOException
    {
        if ( parentKey == null || Strings.isNullOrEmpty( parentKey.getString() ) )
        {
            String message = "Name of the topic is required and must be defined by the client." +
                    " Format is 'projects/{project}/topics/{topic}'." +
                    " Use parent Identifier in order to configure topic name.";
            throw new IOException( message );
        }
        // API doc says create, however under the hood PUT operation is responsible to create a new topic
        return client().projects().topics().create( parentKey.getString(), resource );
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
    public Object prepareDelete( @Nonnull Identifier identifier ) throws IOException
    {
        return client().projects().topics().delete( identifier.getString() );
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
