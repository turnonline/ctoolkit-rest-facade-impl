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
import java.util.Date;

/**
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
public class BatchItem
        implements Serializable
{
    private Long id;

    private String name;

    private String data;

    private String fileName;

    private DataType dataType = DataType.JSON;

    private long dataLength;

    private JobState state;

    private Date createDate;

    private Date updateDate;

    private String error;

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

        return id != null ? id.equals( batchItem.id ) : batchItem.id == null;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "BatchItem{" +
                "id='" + id + '\'' +
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