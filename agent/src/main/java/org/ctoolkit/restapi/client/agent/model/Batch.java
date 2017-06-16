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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class Batch<I extends BatchItem, J extends JobInfo>
        implements Serializable
{
    private Long id;

    private String name;

    private String jobId;

    private Date createDate;

    private Date updateDate;

    private J jobInfo;

    private List<I> items = new ArrayList<I>();

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJobId( String jobId )
    {
        this.jobId = jobId;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate( Date createDate )
    {
        this.createDate = createDate;
    }

    public Date getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate( Date updateDate )
    {
        this.updateDate = updateDate;
    }

    public J getJobInfo()
    {
        return jobInfo;
    }

    public void setJobInfo( J jobInfo )
    {
        this.jobInfo = jobInfo;
    }

    public List<I> getItems()
    {
        return items;
    }

    public void setItems( List<I> items )
    {
        this.items = items;
    }

    @Override
    public String toString()
    {
        return "Batch{" +
                "id='" + id + '\'' +
                ", name=" + name +
                ", jobId='" + jobId + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", jobInfo=" + jobInfo +
                ", items=" + items +
                '}';
    }
}
