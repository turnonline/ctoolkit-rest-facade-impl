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

package org.ctoolkit.restapi.client.agent;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import org.ctoolkit.api.agent.model.ExportBatch;
import org.ctoolkit.api.agent.model.ExportItem;
import org.ctoolkit.api.agent.model.ExportJob;
import org.ctoolkit.api.agent.model.ImportBatch;
import org.ctoolkit.api.agent.model.ImportItem;
import org.ctoolkit.api.agent.model.ImportJob;
import org.ctoolkit.api.agent.model.KindMetaData;
import org.ctoolkit.api.agent.model.MetadataAudit;
import org.ctoolkit.api.agent.model.MigrationBatch;
import org.ctoolkit.api.agent.model.MigrationItem;
import org.ctoolkit.api.agent.model.MigrationJob;
import org.ctoolkit.api.agent.model.PropertyMetaData;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.adapter.BeanMapperConfig;
import org.ctoolkit.restapi.client.adapter.ClientApi;
import org.ctoolkit.restapi.client.agent.adaptee.CustomizedCtoolkitAgent;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonExportBatchAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonExportItemAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonExportJobAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonImportBatchAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonImportItemAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonImportJobAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonKindMetaDataAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonMetadataAuditAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonMigrationBatchAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonMigrationItemAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonMigrationJobAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonPropertyMetaDataAdaptee;
import org.ctoolkit.restapi.client.agent.model.ResourcesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * The CtoolkiT Migration API Client guice module as a default configuration.
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class CtoolkitApiAgentModule
        extends AbstractModule
{
    public static final String API_PREFIX = "ctoolkit-agent";

    private static final Logger logger = LoggerFactory.getLogger( CtoolkitApiAgentModule.class );

    @Override
    protected void configure()
    {
        // bind resource mapper which maps generated rest client model objects into rest model objects
        Multibinder<BeanMapperConfig> multibinder = Multibinder.newSetBinder( binder(), BeanMapperConfig.class );
        multibinder.addBinding().to( ResourcesMapper.class ).in( Singleton.class );

        // ClientApi config
        bind( CustomizedCtoolkitAgent.class ).toProvider( AgentProvider.class );

        MapBinder<String, ClientApi> mapBinder;
        mapBinder = MapBinder.newMapBinder( binder(), String.class, ClientApi.class );
        mapBinder.addBinding( API_PREFIX ).to( AgentProvider.class );
        // ClientApi config end

        // ImportBatch
        bind( new TypeLiteral<GetExecutorAdaptee<ImportBatch>>()
        {
        } ).to( GenericJsonImportBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<ListExecutorAdaptee<ImportBatch>>()
        {
        } ).to( GenericJsonImportBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ImportBatch>>()
        {
        } ).to( GenericJsonImportBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<ImportBatch>>()
        {
        } ).to( GenericJsonImportBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ImportBatch>>()
        {
        } ).to( GenericJsonImportBatchAdaptee.class ).in( Singleton.class );

        // ImportItem
        bind( new TypeLiteral<GetExecutorAdaptee<ImportItem>>()
        {
        } ).to( GenericJsonImportItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ImportItem>>()
        {
        } ).to( GenericJsonImportItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<ImportItem>>()
        {
        } ).to( GenericJsonImportItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ImportItem>>()
        {
        } ).to( GenericJsonImportItemAdaptee.class ).in( Singleton.class );

        // ImportJobInfo
        bind( new TypeLiteral<GetExecutorAdaptee<ImportJob>>()
        {
        } ).to( GenericJsonImportJobAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ImportJob>>()
        {
        } ).to( GenericJsonImportJobAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ImportJob>>()
        {
        } ).to( GenericJsonImportJobAdaptee.class ).in( Singleton.class );

        // ExportBatch
        bind( new TypeLiteral<GetExecutorAdaptee<ExportBatch>>()
        {
        } ).to( GenericJsonExportBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<ListExecutorAdaptee<ExportBatch>>()
        {
        } ).to( GenericJsonExportBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ExportBatch>>()
        {
        } ).to( GenericJsonExportBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<ExportBatch>>()
        {
        } ).to( GenericJsonExportBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ExportBatch>>()
        {
        } ).to( GenericJsonExportBatchAdaptee.class ).in( Singleton.class );

        // ExportItem
        bind( new TypeLiteral<GetExecutorAdaptee<ExportItem>>()
        {
        } ).to( GenericJsonExportItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ExportItem>>()
        {
        } ).to( GenericJsonExportItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<ExportItem>>()
        {
        } ).to( GenericJsonExportItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ExportItem>>()
        {
        } ).to( GenericJsonExportItemAdaptee.class ).in( Singleton.class );

        // ExportJobInfo
        bind( new TypeLiteral<GetExecutorAdaptee<ExportJob>>()
        {
        } ).to( GenericJsonExportJobAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ExportJob>>()
        {
        } ).to( GenericJsonExportJobAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ExportJob>>()
        {
        } ).to( GenericJsonExportJobAdaptee.class ).in( Singleton.class );

        // MigrationBatch
        bind( new TypeLiteral<GetExecutorAdaptee<MigrationBatch>>()
        {
        } ).to( GenericJsonMigrationBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<ListExecutorAdaptee<MigrationBatch>>()
        {
        } ).to( GenericJsonMigrationBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<MigrationBatch>>()
        {
        } ).to( GenericJsonMigrationBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<MigrationBatch>>()
        {
        } ).to( GenericJsonMigrationBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<MigrationBatch>>()
        {
        } ).to( GenericJsonMigrationBatchAdaptee.class ).in( Singleton.class );

        // MigrationItem
        bind( new TypeLiteral<GetExecutorAdaptee<MigrationItem>>()
        {
        } ).to( GenericJsonMigrationItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<MigrationItem>>()
        {
        } ).to( GenericJsonMigrationItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<MigrationItem>>()
        {
        } ).to( GenericJsonMigrationItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<MigrationItem>>()
        {
        } ).to( GenericJsonMigrationItemAdaptee.class ).in( Singleton.class );

        // MigrationJobInfo
        bind( new TypeLiteral<GetExecutorAdaptee<MigrationJob>>()
        {
        } ).to( GenericJsonMigrationJobAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<MigrationJob>>()
        {
        } ).to( GenericJsonMigrationJobAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<MigrationJob>>()
        {
        } ).to( GenericJsonMigrationJobAdaptee.class ).in( Singleton.class );

        // KindMetaData
        bind( new TypeLiteral<ListExecutorAdaptee<KindMetaData>>()
        {
        } ).to( GenericJsonKindMetaDataAdaptee.class ).in( Singleton.class );

        // PropertyMetaData
        bind( new TypeLiteral<ListExecutorAdaptee<PropertyMetaData>>()
        {
        } ).to( GenericJsonPropertyMetaDataAdaptee.class ).in( Singleton.class );

        // MetadataAudit
        bind( new TypeLiteral<ListExecutorAdaptee<MetadataAudit>>()
        {
        } ).to( GenericJsonMetadataAuditAdaptee.class ).in( Singleton.class );
    }
}
