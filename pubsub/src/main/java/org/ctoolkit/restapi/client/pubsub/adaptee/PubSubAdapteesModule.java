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

import com.google.api.services.pubsub.model.AcknowledgeRequest;
import com.google.api.services.pubsub.model.ModifyAckDeadlineRequest;
import com.google.api.services.pubsub.model.ModifyPushConfigRequest;
import com.google.api.services.pubsub.model.Policy;
import com.google.api.services.pubsub.model.PublishRequest;
import com.google.api.services.pubsub.model.PullRequest;
import com.google.api.services.pubsub.model.Subscription;
import com.google.api.services.pubsub.model.TestIamPermissionsRequest;
import com.google.api.services.pubsub.model.Topic;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.pubsub.TopicMessage;

/**
 * The guice module with all Pub/Sub resource model adaptees injection configuration.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class PubSubAdapteesModule
        extends AbstractModule
{
    @Override
    protected void configure()
    {
        // adaptee for PublishRequest
        bind( new TypeLiteral<InsertExecutorAdaptee<PublishRequest>>()
        {
        } ).to( PublishAdaptee.class );

        // adaptee for TopicMessage
        bind( new TypeLiteral<InsertExecutorAdaptee<TopicMessage>>()
        {
        } ).to( TopicMessageAdaptee.class );

        // policy adaptee
        bind( new TypeLiteral<GetExecutorAdaptee<Policy>>()
        {
        } ).to( PolicyAdaptee.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<Policy>>()
        {
        } ).to( PolicyAdaptee.class );

        // TestIamPermissionsRequest adaptee
        bind( new TypeLiteral<InsertExecutorAdaptee<TestIamPermissionsRequest>>()
        {
        } ).to( TestIamPermissionsAdaptee.class );

        // PullRequest adaptee
        bind( new TypeLiteral<InsertExecutorAdaptee<PullRequest>>()
        {
        } ).to( PullRequestAdaptee.class );

        // ModifyAckDeadlineRequest adaptee
        bind( new TypeLiteral<UpdateExecutorAdaptee<ModifyAckDeadlineRequest>>()
        {
        } ).to( ModifyAckDeadlineAdaptee.class );

        // ModifyPushConfigRequest adaptee
        bind( new TypeLiteral<UpdateExecutorAdaptee<ModifyPushConfigRequest>>()
        {
        } ).to( ModifyPushConfigAdaptee.class );

        // topic adaptee
        bind( new TypeLiteral<GetExecutorAdaptee<Topic>>()
        {
        } ).to( TopicAdaptee.class );

        bind( new TypeLiteral<ListExecutorAdaptee<Topic>>()
        {
        } ).to( TopicAdaptee.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<Topic>>()
        {
        } ).to( TopicAdaptee.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<Topic>>()
        {
        } ).to( TopicAdaptee.class );

        // subscription adaptee
        bind( new TypeLiteral<GetExecutorAdaptee<Subscription>>()
        {
        } ).to( SubscriptionAdaptee.class );

        bind( new TypeLiteral<ListExecutorAdaptee<Subscription>>()
        {
        } ).to( SubscriptionAdaptee.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<Subscription>>()
        {
        } ).to( SubscriptionAdaptee.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<Subscription>>()
        {
        } ).to( SubscriptionAdaptee.class );

        // subscription acknowledge adaptee
        bind( new TypeLiteral<InsertExecutorAdaptee<AcknowledgeRequest>>()
        {
        } ).to( SubscriptionAcknowledgeAdaptee.class );

    }
}
