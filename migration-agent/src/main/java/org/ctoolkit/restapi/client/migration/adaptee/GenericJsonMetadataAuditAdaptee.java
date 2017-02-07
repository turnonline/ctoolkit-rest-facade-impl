/*
 * Copyright (c) 2016 Comvai, s.r.o. All Rights Reserved.
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

package org.ctoolkit.restapi.client.migration.adaptee;

import org.ctoolkit.api.migration.CtoolkitAgent;
import org.ctoolkit.api.migration.CtoolkitAgentRequest;
import org.ctoolkit.api.migration.model.MetadataAudit;
import org.ctoolkit.api.migration.model.MetadataAuditCollection;
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
 * The MetadataAudit as {@link MetadataAudit} concrete adaptee implementation.
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class GenericJsonMetadataAuditAdaptee
        extends AbstractGoogleClientAdaptee<Provider<CtoolkitAgent>, MetadataAudit>
        implements ListExecutorAdaptee<MetadataAudit>

{
    @Inject
    public GenericJsonMetadataAuditAdaptee( Provider<CtoolkitAgent> ctoolkitAgent )
    {
        super( ctoolkitAgent );
    }


    @Override
    public Object prepareList( @Nullable Identifier parentKey ) throws IOException
    {
        return client().get().audit().list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<MetadataAudit> executeList( @Nonnull Object o, @Nullable Map<String, Object> parameters, @Nullable Locale locale, int start, int length )
            throws IOException
    {
        CtoolkitAgentRequest<MetadataAuditCollection> request = ( CtoolkitAgentRequest<MetadataAuditCollection> ) o;

        fill( request, parameters, locale );
        return request.execute().getItems();
    }
}
