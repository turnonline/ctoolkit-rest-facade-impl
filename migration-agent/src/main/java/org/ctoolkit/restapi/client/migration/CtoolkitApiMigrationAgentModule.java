package org.ctoolkit.restapi.client.migration;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.ctoolkit.api.migration.CtoolkitAgent;
import org.ctoolkit.api.migration.CtoolkitAgentScopes;
import org.ctoolkit.api.migration.model.ChangeBatch;
import org.ctoolkit.api.migration.model.ChangeItem;
import org.ctoolkit.api.migration.model.ChangeJobInfo;
import org.ctoolkit.api.migration.model.ExportBatch;
import org.ctoolkit.api.migration.model.ExportJobInfo;
import org.ctoolkit.api.migration.model.ImportBatch;
import org.ctoolkit.api.migration.model.ImportItem;
import org.ctoolkit.api.migration.model.ImportJobInfo;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;
import org.ctoolkit.restapi.client.googleapis.GoogleApiProxyFactory;
import org.ctoolkit.restapi.client.migration.adaptee.GenericJsonChangeBatchAdaptee;
import org.ctoolkit.restapi.client.migration.adaptee.GenericJsonChangeItemAdaptee;
import org.ctoolkit.restapi.client.migration.adaptee.GenericJsonChangeJobInfoAdaptee;
import org.ctoolkit.restapi.client.migration.adaptee.GenericJsonExportBatchAdaptee;
import org.ctoolkit.restapi.client.migration.adaptee.GenericJsonExportJobInfoAdaptee;
import org.ctoolkit.restapi.client.migration.adaptee.GenericJsonImportBatchAdaptee;
import org.ctoolkit.restapi.client.migration.adaptee.GenericJsonImportInfoJobAdaptee;
import org.ctoolkit.restapi.client.migration.adaptee.GenericJsonImportItemAdaptee;
import org.ctoolkit.restapi.client.migration.model.ResourcesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

/**
 * The CToolkit migration api guice module as a default configuration.
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class CtoolkitApiMigrationAgentModule
        extends AbstractModule
{
    public static final String API_PREFIX = "migrationAgent";

    private static final Logger logger = LoggerFactory.getLogger( CtoolkitApiMigrationAgentModule.class );

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
        bind( new TypeLiteral<GetExecutorAdaptee<ImportJobInfo>>()
        {
        } ).to( GenericJsonImportInfoJobAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ImportJobInfo>>()
        {
        } ).to( GenericJsonImportInfoJobAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<ImportJobInfo>>()
        {
        } ).to( GenericJsonImportInfoJobAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ImportJobInfo>>()
        {
        } ).to( GenericJsonImportInfoJobAdaptee.class ).in( Singleton.class );

        // ChangeBatch
        bind( new TypeLiteral<GetExecutorAdaptee<ChangeBatch>>()
        {
        } ).to( GenericJsonChangeBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<ListExecutorAdaptee<ChangeBatch>>()
        {
        } ).to( GenericJsonChangeBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ChangeBatch>>()
        {
        } ).to( GenericJsonChangeBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<ChangeBatch>>()
        {
        } ).to( GenericJsonChangeBatchAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ChangeBatch>>()
        {
        } ).to( GenericJsonChangeBatchAdaptee.class ).in( Singleton.class );

        // ChangeItem
        bind( new TypeLiteral<GetExecutorAdaptee<ChangeItem>>()
        {
        } ).to( GenericJsonChangeItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ChangeItem>>()
        {
        } ).to( GenericJsonChangeItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<ChangeItem>>()
        {
        } ).to( GenericJsonChangeItemAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ChangeItem>>()
        {
        } ).to( GenericJsonChangeItemAdaptee.class ).in( Singleton.class );

        // ChangeJobInfo
        bind( new TypeLiteral<GetExecutorAdaptee<ChangeJobInfo>>()
        {
        } ).to( GenericJsonChangeJobInfoAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ChangeJobInfo>>()
        {
        } ).to( GenericJsonChangeJobInfoAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<ChangeJobInfo>>()
        {
        } ).to( GenericJsonChangeJobInfoAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ChangeJobInfo>>()
        {
        } ).to( GenericJsonChangeJobInfoAdaptee.class ).in( Singleton.class );

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

        // ExportJobInfo
        bind( new TypeLiteral<GetExecutorAdaptee<ExportJobInfo>>()
        {
        } ).to( GenericJsonExportJobInfoAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<InsertExecutorAdaptee<ExportJobInfo>>()
        {
        } ).to( GenericJsonExportJobInfoAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<UpdateExecutorAdaptee<ExportJobInfo>>()
        {
        } ).to( GenericJsonExportJobInfoAdaptee.class ).in( Singleton.class );

        bind( new TypeLiteral<DeleteExecutorAdaptee<ExportJobInfo>>()
        {
        } ).to( GenericJsonExportJobInfoAdaptee.class ).in( Singleton.class );
    }

    @Provides
    @Singleton
    CtoolkitAgent provideCtoolkitmigration(GoogleApiProxyFactory factory )
    {
        HttpTransport httpTransport;
        HttpRequestInitializer credential;
        Set<String> scopes = CtoolkitAgentScopes.all();

        String applicationName = factory.getApplicationName(API_PREFIX);

        try
        {
            httpTransport = factory.getHttpTransport();
            credential = factory.authorize( scopes, null, API_PREFIX );
        }
        catch ( GeneralSecurityException | IOException e )
        {
            logger.error( "Scopes: " + scopes.toString()
                    + " Application name: " + applicationName
                    + " Service account: " + factory.getServiceAccountEmail(API_PREFIX), e );

            throw new IllegalArgumentException( "Error occurred during providing ctoolkit migration REST API" );
        }

        return new CtoolkitAgent.Builder( httpTransport, factory.getJsonFactory(), credential )
                .setApplicationName( applicationName )
                .setRootUrl( factory.getEndpointUrl(API_PREFIX) )
                .build();
    }
}
