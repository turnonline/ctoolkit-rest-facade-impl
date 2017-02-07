package org.ctoolkit.restapi.client.migration.model;

import java.util.Date;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class MetadataAudit
{
    private String key;

    private Date createDate;

    private Date updateDate;

    private String createdBy;

    private String updatedBy;

    private Action action;

    private Operation operation;

    private String ownerId;

    public enum Action
    {
        CREATE,
        UPDATE,
        DELETE,

        START_JOB,
        CANCEL_JOB,
        DELETE_JOB,

        MIGRATION
    }

    public enum Operation
    {
        IMPORT,
        IMPORT_ITEM,
        EXPORT,
        EXPORT_ITEM,
        CHANGE,
        CHANGE_ITEM
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
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

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy( String createdBy )
    {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy()
    {
        return updatedBy;
    }

    public void setUpdatedBy( String updatedBy )
    {
        this.updatedBy = updatedBy;
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

    @Override
    public String toString()
    {
        return "MetadataAudit{" +
                "key='" + key + '\'' +
                ", createDate=" + createDate +
                ", updateDate=" + updateDate +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                ", action=" + action +
                ", operation=" + operation +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }
}
