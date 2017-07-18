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

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.http.HttpTransport;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.ctoolkit.api.agent.Agent;
import org.ctoolkit.api.agent.AgentScopes;
import org.ctoolkit.api.agent.model.ExportBatch;
import org.ctoolkit.api.agent.model.ExportItem;
import org.ctoolkit.api.agent.model.ExportJob;
import org.ctoolkit.api.agent.model.ImportBatch;
import org.ctoolkit.api.agent.model.ImportItem;
import org.ctoolkit.api.agent.model.ImportJob;
import org.ctoolkit.api.agent.model.KindMetaData;
import org.ctoolkit.api.agent.model.MetadataAudit;
import org.ctoolkit.api.agent.model.PropertyMetaData;
import org.ctoolkit.restapi.client.AccessToken;
import org.ctoolkit.restapi.client.RemoteServerErrorException;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.CustomizedCtoolkitAgent;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonExportBatchAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonExportItemAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonExportJobAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonImportBatchAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonImportItemAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonImportJobAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonKindMetaDataAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonMetadataAuditAdaptee;
import org.ctoolkit.restapi.client.agent.adaptee.GenericJsonPropertyMetaDataAdaptee;
import org.ctoolkit.restapi.client.agent.model.ResourcesMapper;
import org.ctoolkit.restapi.client.googleapis.ApiToken;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

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

    private ApiToken initialized;

    @Override
    protected void configure()
    {
        // bind resource mapper which maps generated rest client model objects into rest model objects
        bind( ResourcesMapper.class ).asEagerSingleton();

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

        bind( new TypeLiteral<UpdateExecutorAdaptee<ImportJob>>()
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

        bind( new TypeLiteral<UpdateExecutorAdaptee<ExportJob>>()
        {
        } ).to( GenericJsonExportJobAdaptee.class ).in( Singleton.class );

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

    @Provides
    @Singleton
    CustomizedCtoolkitAgent provideCtoolkitAgent( GoogleApiProxyFactory factory )
    {
        Set<String> scopes = AgentScopes.all();
        CustomizedCtoolkitAgent.Builder builder;

        String applicationName = factory.getApplicationName( API_PREFIX );
        String endpointUrl = factory.getEndpointUrl( API_PREFIX );

        try
        {
            HttpTransport httpTransport = factory.getHttpTransport();
            initialized = factory.authorize( scopes, null, API_PREFIX );
            HttpRequestInitializer credential = initialized.getCredential();

            builder = new CustomizedCtoolkitAgent.Builder( httpTransport, factory.getJsonFactory(), credential );
            builder.setApplicationName( applicationName )
                    .setRootUrl( endpointUrl )
                    .setServicePath( Agent.DEFAULT_SERVICE_PATH );
        }
        catch ( GeneralSecurityException e )
        {
            logger.error( "Scopes: " + scopes.toString()
                    + " Application name: " + applicationName
                    + " Endpoint URL: " + endpointUrl, e );

            throw new UnauthorizedException( e.getMessage() );
        }
        catch ( IOException e )
        {
            logger.error( "Failed. Scopes: " + scopes.toString()
                    + " Application name: " + applicationName
                    + " Endpoint URL: " + endpointUrl, e );

            throw new RemoteServerErrorException( HttpStatusCodes.STATUS_CODE_SERVER_ERROR, e.getMessage() );
        }

        return builder.build();
    }

    @Provides
    @AccessToken( apiName = API_PREFIX )
    ApiToken.Data provideCtoolkitAgentTokenData( CustomizedCtoolkitAgent client )
    {
        initialized.setServiceUrl( client.getBaseUrl() );
        return initialized.getTokenData();
    }
}
