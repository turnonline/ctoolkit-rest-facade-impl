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