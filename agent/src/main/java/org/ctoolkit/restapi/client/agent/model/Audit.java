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
public class Audit
        implements Serializable
{
    private Date createDate;

    private String createdBy;

    private Action action;

    private Operation operation;

    private String ownerId;

    private String userPhotoUrl;

    private String userDisplayName;

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate( Date createDate )
    {
        this.createDate = createDate;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }

    public Action getAction()
    {
        return action;
    }

    public void setAction( Action action )
    {
        this.action = action;
    }

    public Operation getOperation()
    {
        return operation;
    }

    public void setOperation( Operation operation )
    {
        this.operation = operation;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId( String ownerId )
    {
        this.ownerId = ownerId;
    }

    public String getUserPhotoUrl()
    {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl( String userPhotoUrl )
    {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getUserDisplayName()
    {
        return userDisplayName;
    }

    public void setUserDisplayName( String userDisplayName )
    {
        this.userDisplayName = userDisplayName;
    }

    @Override
    public String toString()
    {
        return "Audit{" +
                "createDate=" + createDate +
                ", createdBy='" + createdBy + '\'' +
                ", action=" + action +
                ", operation=" + operation +
                ", ownerId='" + ownerId + '\'' +
                ", userPhotoUrl='" + userPhotoUrl + '\'' +
                ", userDisplayName='" + userDisplayName + '\'' +
                '}';
    }

    public enum Action
    {
        CREATE,
        UPDATE,
        DELETE,

        START_JOB,
        CANCEL_JOB
    }

    public enum Operation
    {
        IMPORT,
        IMPORT_ITEM,

        EXPORT,
        EXPORT_ITEM,

        MIGRATION,
        MIGRATION_ITEM
    }
}
