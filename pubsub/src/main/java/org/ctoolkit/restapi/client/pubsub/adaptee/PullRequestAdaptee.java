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
import com.google.api.services.pubsub.model.PullRequest;
import com.google.common.base.Strings;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adapter.AbstractInsertExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * The Pub/Sub's {@link PullRequest} adaptee implementation.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
public class PullRequestAdaptee
        extends AbstractInsertExecutorAdaptee<Pubsub, PullRequest>
{
    @Inject
    public PullRequestAdaptee( Pubsub client )
    {
        super( client );
    }

    @Override
    public Object prepareInsert( @Nonnull PullRequest resource,
                                 @Nullable Identifier parentKey,
                                 @Nullable MediaProvider provider )
            throws IOException
    {
        if ( parentKey == null || Strings.isNullOrEmpty( parentKey.getString() ) )
        {
            String message = "The subscription from which messages should be pulled is required."
                    + " Use parent Identifier. Format is 'projects/{project}/subscriptions/{subscription}'.";
            throw new IOException( message );
        }

        return client().projects().subscriptions().pull( parentKey.getString(), resource );
    }
}
