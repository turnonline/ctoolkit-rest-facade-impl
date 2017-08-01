package org.ctoolkit.restapi.client.agent.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Migration set descriptors.
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
@XmlRootElement( name = "migrationset" )
@XmlAccessorType( XmlAccessType.FIELD )
public class MigrationSet
        implements Serializable
{
    @XmlAttribute( name = "author" )
    private String author;

    @XmlAttribute( name = "comment" )
    private String comment;

    @XmlElement( name = "operations" )
    private MigrationSetKindOperations operations;

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor( String author )
    {
        this.author = author;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    public MigrationSetKindOperations getOperations()
    {
        return operations;
    }

    public void setOperations( MigrationSetKindOperations operations )
    {
        this.operations = operations;
    }

    @Override
    public String toString()
    {
        return "MigrationSet{" +
                "author='" + author + '\'' +
                ", comment='" + comment + '\'' +
                ", operations=" + operations +
                '}';
    }
}
