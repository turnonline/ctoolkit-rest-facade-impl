package org.ctoolkit.restapi.client.migration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class Batch<I extends BatchItem, J extends JobInfo>
        implements Serializable
{
    private String key;

    private String name;

    private String mapReduceJobId;

    private String token;

    private Date createDate;

    private Date updateDate;

    private J jobInfo;

    private List<I> items = new ArrayList<I>();

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
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
        return "Change{" +
                "key='" + key + '\'' +
                ", name=" + name +
                ", mapReduceJobId='" + mapReduceJobId + '\'' +
                ", token='" + token + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", jobInfo=" + jobInfo +
                ", items=" + items +
                '}';
    }
}
