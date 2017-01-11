package org.ctoolkit.restapi.client.migration.model;

/**
 * Change DTO for rest communication
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class ChangeBatch
        extends Batch<ChangeBatch.ChangeItem, ChangeJobInfo>
{
    public static class ChangeItem
            extends BatchItem
    {
        @Override
        public String toString()
        {
            return "ChangeItem{} " + super.toString();
        }
    }

    @Override
    public String toString()
    {
        return "ChangeBatch{} " + super.toString();
    }
}
