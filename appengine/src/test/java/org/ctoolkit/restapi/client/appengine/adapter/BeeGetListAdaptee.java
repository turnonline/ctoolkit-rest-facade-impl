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

package org.ctoolkit.restapi.client.appengine.adapter;

import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteBee;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class BeeGetListAdaptee
        extends AbstractGoogleClientAdaptee<FakeClient>
        implements GetExecutorAdaptee<RemoteBee>, ListExecutorAdaptee<RemoteBee>
{
    @Inject
    public BeeGetListAdaptee( FakeClient client )
    {
        super( client );
    }

    @Override
    public Object prepareGet( @Nonnull Identifier identifier )
            throws IOException
    {
        return new RemoteRequest();
    }

    @Override
    public RemoteBee executeGet( @Nonnull Object request,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
            throws IOException
    {
        return new RemoteBee();
    }

    @Override
    public Object prepareList( @Nullable Identifier parentKey )
            throws IOException
    {
        return new RemoteRequest();
    }

    @Override
    public List<RemoteBee> executeList( @Nonnull Object request,
                                        @Nullable Map<String, Object> criteria,
                                        @Nullable Locale locale,
                                        @Nullable Integer start,
                                        @Nullable Integer length,
                                        @Nullable String orderBy,
                                        @Nullable Boolean ascending )
            throws IOException
    {
        List<RemoteBee> bees = new ArrayList<>();
        bees.add( new RemoteBee() );

        return bees;
    }
}
