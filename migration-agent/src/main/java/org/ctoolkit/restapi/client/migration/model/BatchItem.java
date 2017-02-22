package org.ctoolkit.restapi.client.migration.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class BatchItem
        implements Serializable
{
    private String key;

    private String name;

    private String data;

    private String fileName;

    private DataType dataType = DataType.JSON;

    private long dataLength;

    private JobState state;

    private Date createDate;

    private Date updateDate;

    private String error;

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

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName( String fileName )
    {
        this.fileName = fileName;
    }

    public DataType getDataType()
    {
        return dataType;
    }

    public void setDataType( DataType dataType )
    {
        this.dataType = dataType;
    }

    public long getDataLength()
    {
        return dataLength;
    }

    public void setDataLength( long dataLength )
    {
        this.dataLength = dataLength;
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

    public String getError()
    {
        return error;
    }

    public void setError( String error )
    {
        this.error = error;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( !( o instanceof BatchItem ) ) return false;

        BatchItem batchItem = ( BatchItem ) o;

        return key != null ? key.equals( batchItem.key ) : batchItem.key == null;
    }

    @Override
    public int hashCode()
    {
        return key != null ? key.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "BatchItem{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                ", dataType=" + dataType +
                ", dataLength=" + dataLength +
                ", state=" + state +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                '}';
    }
}