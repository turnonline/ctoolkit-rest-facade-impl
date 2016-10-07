package org.ctoolkit.restapi.client.migration.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Import DTO for rest communication
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class ExportBatch
        implements Serializable
{
    private String key;

    private String name;

    private String mapReduceJobId;

    private String token;

    private Date createDate;

    private Date updateDate;

    private ChangeJobInfo jobInfo;

    private List<ExportItem> items = new ArrayList<>();

    public static class ExportItem
            implements Serializable
    {
        private String key;

        private String name;

        private String data;

        private DataType dataType = DataType.JSON;

        private JobState state;

        private String entityToExport;

        private Date createDate;

        private Date updateDate;

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

        public String getData()
        {
            return data;
        }

        public void setData( String data )
        {
            this.data = data;
        }

        public DataType getDataType()
        {
            return dataType;
        }

        public void setDataType( DataType dataType )
        {
            this.dataType = dataType;
        }

        public JobState getState()
        {
            return state;
        }

        public void setState( JobState state )
        {
            this.state = state;
        }

        public String getEntityToExport()
        {
            return entityToExport;
        }

        public void setEntityToExport( String entityToExport )
        {
            this.entityToExport = entityToExport;
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

        @Override
        public String toString()
        {
            return "Item{" +
                    "key='" + key + '\'' +
                    ", name=" + name +
                    ", entityToExport='" + entityToExport + '\'' +
                    ", createDate=" + createDate +
                    ", dataType=" + dataType +
                    ", state=" + state +
                    ", updateDate=" + updateDate +
                    '}';
        }
    }

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

    public ChangeJobInfo getJobInfo()
    {
        return jobInfo;
    }

    public void setJobInfo( ChangeJobInfo jobInfo )
    {
        this.jobInfo = jobInfo;
    }

    public List<ExportItem> getItems()
    {
        return items;
    }

    public void setItems( List<ExportItem> items )
    {
        this.items = items;
    }

    @Override
    public String toString()
    {
        return "Export{" +
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
