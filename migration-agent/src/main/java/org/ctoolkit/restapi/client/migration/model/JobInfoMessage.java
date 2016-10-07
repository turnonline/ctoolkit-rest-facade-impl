package org.ctoolkit.restapi.client.migration.model;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class JobInfoMessage
{
    private String key;

    private Status status;

    public enum Status
    {
        COMPLETED_SUCCESSFULLY,
        STOPPED_BY_ERROR
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus( Status status )
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "JobInfoMessage{" +
                "key='" + key + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
