package org.ctoolkit.restapi.client.migration.model;

/**
 * Metadata for entity kind
 *
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class PropertyMetaData
{
    private String property;

    private String type;

    private String kind;

    private String namespace;

    public String getProperty()
    {
        return property;
    }

    public void setProperty( String property )
    {
        this.property = property;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind( String kind )
    {
        this.kind = kind;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public void setNamespace( String namespace )
    {
        this.namespace = namespace;
    }

    @Override
    public String toString()
    {
        return "PropertyMetaData{" +
                "property='" + property + '\'' +
                ", type='" + type + '\'' +
                ", kind='" + kind + '\'' +
                ", namespace='" + namespace + '\'' +
                '}';
    }
}
