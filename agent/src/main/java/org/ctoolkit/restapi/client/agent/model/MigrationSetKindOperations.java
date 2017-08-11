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

    public void addOperationRemove( String kind, String property )
    {
        removeOperationRemove( kind, property );

        MigrationSetKindOperationRemove op = new MigrationSetKindOperationRemove();
        op.setKind( kind );
        op.setProperty( property );
        getRemove().add( op );
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
