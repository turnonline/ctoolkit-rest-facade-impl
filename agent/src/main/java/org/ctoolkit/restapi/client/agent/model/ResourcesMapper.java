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

package org.ctoolkit.restapi.client.agent.model;

import ma.glasnost.orika.MapperFactory;
import org.ctoolkit.restapi.client.adapter.BeanMapperConfig;

import javax.inject.Singleton;

/**
 * Resource mapper maps model object from rest api to rest client. Frontend requires from model object to be serializable,
 * but rest api objects are not.
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
@Singleton
public class ResourcesMapper
        implements BeanMapperConfig
{
    @Override
    public void config( MapperFactory factory )
    {
        factory.classMap( ImportBatch.class, org.ctoolkit.api.agent.model.ImportBatch.class ).byDefault().register();
        factory.classMap( ExportBatch.class, org.ctoolkit.api.agent.model.ExportBatch.class ).byDefault().register();
        factory.classMap( MigrationBatch.class, org.ctoolkit.api.agent.model.MigrationBatch.class ).byDefault().register();

        factory.classMap( ImportBatch.ImportItem.class, org.ctoolkit.api.agent.model.ImportItem.class ).byDefault().register();
        factory.classMap( ExportBatch.ExportItem.class, org.ctoolkit.api.agent.model.ExportItem.class ).byDefault().register();
        factory.classMap( MigrationBatch.MigrationItem.class, org.ctoolkit.api.agent.model.MigrationItem.class ).byDefault().register();

        factory.classMap( ImportJobInfo.class, org.ctoolkit.api.agent.model.ImportJob.class ).byDefault().register();
        factory.classMap( ExportJobInfo.class, org.ctoolkit.api.agent.model.ExportJob.class ).byDefault().register();
        factory.classMap( MigrationJobInfo.class, org.ctoolkit.api.agent.model.MigrationJob.class ).byDefault().register();

        factory.classMap( Audit.class, org.ctoolkit.api.agent.model.MetadataAudit.class ).byDefault().register();
        factory.classMap( KindMetaData.class, org.ctoolkit.api.agent.model.KindMetaData.class ).byDefault().register();
        factory.classMap( PropertyMetaData.class, org.ctoolkit.api.agent.model.PropertyMetaData.class ).byDefault().register();
    }
}
