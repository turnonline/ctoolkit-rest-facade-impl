package org.ctoolkit.restapi.client.migration.model;

import java.io.Serializable;

/**
 * Job info
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public abstract class JobInfo
        implements Serializable
{
    private String id;

    private String mapReduceJobId;

    private String token;

    private int totalItems;

    private int processedItems;

    private JobState state;

    private String stackTrace;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getMapReduceJobId()
    {
        return mapReduceJobId;
    }

    public void setMapReduceJobId( String mapReduceJobId )
    {
        this.mapReduceJobId = mapReduceJobId;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken( String token )
    {
        this.token = token;
    }

    public int getTotalItems()
    {
        return totalItems;
    }

    public void setTotalItems( int totalItems )
    {
        this.totalItems = totalItems;
    }

    public int getProcessedItems()
    {
        return processedItems;
    }

    public void setProcessedItems( int processedItems )
    {
        this.processedItems = processedItems;
    }

    public JobState getState()
    {
        return state;
    }

    public void setState( JobState state )
    {
        this.state = state;
    }

    public String getStackTrace()
    {
        return stackTrace;
    }

    public void setStackTrace( String stackTrace )
    {
        this.stackTrace = stackTrace;
    }

    @Override
    public String toString()
    {
        return "JobInfo{" +
                "id='" + id + '\'' +
                ", mapReduceJobId='" + mapReduceJobId + '\'' +
                ", token='" + token + '\'' +
                ", totalItems=" + totalItems +
                ", processedItems=" + processedItems +
                ", state=" + state +
                ", stackTrace='" + stackTrace + '\'' +
                '}';
    }
}
