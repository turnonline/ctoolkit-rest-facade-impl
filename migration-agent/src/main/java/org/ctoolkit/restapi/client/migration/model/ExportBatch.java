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
        @Override
        public String toString()
        {
            return "ExportItem{} " + super.toString();
        }
    }

    @Override
    public String toString()
    {
        return "ExportBatch{} " + super.toString();
    }
}
