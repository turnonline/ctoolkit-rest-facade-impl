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

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * The {@link PubsubMessage} listener interface.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public interface PubsubMessageListener
        extends Serializable
{
    /**
     * Called once a message has been received for given subscription.
     * In case of message processing failure it throws exception to signal re-try.
     * The implementation must be idempotent to correctly process on re-try.
     *
     * @param message      the published message to be processed
     * @param subscription the subscription that received push message
     * @throws Exception the exception thrown in case of failure
     */
    void onMessage( @Nonnull PubsubMessage message, @Nonnull String subscription )
            throws Exception;
}
