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

import java.io.Serializable;

/**
 * Job info
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public abstract class JobInfo
        implements Serializable
{
    private String id;

    private String jobId;

    private int totalItems;

    private int processedItems;

    private int processedErrorItems;

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

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId( String jobId )
    {
        this.jobId = jobId;
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

    public int getProcessedErrorItems()
    {
        return processedErrorItems;
    }

    public void setProcessedErrorItems( int processedErrorItems )
    {
        this.processedErrorItems = processedErrorItems;
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
                ", jobId='" + jobId + '\'' +
                ", totalItems=" + totalItems +
                ", processedItems=" + processedItems +
                ", processedErrorItems=" + processedErrorItems +
                ", state=" + state +
                ", stackTrace='" + stackTrace + '\'' +
                '}';
    }
}
