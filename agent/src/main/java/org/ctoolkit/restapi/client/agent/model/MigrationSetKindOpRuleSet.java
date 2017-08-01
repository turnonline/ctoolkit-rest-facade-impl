package org.ctoolkit.restapi.client.agent.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The bean holding migration set kind operation restrictions
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class MigrationSetKindOpRuleSet
        implements Serializable
{
    public static final String AND = "and";
    public static final String OR = "or";

    @XmlAttribute( name = "operation" )
    private String operation;

    @XmlElement( name = "rule" )
    private List<MigrationSetKindOpRule> rules;

    public String getOperation()
    {
        return operation;
    }

    public void setOperation( String operation )
    {
        this.operation = operation;
    }

    public List<MigrationSetKindOpRule> getRules()
    {
        if ( rules == null )
        {
            rules = new ArrayList<MigrationSetKindOpRule>();
        }
        return rules;
    }

    public void setRules( List<MigrationSetKindOpRule> rules )
    {
        this.rules = rules;
    }

    @Override
    public String toString()
    {
        return "MigrationSetKindOpRestriction{" +
                ", operation='" + operation + '\'' +
                ", rules=" + rules +
                '}';
    }
}
