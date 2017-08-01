package org.ctoolkit.restapi.client.agent.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

/**
 * The bean holding migration set kind rule
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class MigrationSetKindOpRule
        implements Serializable
{
    public final static String EQUALS = "eq";
    public final static String LOWER_THAN = "lt";
    public final static String LOWER_THAN_EQUALS = "lte";
    public final static String GREATER_THAN = "gt";
    public final static String GREATER_THAN_EQUALS = "gte";
    public final static String REGEXP = "regexp";

    @XmlAttribute( name = "property" )
    private String property;

    @XmlAttribute( name = "operation" )
    private String operation;

    @XmlAttribute( name = "value" )
    private String value;

    public String getProperty()
    {
        return property;
    }

    public void setProperty( String property )
    {
        this.property = property;
    }

    public String getOperation()
    {
        return operation;
    }

    public void setOperation( String operation )
    {
        this.operation = operation;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "MigrationSetKindOpRule{" +
                "property='" + property + '\'' +
                ", operation='" + operation + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
