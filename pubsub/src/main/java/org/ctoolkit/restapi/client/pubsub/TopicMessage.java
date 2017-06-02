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

import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.text.MessageFormat;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Pub/Sub class with messages to be published via specified topic.
 * <p>
 * It acts as a resource and it's associated with adaptee {@link org.ctoolkit.restapi.client.pubsub.adaptee.TopicMessageAdaptee}
 * implementation. The instance of this class might be used directly via
 * {@link org.ctoolkit.restapi.client.ResourceFacade}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class TopicMessage
{
    private static final String TOPIC_PATH_TEMPLATE = "projects/{0}/topics/{1}";

    private final String topic;

    private List<PubsubMessage> messages;

    private TopicMessage( Builder builder )
    {
        checkArgument( !Strings.isNullOrEmpty( builder.projectId ) );
        checkArgument( !Strings.isNullOrEmpty( builder.topicId ) );
        checkNotNull( builder.messages );
        checkArgument( !builder.messages.isEmpty() );

        this.messages = builder.messages;
        this.topic = MessageFormat.format( TOPIC_PATH_TEMPLATE, builder.projectId, builder.topicId );
    }

    /**
     * Creates a topic with message.
     *
     * @param projectId the unique project identifier (publisher) of the content
     * @param topicId   the target topic where a message will be published
     * @param data      the message to be published
     * @return the populated instance
     */
    public static TopicMessage create( @Nonnull String projectId, @Nonnull String topicId, @Nonnull byte[] data )
    {
        List<PubsubMessage> list = Lists.newArrayList( new PubsubMessage().encodeData( data ) );
        return new Builder().setProjectId( projectId ).setTopicId( topicId ).setMessages( list ).build();
    }

    /**
     * Creates a topic with message.
     *
     * @param projectId the unique project identifier (publisher) of the content
     * @param topicId   the target topic where a message will be published
     * @param data      the message to be published
     * @return the populated instance
     */
    public static TopicMessage create( @Nonnull String projectId, @Nonnull String topicId, @Nonnull String data )
    {
        checkArgument( !Strings.isNullOrEmpty( data ) );

        byte[] bytes = data.getBytes( Charsets.UTF_8 );
        List<PubsubMessage> list = Lists.newArrayList( new PubsubMessage().encodeData( bytes ) );
        return new Builder().setProjectId( projectId ).setTopicId( topicId ).setMessages( list ).build();
    }

    /**
     * Creates a topic with message.
     *
     * @param projectId the unique project identifier (publisher) of the content
     * @param topicId   the target topic where a message will be published
     * @param messages  the list of messages to be published
     * @return the populated instance
     */
    public static TopicMessage create( @Nonnull String projectId,
                                       @Nonnull String topicId,
                                       List<PubsubMessage> messages )
    {
        return new Builder().setProjectId( projectId ).setTopicId( topicId ).setMessages( messages ).build();
    }

    public String getTopic()
    {
        return topic;
    }

    public List<PubsubMessage> getMessages()
    {
        return messages;
    }

    @Override
    public String toString()
    {
        return "TopicMessage{" +
                "topic='" + topic + '\'' +
                ", messages=" + ( messages == null ? "0" : String.valueOf( messages.size() ) ) +
                '}';
    }

    private static class Builder
    {
        private String projectId;

        private String topicId;

        private List<PubsubMessage> messages;

        private Builder()
        {
        }

        Builder setProjectId( String projectId )
        {
            this.projectId = projectId;
            return this;
        }

        Builder setTopicId( String topicId )
        {
            this.topicId = topicId;
            return this;
        }

        Builder setMessages( List<PubsubMessage> messages )
        {
            this.messages = messages;
            return this;
        }

        TopicMessage build()
        {
            return new TopicMessage( this );
        }
    }
}
