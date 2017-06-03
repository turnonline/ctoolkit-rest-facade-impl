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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        checkNotNull( builder.projectId );
        checkNotNull( builder.topicId );
        checkArgument( !builder.messages.isEmpty(), "At least one message must be provided." );

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
        return newBuilder().setProjectId( projectId ).setTopicId( topicId ).addMessage( data ).build();
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
        return newBuilder().setProjectId( projectId ).setTopicId( topicId ).addMessage( data ).build();
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
        return newBuilder().setProjectId( projectId ).setTopicId( topicId ).setMessages( messages ).build();
    }

    /**
     * Creates a new topic builder.
     *
     * @return the builder instance
     */
    public static Builder newBuilder()
    {
        return new Builder();
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

    public static class Builder
    {
        private String projectId;

        private String topicId;

        private List<PubsubMessage> messages;

        private Builder()
        {
            messages = new ArrayList<>();
        }

        /**
         * Set the project ID.
         *
         * @return the builder
         */
        public Builder setProjectId( @Nonnull String projectId )
        {
            checkArgument( !Strings.isNullOrEmpty( projectId ), "Project ID must be non empty string." );
            this.projectId = projectId;
            return this;
        }

        /**
         * Set the topic ID.
         *
         * @return the builder
         */
        public Builder setTopicId( @Nonnull String topicId )
        {
            checkArgument( !Strings.isNullOrEmpty( topicId ), "Topic ID must be non empty string." );
            this.topicId = topicId;
            return this;
        }

        /**
         * Set the list of messages.
         *
         * @param messages the list of messages
         * @return the builder
         */
        public Builder setMessages( @Nonnull List<PubsubMessage> messages )
        {
            this.messages.addAll( checkNotNull( messages ) );
            return this;
        }

        /**
         * Add message payload.
         *
         * @param message the message payload
         * @return the builder
         */
        public Builder addMessage( @Nonnull PubsubMessage message )
        {
            this.messages.add( checkNotNull( message ) );
            return this;
        }

        /**
         * Add message payload.
         *
         * @param data the message payload
         * @return the builder
         */
        public Builder addMessage( @Nonnull String data )
        {
            return addMessage( data, null );
        }

        /**
         * Add message payload with attribute.
         *
         * @param data  the message payload
         * @param key   the attribute key
         * @param value the attribute value
         * @return the builder
         */
        public Builder addMessage( @Nonnull String data, @Nonnull String key, @Nonnull String value )
        {
            Map<String, String> attributes = buildAttributes( key, value );
            return addMessage( data, attributes );
        }

        /**
         * Add message payload with optional attributes.
         *
         * @param data       the message payload
         * @param attributes the optional map of attributes
         * @return the builder
         */
        public Builder addMessage( @Nonnull String data, @Nullable Map<String, String> attributes )
        {
            checkArgument( !Strings.isNullOrEmpty( data ), "Data must be non empty string." );

            byte[] bytes = data.getBytes( Charsets.UTF_8 );
            PubsubMessage message = new PubsubMessage().encodeData( bytes );

            if ( attributes != null && !attributes.isEmpty() )
            {
                message.setAttributes( attributes );
            }
            this.messages.add( message );

            return this;
        }

        /**
         * Add message payload.
         *
         * @param bytes the message payload
         * @return the builder
         */
        public Builder addMessage( @Nonnull byte[] bytes )
        {
            return addMessage( bytes, null );
        }

        /**
         * Add message payload with attribute.
         *
         * @param bytes the message payload
         * @param key   the attribute key
         * @param value the attribute value
         * @return the builder
         */
        public Builder addMessage( @Nonnull byte[] bytes, @Nonnull String key, @Nonnull String value )
        {
            Map<String, String> attributes = buildAttributes( key, value );
            return addMessage( bytes, attributes );
        }

        /**
         * Add message payload with optional attributes.
         *
         * @param bytes      the message payload
         * @param attributes the optional map of attributes
         * @return the builder
         */
        public Builder addMessage( @Nonnull byte[] bytes, @Nullable Map<String, String> attributes )
        {
            PubsubMessage message = new PubsubMessage().encodeData( checkNotNull( bytes ) );

            if ( attributes != null && !attributes.isEmpty() )
            {
                message.setAttributes( attributes );
            }

            this.messages.add( message );
            return this;
        }

        private Map<String, String> buildAttributes( @Nonnull String key, @Nonnull String value )
        {
            checkArgument( !Strings.isNullOrEmpty( key ), "Key must be non empty string." );
            checkArgument( !Strings.isNullOrEmpty( value ), "Value must be non empty string." );

            Map<String, String> attributes = new HashMap<>();
            attributes.put( key, value );
            return attributes;
        }

        public TopicMessage build()
        {
            return new TopicMessage( this );
        }
    }
}
