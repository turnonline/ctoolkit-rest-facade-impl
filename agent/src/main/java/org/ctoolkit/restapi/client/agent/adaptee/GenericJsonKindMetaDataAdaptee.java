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

package org.ctoolkit.restapi.client.agent.adaptee;

import org.ctoolkit.api.agent.CtoolkitAgent;
import org.ctoolkit.api.agent.CtoolkitAgentRequest;
import org.ctoolkit.api.agent.model.KindMetaData;
import org.ctoolkit.api.agent.model.KindMetaDataCollection;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class GenericJsonKindMetaDataAdaptee
        extends AbstractGoogleClientAdaptee<Provider<CtoolkitAgent>, KindMetaData>
        implements ListExecutorAdaptee<KindMetaData>
{
    @Inject
    public GenericJsonKindMetaDataAdaptee( Provider<CtoolkitAgent> client )
    {
        super( client );
    }

    @Override
    public Object prepareList( @Nullable Identifier parentKey ) throws IOException
    {
        return client().get().metadata().kind().list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<KindMetaData> executeList( @Nonnull Object o, @Nullable Map<String, Object> map, @Nullable Locale locale, int start, int length )
            throws IOException
    {
        CtoolkitAgentRequest<KindMetaDataCollection> request = ( CtoolkitAgentRequest<KindMetaDataCollection> ) o;

        fill( request, map, locale );
        return request.execute().getItems();
    }
}
