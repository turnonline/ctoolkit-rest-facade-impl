package org.ctoolkit.restapi.client.agent.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The bean holding migration set kind operations
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class MigrationSetKindOperations
        implements Serializable
{
    @XmlElement( name = "add" )
    private List<MigrationSetKindOperationAdd> add;

    @XmlElement( name = "change" )
    private List<MigrationSetKindOperationChange> change;

    @XmlElement( name = "remove" )
    private List<MigrationSetKindOperationRemove> remove;

    public List<MigrationSetKindOperationAdd> getAdd()
    {
        if ( add == null )
        {
            add = new ArrayList<MigrationSetKindOperationAdd>();
        }
        return add;
    }

    public void setAdd( List<MigrationSetKindOperationAdd> add )
    {
        this.add = add;
    }

    public List<MigrationSetKindOperationChange> getChange()
    {
        if ( change == null )
        {
            change = new ArrayList<MigrationSetKindOperationChange>();
        }
        return change;
    }

    public void setChange( List<MigrationSetKindOperationChange> change )
    {
        this.change = change;
    }

    public List<MigrationSetKindOperationRemove> getRemove()
    {
        if ( remove == null )
        {
            remove = new ArrayList<MigrationSetKindOperationRemove>();
        }
        return remove;
    }

    public void setRemove( List<MigrationSetKindOperationRemove> remove )
    {
        this.remove = remove;
    }

    public List<MigrationSetKindOperation> getAll()
    {
        List<MigrationSetKindOperation> all = new ArrayList<MigrationSetKindOperation>();

        all.addAll( getAdd() );
        all.addAll( getChange() );
        all.addAll( getRemove() );

        return all;
    }

    public void addOperationAdd( String kind, String property )
    {
        MigrationSetKindOperationAdd op = new MigrationSetKindOperationAdd();
        op.setKind( kind );
        op.setProperty( property );
        getAdd().add( op );
    }

    public void addOperationChange( String kind, String property )
    {
        removeOperationChange( kind, property );

        MigrationSetKindOperationChange op = new MigrationSetKindOperationChange();
        op.setKind( kind );
        op.setProperty( property );
        getChange().add( op );
    }

    public void addOperationRemove( String kind, String property )
    {
        removeOperationRemove( kind, property );

        MigrationSetKindOperationRemove op = new MigrationSetKindOperationRemove();
        op.setKind( kind );
        op.setProperty( property );
        getRemove().add( op );
    }

    public void removeOperationAdd( String kind, String property )
    {
        Iterator<MigrationSetKindOperationAdd> it = getAdd().iterator();
        while ( it.hasNext() )
        {
            MigrationSetKindOperationAdd op = it.next();
            if ( kind.equals( op.getKind() ) && property.equals( op.getProperty() ) )
            {
                it.remove();
            }
        }
    }

    public void removeOperationChange( String kind, String property )
    {
        Iterator<MigrationSetKindOperationChange> it = getChange().iterator();
        while ( it.hasNext() )
        {
            MigrationSetKindOperationChange op = it.next();
            if ( kind.equals( op.getKind() ) && property.equals( op.getProperty() ) )
            {
                it.remove();
            }
        }
    }

    public void removeOperationRemove( String kind, String property )
    {
        Iterator<MigrationSetKindOperationRemove> it = getRemove().iterator();
        while ( it.hasNext() )
        {
            MigrationSetKindOperationRemove op = it.next();
            if ( kind.equals( op.getKind() ) && property.equals( op.getProperty() ) )
            {
                it.remove();
            }
        }
    }

    @Override
    public String toString()
    {
        return "MigrationSetKindOperations{" +
                "add=" + add +
                ", change=" + change +
                ", remove=" + remove +
                '}';
    }
}
