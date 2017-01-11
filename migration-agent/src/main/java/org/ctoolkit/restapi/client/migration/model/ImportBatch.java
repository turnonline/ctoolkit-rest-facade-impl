package org.ctoolkit.restapi.client.migration.model;

/**
 * Import DTO for rest communication
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class ImportBatch
        extends Batch<ImportBatch.ImportItem, ImportJobInfo>
{
    public static class ImportItem
            extends BatchItem
    {
        @Override
        public String toString()
        {
            return "ImportItem{} " + super.toString();
        }
    }

    @Override
    public String toString()
    {
        return "ImportBatch{} " + super.toString();
    }
}
