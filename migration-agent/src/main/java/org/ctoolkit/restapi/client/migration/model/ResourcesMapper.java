package org.ctoolkit.restapi.client.migration.model;

import ma.glasnost.orika.MapperFactory;

import javax.inject.Inject;

/**
 * Resource mapper maps model object from rest api to rest client. Frontend requires from model object to be serializable,
 * but rest api objects are not.
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class ResourcesMapper
{
    @Inject
    public ResourcesMapper( MapperFactory factory )
    {
        factory.classMap( ImportBatch.class, org.ctoolkit.api.migration.model.ImportBatch.class ).byDefault().register();
        factory.classMap( ExportBatch.class, org.ctoolkit.api.migration.model.ExportBatch.class ).byDefault().register();
        factory.classMap( ChangeBatch.class, org.ctoolkit.api.migration.model.ChangeBatch.class ).byDefault().register();

        factory.classMap( ImportJobInfo.class, org.ctoolkit.api.migration.model.ImportJobInfo.class ).byDefault().register();
        factory.classMap( ExportJobInfo.class, org.ctoolkit.api.migration.model.ExportJobInfo.class ).byDefault().register();
        factory.classMap( ChangeJobInfo.class, org.ctoolkit.api.migration.model.ChangeJobInfo.class ).byDefault().register();

        factory.classMap( MetadataAudit.class, org.ctoolkit.api.migration.model.MetadataAudit.class ).byDefault().register();
        factory.classMap( KindMetaData.class, org.ctoolkit.api.migration.model.KindMetaData.class ).byDefault().register();
        factory.classMap( PropertyMetaData.class, org.ctoolkit.api.migration.model.PropertyMetaData.class ).byDefault().register();
    }
}
