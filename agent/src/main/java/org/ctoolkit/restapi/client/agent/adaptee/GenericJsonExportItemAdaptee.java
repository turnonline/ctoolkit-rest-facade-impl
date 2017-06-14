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

import org.ctoolkit.api.agent.model.ExportItem;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The ExportItem as {@link ExportItem} concrete adaptee implementation.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GenericJsonExportItemAdaptee
        extends AbstractGoogleClientAdaptee<CustomizedCtoolkitAgent, ExportItem>
        implements
        GetExecutorAdaptee<ExportItem>,
        InsertExecutorAdaptee<ExportItem>,
        UpdateExecutorAdaptee<ExportItem>,
        DeleteExecutorAdaptee<ExportItem>
{
    @Inject
    public GenericJsonExportItemAdaptee( CustomizedCtoolkitAgent ctoolkitAgent )
    {
        super( ctoolkitAgent );
    }

    @Override
    public Object prepareGet( @Nonnull Identifier identifier )
            throws IOException
    {
        checkNotNull( identifier, "Parent identifier cannot be null" );
        checkNotNull( identifier.getChild(), "Item identifier cannot be null" );

        String metadataId = identifier.getString();
        String id = identifier.getChild().getString();
        return client().exportBatch().item().get( metadataId, id );
    }

    @Override
    public ExportItem executeGet( @Nonnull Object request,
                                  @Nullable Map<String, Object> parameters,
                                  @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        fill( get( request ), parameters, locale );
        return ( ( CustomizedCtoolkitAgent.ExportBatch.Item.Get ) request ).execute( credential );
    }

    @Override
    public Object prepareInsert( @Nonnull ExportItem resource,
                                 @Nullable Identifier parentKey,
                                 @Nullable MediaProvider provider )
            throws IOException
    {
        checkNotNull( resource );
        checkNotNull( parentKey );

        return client().exportBatch().item().insert( parentKey.getString(), resource );
    }

    @Override
    public ExportItem executeInsert( @Nonnull Object request,
                                     @Nullable Map<String, Object> parameters,
                                     @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        fill( get( request ), parameters, locale );
        return ( ( CustomizedCtoolkitAgent.ExportBatch.Item.Insert ) request ).execute( credential );
    }

    @Override
    public Object prepareUpdate( @Nonnull ExportItem resource,
                                 @Nonnull Identifier identifier,
                                 @Nullable MediaProvider provider )
            throws IOException
    {
        checkNotNull( resource );
        checkNotNull( identifier, "Parent identifier cannot be null" );
        checkNotNull( identifier.getChild(), "Item identifier cannot be null" );

        String metadataId = identifier.getString();
        String id = identifier.getChild().getString();
        return client().exportBatch().item().update( metadataId, id, resource );
    }

    @Override
    public ExportItem executeUpdate( @Nonnull Object request,
                                     @Nullable Map<String, Object> parameters,
                                     @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        fill( get( request ), parameters, locale );
        return ( ( CustomizedCtoolkitAgent.ExportBatch.Item.Update ) request ).execute( credential );
    }

    @Override
    public Object prepareDelete( @Nonnull Identifier identifier ) throws IOException
    {
        checkNotNull( identifier, "Parent identifier cannot be null" );
        checkNotNull( identifier.getChild(), "Item identifier cannot be null" );

        String metadataId = identifier.getString();
        String id = identifier.getChild().getString();
        return client().exportBatch().item().delete( metadataId, id );
    }

    @Override
    public Object executeDelete( @Nonnull Object request,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale ) throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        ( ( CustomizedCtoolkitAgent.ExportBatch.Item.Delete ) request ).execute( credential );
        return null;
    }
}
