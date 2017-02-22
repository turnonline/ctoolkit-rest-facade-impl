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
import org.ctoolkit.restapi.client.adaptee.DownloadExecutorAdaptee;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;
import org.ctoolkit.restapi.client.appengine.adapter.model.RemoteFoo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class FooGetAdaptee
        extends AbstractGoogleClientAdaptee<FakeClient, RemoteFoo>
        implements DownloadExecutorAdaptee<RemoteFoo>
{
    @Inject
    public FooGetAdaptee( FakeClient client )
    {
        super( client );
    }

    @Override
    public Object prepareGet( @Nonnull Identifier identifier )
            throws IOException
    {
        return new FakeClient();
    }

    @Override
    public RemoteFoo executeGet( @Nonnull Object request,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
            throws IOException
    {
        return new RemoteFoo();
    }

    @Override
    public URL prepareDownloadUrl( @Nonnull Identifier identifier, @Nullable String s, @Nullable Map<String, Object> map, @Nullable Locale locale )
    {
        try
        {
            String spec = "https://github.com/turnonline/ctoolkit-rest-facade-impl/blob/master/pom.xml";
            return new URL( spec );
        }
        catch ( MalformedURLException ignored )
        {
        }
        return null;
    }

    @Override
    public String getApiPrefix()
    {
        return null;
    }
}
