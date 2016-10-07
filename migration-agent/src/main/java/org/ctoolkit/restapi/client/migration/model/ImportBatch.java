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
public class ImportBatch
        implements Serializable
{
    private String key;

    private String name;

    private String mapReduceJobId;

    private String token;

    private Date createDate;

    private Date updateDate;

    private ImportJobInfo jobInfo;

    private List<ImportItem> items = new ArrayList<>();

    public static class ImportItem
            implements Serializable
    {
        private String key;

        private String name;

        private String data;

        private DataType dataType = DataType.JSON;

        private JobState state;

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
                    ", dataType=" + dataType +
                    ", state=" + state +
                    ", createDate=" + createDate +
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

    public ImportJobInfo getJobInfo()
    {
        return jobInfo;
    }

    public void setJobInfo( ImportJobInfo jobInfo )
    {
        this.jobInfo = jobInfo;
    }

    public List<ImportItem> getItems()
    {
        return items;
    }

    public void setItems( List<ImportItem> items )
    {
        this.items = items;
    }

    @Override
    public String toString()
    {
        return "Import{" +
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
