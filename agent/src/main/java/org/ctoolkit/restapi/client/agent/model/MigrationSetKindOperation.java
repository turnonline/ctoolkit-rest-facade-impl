package org.ctoolkit.restapi.client.agent.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

/**
 * The bean holding migration set kind operations
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
@XmlAccessorType( XmlAccessType.FIELD )
public abstract class MigrationSetKindOperation
        implements Serializable
{
    @XmlAttribute( name = "kind" )
    private String kind;

    @XmlAttribute( name = "property" )
    private String property;

    @XmlAttribute( name = "newKind" )
    private String newKind;

    @XmlAttribute( name = "newType" )
    private String newType;

    @XmlAttribute( name = "newName" )
    private String newName;

    @XmlAttribute( name = "newValue" )
    private String newValue;

    @XmlElement( name = "ruleset" )
    private MigrationSetKindOpRuleSet ruleSet;

    public String getKind()
    {
        return kind;
    }

    public void setKind( String kind )
    {
        this.kind = kind;
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty( String property )
    {
        this.property = property;
    }

    public String getNewKind()
    {
        return newKind;
    }

    public void setNewKind( String newKind )
    {
        this.newKind = newKind;
    }

    public String getNewType()
    {
        return newType;
    }

    public void setNewType( String newType )
    {
        this.newType = newType;
    }

    public String getNewName()
    {
        return newName;
    }

    public void setNewName( String newName )
    {
        this.newName = newName;
    }

    public String getNewValue()
    {
        return newValue;
    }

    public void setNewValue( String newValue )
    {
        this.newValue = newValue;
    }

    public MigrationSetKindOpRuleSet getRuleSet()
    {
        return ruleSet;
    }

    public void setRuleSet( MigrationSetKindOpRuleSet ruleSet )
    {
        this.ruleSet = ruleSet;
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "{" +
                "kind='" + kind + '\'' +
                ", property='" + property + '\'' +
                ", newKind='" + newKind + '\'' +
                ", newType='" + newType + '\'' +
                ", newName='" + newName + '\'' +
                ", newValue='" + newValue + '\'' +
                ", ruleSet=" + ruleSet +
                '}';
    }
}
