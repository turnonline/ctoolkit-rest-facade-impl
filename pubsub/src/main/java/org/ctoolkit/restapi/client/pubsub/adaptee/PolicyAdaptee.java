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
import com.google.api.services.pubsub.model.Policy;
import com.google.api.services.pubsub.model.SetIamPolicyRequest;
import com.google.common.base.Strings;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * The Pub/Sub's {@link Policy} adaptee implementation.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
public class PolicyAdaptee
        extends AbstractGoogleClientAdaptee<Pubsub>
        implements GetExecutorAdaptee<Policy>, InsertExecutorAdaptee<Policy>
{
    @Inject
    public PolicyAdaptee( Pubsub client )
    {
        super( client );
    }

    @Override
    public Object prepareGet( @Nonnull Identifier identifier ) throws IOException
    {
        return client().projects().subscriptions().getIamPolicy( identifier.getString() );
    }

    @Override
    public Policy executeGet( @Nonnull Object request,
                              @Nullable Map<String, Object> parameters,
                              @Nullable Locale locale )
            throws IOException
    {
        return Policy.class.cast( execute( request, parameters ) );
    }

    @Override
    public Object prepareInsert( @Nonnull Policy resource,
                                 @Nullable Identifier parentKey,
                                 @Nullable MediaProvider provider )
            throws IOException
    {
        if ( parentKey == null || Strings.isNullOrEmpty( parentKey.getString() ) )
        {
            String message = "REQUIRED: The 'resource' for which the policy is being specified." +
                    " See the operation documentation for the appropriate value for this field."
                    + " Use parent Identifier in order to setup 'resource'.";
            throw new IOException( message );
        }

        SetIamPolicyRequest policyRequest = new SetIamPolicyRequest();
        policyRequest.setPolicy( resource );

        return client().projects().subscriptions().setIamPolicy( parentKey.getString(), policyRequest );
    }

    @Override
    public Object executeInsert( @Nonnull Object request,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
            throws IOException
    {
        return execute( request, parameters );
    }
}
