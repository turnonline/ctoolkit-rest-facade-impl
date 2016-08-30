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
import org.ctoolkit.api.migration.model.ChangeJobInfo;
import org.ctoolkit.api.migration.model.ImportBatch;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.MediaProvider;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The ImportBatch as {@link ImportBatch} concrete adaptee implementation.
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class GenericJsonChangeJobInfoAdaptee
        extends AbstractGoogleClientAdaptee<Provider<CtoolkitAgent>, ChangeJobInfo>
        implements
        GetExecutorAdaptee<ChangeJobInfo>,
        InsertExecutorAdaptee<ChangeJobInfo>,
        UpdateExecutorAdaptee<ChangeJobInfo>,
        DeleteExecutorAdaptee<ChangeJobInfo>
{
    @Inject
    public GenericJsonChangeJobInfoAdaptee( Provider<CtoolkitAgent> ctoolkitAgent )
    {
        super( ctoolkitAgent );
    }

    @Override
    public Object prepareGet( @Nonnull Identifier identifier )
            throws IOException
    {
        checkNotNull( identifier );

        return client().get().changeBatch().job().progress( identifier.getString() );
    }

    @Override
    public ChangeJobInfo executeGet( @Nonnull Object request,
                                   @Nullable Map<String, Object> parameters,
                                   @Nullable Locale locale )
            throws IOException
    {
        fill( get( request ), parameters, locale );
        return execute( request );
    }

    @Override
    public Object prepareInsert( @Nonnull ChangeJobInfo resource,
                                 @Nullable Identifier parentKey,
                                 @Nullable MediaProvider<?> provider )
            throws IOException
    {
        checkNotNull( resource );
        return client().get().changeBatch().job().start( resource.getId() );
    }

    @Override
    public ChangeJobInfo executeInsert( @Nonnull Object request,
                                      @Nullable Map<String, Object> parameters,
                                      @Nullable Locale locale )
            throws IOException
    {
        fill( get( request ), parameters, locale );
        return execute( request );
    }

    @Override
    public Object prepareUpdate( @Nonnull ChangeJobInfo resource,
                                 @Nonnull Identifier identifier,
                                 @Nullable MediaProvider<?> provider )
            throws IOException
    {
        checkNotNull( resource );
        checkNotNull( identifier );

        return client().get().changeBatch().job().cancel( identifier.getString() );
    }

    @Override
    public ChangeJobInfo executeUpdate( @Nonnull Object request,
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
        return client().get().changeBatch().job().delete( identifier.getString() );
    }

    @Override
    public void executeDelete( @Nonnull Object o, @Nullable Locale locale ) throws IOException
    {
        execute( o );
    }
}
