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

import org.ctoolkit.api.agent.model.ImportBatch;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.RequestCredential;
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
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class GenericJsonImportBatchAdaptee
        extends AbstractGoogleClientAdaptee<CustomizedCtoolkitAgent>
        implements
        GetExecutorAdaptee<ImportBatch>,
        InsertExecutorAdaptee<ImportBatch>,
        UpdateExecutorAdaptee<ImportBatch>,
        ListExecutorAdaptee<ImportBatch>,
        DeleteExecutorAdaptee<ImportBatch>
{
    @Inject
    public GenericJsonImportBatchAdaptee( CustomizedCtoolkitAgent ctoolkitAgent )
    {
        super( ctoolkitAgent );
    }

    @Override
    public Object prepareGet( @Nonnull Identifier identifier )
            throws IOException
    {
        checkNotNull( identifier, "Identifier cannot be null" );

        return client().importBatch().get( identifier.getLong() );
    }

    @Override
    public ImportBatch executeGet( @Nonnull Object request,
                                   @Nullable Map<String, Object> parameters,
                                   @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        fill( request, parameters );
        return ( ( CustomizedCtoolkitAgent.ImportBatch.Get ) request ).execute( credential );
    }

    @Override
    public Object prepareInsert( @Nonnull ImportBatch resource,
                                 @Nullable Identifier parentKey,
                                 @Nullable MediaProvider provider )
            throws IOException
    {
        checkNotNull( resource );
        return client().importBatch().insert( resource );
    }

    @Override
    public ImportBatch executeInsert( @Nonnull Object request,
                                      @Nullable Map<String, Object> parameters,
                                      @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        fill( request, parameters );
        return ( ( CustomizedCtoolkitAgent.ImportBatch.Insert ) request ).execute( credential );
    }

    @Override
    public Object prepareUpdate( @Nonnull ImportBatch resource,
                                 @Nonnull Identifier identifier,
                                 @Nullable MediaProvider provider )
            throws IOException
    {
        checkNotNull( resource );
        checkNotNull( identifier, "Identifier cannot be null" );

        return client().importBatch().update( identifier.getLong(), resource );
    }

    @Override
    public ImportBatch executeUpdate( @Nonnull Object request,
                                      @Nullable Map<String, Object> parameters,
                                      @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        fill( request, parameters );
        return ( ( CustomizedCtoolkitAgent.ImportBatch.Update ) request ).execute( credential );
    }

    @Override
    public Object prepareDelete( @Nonnull Identifier identifier ) throws IOException
    {
        checkNotNull( identifier, "Identifier cannot be null" );
        return client().importBatch().delete( identifier.getLong() );
    }

    @Override
    public Object executeDelete( @Nonnull Object request,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        return ( ( CustomizedCtoolkitAgent.ImportBatch.Delete ) request ).execute( credential );
    }

    @Override
    public Object prepareList( @Nullable Identifier identifier ) throws IOException
    {
        return client().importBatch().list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<ImportBatch> executeList( @Nonnull Object request,
                                          @Nullable Map<String, Object> parameters,
                                          @Nullable Locale locale,
                                          @Nullable Integer start,
                                          @Nullable Integer length,
                                          @Nullable String orderBy,
                                          @Nullable Boolean ascending )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );


        fill( request, parameters );
        return ( ( CustomizedCtoolkitAgent.ImportBatch.List ) request ).execute( credential ).getItems();
    }
}
