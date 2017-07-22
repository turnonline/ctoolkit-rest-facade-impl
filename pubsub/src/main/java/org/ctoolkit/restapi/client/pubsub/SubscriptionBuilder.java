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

import com.google.api.services.pubsub.model.PushConfig;
import com.google.api.services.pubsub.model.Subscription;
import com.google.common.base.Strings;
import org.ctoolkit.restapi.client.Identifier;

import javax.annotation.Nonnull;
import java.text.MessageFormat;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Pub/Sub subscription builder.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 * @see com.google.api.services.pubsub.model.Subscription
 */
public class SubscriptionBuilder
{
    private static final String SUBSCRIPTION_PATH_TEMPLATE = "projects/{project}/subscriptions/{subscription}";

    private String projectId;

    private String topicId;

    private Integer ackDeadlineSeconds;

    private PushConfig pushConfig;

    private SubscriptionBuilder()
    {
    }

    /**
     * Creates full subscription name represented by {@link Identifier}.
     *
     * @param projectId      the ID of the project as part of the full subscription name.
     * @param subscriptionId the ID of the subscription as part of the full subscription name.
     * @return the subscription name represented by identifier
     */
    public static Identifier create( @Nonnull String projectId, @Nonnull String subscriptionId )
    {
        return new Identifier( MessageFormat.format( SUBSCRIPTION_PATH_TEMPLATE, projectId, subscriptionId ) );
    }

    /**
     * Creates a subscription builder.
     *
     * @return the builder instance
     */
    public static SubscriptionBuilder newBuilder()
    {
        return new SubscriptionBuilder();
    }

    /**
     * Sets the ID of the project from which this subscription is receiving messages.
     *
     * @return the builder
     */
    public SubscriptionBuilder setProjectId( @Nonnull String projectId )
    {
        checkArgument( !Strings.isNullOrEmpty( projectId ), "Project ID must be non empty string." );
        this.projectId = projectId;
        return this;
    }

    /**
     * Sets the name of the topic from which this subscription is receiving messages.
     *
     * @return the builder
     */
    public SubscriptionBuilder setTopicId( @Nonnull String topicId )
    {
        checkArgument( !Strings.isNullOrEmpty( topicId ), "Topic ID must be non empty string." );
        this.topicId = topicId;
        return this;
    }

    /**
     * See {@link Subscription#setAckDeadlineSeconds(Integer)}.
     *
     * @return the builder
     */
    public SubscriptionBuilder setAckDeadlineSeconds( Integer ackDeadlineSeconds )
    {
        this.ackDeadlineSeconds = ackDeadlineSeconds;
        return this;
    }

    /**
     * See {@link Subscription#setPushConfig(PushConfig)}.
     *
     * @return the builder
     */
    public SubscriptionBuilder setPushConfig( PushConfig pushConfig )
    {
        this.pushConfig = pushConfig;
        return this;
    }

    public Subscription build()
    {
        Subscription subscription = new Subscription();
        checkNotNull( this.projectId );
        checkNotNull( this.topicId );

        String topic = MessageFormat.format( TopicMessage.TOPIC_PATH_TEMPLATE, projectId, topicId );
        subscription.setTopic( topic );
        subscription.setAckDeadlineSeconds( this.ackDeadlineSeconds );
        subscription.setPushConfig( this.pushConfig );

        return subscription;
    }
}
