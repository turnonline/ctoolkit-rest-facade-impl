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

import org.ctoolkit.api.agent.model.ChangeItem;
import org.ctoolkit.api.agent.model.ImportItem;
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
 * The ImportItem as {@link ImportItem} concrete adaptee implementation.
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class GenericJsonChangeItemAdaptee
        extends AbstractGoogleClientAdaptee<CustomizedCtoolkitAgent, ChangeItem>
        implements
        GetExecutorAdaptee<ChangeItem>,
        InsertExecutorAdaptee<ChangeItem>,
        UpdateExecutorAdaptee<ChangeItem>,
        DeleteExecutorAdaptee<ChangeItem>
{
    @Inject
    public GenericJsonChangeItemAdaptee( CustomizedCtoolkitAgent ctoolkitAgent )
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
        return client().changeBatch().item().get( metadataId, id );
    }

    @Override
    public ChangeItem executeGet( @Nonnull Object request,
                                  @Nullable Map<String, Object> parameters,
                                  @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        fill( get( request ), parameters, locale );
        return ( ( CustomizedCtoolkitAgent.ChangeBatch.Item.Get ) request ).execute( credential );
    }

    @Override
    public Object prepareInsert( @Nonnull ChangeItem resource,
                                 @Nullable Identifier parentKey,
                                 @Nullable MediaProvider<?> provider )
            throws IOException
    {
        checkNotNull( resource );
        checkNotNull( parentKey, "Parent identifier cannot be null" );

        return client().changeBatch().item().insert( parentKey.getString(), resource );
    }

    @Override
    public ChangeItem executeInsert( @Nonnull Object request,
                                     @Nullable Map<String, Object> parameters,
                                     @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        fill( get( request ), parameters, locale );
        return ( ( CustomizedCtoolkitAgent.ChangeBatch.Item.Insert ) request ).execute( credential );
    }

    @Override
    public Object prepareUpdate( @Nonnull ChangeItem resource,
                                 @Nonnull Identifier identifier,
                                 @Nullable MediaProvider<?> provider )
            throws IOException
    {
        checkNotNull( resource );
        checkNotNull( identifier, "Parent identifier cannot be null" );
        checkNotNull( identifier.getChild(), "Item identifier cannot be null" );

        String metadataId = identifier.getString();
        String id = identifier.getChild().getString();
        return client().changeBatch().item().update( metadataId, id, resource );
    }

    @Override
    public ChangeItem executeUpdate( @Nonnull Object request,
                                     @Nullable Map<String, Object> parameters,
                                     @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        fill( get( request ), parameters, locale );
        return ( ( CustomizedCtoolkitAgent.ChangeBatch.Item.Update ) request ).execute( credential );
    }

    @Override
    public Object prepareDelete( @Nonnull Identifier identifier ) throws IOException
    {
        checkNotNull( identifier, "Parent identifier cannot be null" );
        checkNotNull( identifier.getChild(), "Item identifier cannot be null" );

        String metadataId = identifier.getString();
        String id = identifier.getChild().getString();
        return client().changeBatch().item().delete( metadataId, id );
    }

    @Override
    public void executeDelete( @Nonnull Object request, @Nullable Map<String, Object> parameters, @Nullable Locale locale )
            throws IOException
    {
        checkNotNull( request );

        RequestCredential credential = new RequestCredential();
        credential.fillInFrom( parameters, true );

        ( ( CustomizedCtoolkitAgent.ChangeBatch.Item.Delete ) request ).execute( credential );
    }
}
