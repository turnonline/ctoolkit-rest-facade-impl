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
import org.ctoolkit.api.migration.model.ChangeBatch;
import org.ctoolkit.api.migration.model.ChangeBatchCollection;
import org.ctoolkit.api.migration.model.ImportBatch;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The ImportBatch as {@link ImportBatch} concrete adaptee implementation.
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class GenericJsonChangeBatchAdaptee
        extends AbstractGoogleClientAdaptee<CtoolkitAgent, ChangeBatch>
        implements
        GetExecutorAdaptee<ChangeBatch>,
        InsertExecutorAdaptee<ChangeBatch>,
        UpdateExecutorAdaptee<ChangeBatch>,
        ListExecutorAdaptee<ChangeBatch>,
        DeleteExecutorAdaptee<ChangeBatch>
{
    @Inject
    public GenericJsonChangeBatchAdaptee( CtoolkitAgent ctoolkitAgent )
    {
        super( ctoolkitAgent );
    }

    @Override
    public Object prepareGet( @Nonnull Identifier identifier )
            throws IOException
    {
        checkNotNull( identifier );

        return client().change().get( identifier.getString() );
    }

    @Override
    public ChangeBatch executeGet( @Nonnull Object request,
                               @Nullable Map<String, Object> parameters,
                               @Nullable Locale locale )
            throws IOException
    {
        fill( get( request ), parameters, locale );
        return execute( request );
    }

    @Override
    public Object prepareInsert( @Nonnull ChangeBatch resource,
                                 @Nullable Identifier parentKey,
                                 @Nullable MediaProvider<?> provider )
            throws IOException
    {
        checkNotNull( resource );
        return client().change().insert( resource );
    }

    @Override
    public ChangeBatch executeInsert( @Nonnull Object request,
                                  @Nullable Map<String, Object> parameters,
                                  @Nullable Locale locale )
            throws IOException
    {
        fill( get( request ), parameters, locale );
        return execute( request );
    }

    @Override
    public Object prepareUpdate( @Nonnull ChangeBatch resource,
                                 @Nonnull Identifier identifier,
                                 @Nullable MediaProvider<?> provider )
            throws IOException
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        return client().change().update( identifier.getString(), resource );
    }

    @Override
    public ChangeBatch executeUpdate( @Nonnull Object request,
                                  @Nullable Map<String, Object> parameters,
                                  @Nullable Locale locale )
            throws IOException
    {
        fill( get( request ), parameters, locale );
        return execute( request );
    }

    @Override
    public Object prepareDelete( @Nonnull Identifier identifier ) throws IOException
    {
        checkNotNull( identifier );
        return client().change().delete( identifier.getString() );
    }

    @Override
    public void executeDelete( @Nonnull Object o, @Nullable Locale locale ) throws IOException
    {
        execute( o );
    }

    @Override
    public Object prepareList( @Nullable Identifier identifier ) throws IOException
    {
        return client().change().list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<ChangeBatch> executeList( @Nonnull Object o, @Nullable Map<String, Object> map, @Nullable Locale locale, int i, int i1 )
            throws IOException
    {
        CtoolkitAgentRequest<ChangeBatchCollection> request = ( CtoolkitAgentRequest<ChangeBatchCollection> ) o;

        fill( request, map, locale );
        return request.execute().getItems();
    }
}
