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

/**
 * Export DTO for rest communication
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class ExportBatch
        extends Batch<ExportBatch.ExportItem, ExportJobInfo>
{
    public ExportItem getItem( String entityToExport )
    {
        for ( ExportItem item : getItems() )
        {
            if ( item.getEntityToExport().equals( entityToExport ) )
            {
                return item;
            }
        }

        return null;
    }

    @Override
    public String toString()
    {
        return "ExportBatch{} " + super.toString();
    }

    public static class ExportItem
            extends BatchItem
    {
        private String entityToExport;

        public String getEntityToExport()
        {
            return entityToExport;
        }

        public void setEntityToExport( String entityToExport )
        {
            this.entityToExport = entityToExport;
        }

        @Override
        public String toString()
        {
            return "ExportItem{" +
                    "entityToExport='" + entityToExport + '\'' +
                    "} " + super.toString();
        }
    }
}
