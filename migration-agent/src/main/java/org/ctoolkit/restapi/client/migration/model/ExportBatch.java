package org.ctoolkit.restapi.client.migration.model;

/**
 * Export DTO for rest communication
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class ExportBatch
        extends Batch<ExportBatch.ExportItem, ExportJobInfo>
{
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
}
