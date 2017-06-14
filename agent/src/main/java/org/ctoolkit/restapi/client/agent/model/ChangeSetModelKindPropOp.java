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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

/**
 * The bean holding entity kind property model update description
 * Example:
 * <table border="1">
 * <tr>
 * <th>Operation</th>
 * <th>Example</th>
 * </tr>
 * <tr>
 * <td>add</td>
 * <td>{@code <kindprop op="add" kind="User" newName="age" property="string" newValue="{optional-default-value}"/>}</td>
 * </tr>
 * <tr>
 * <td>remove</td>
 * <td>{@code <kindprop op="remove" kind="User" property="age"/>}</td>
 * </tr>
 * <tr>
 * <td>change</td>
 * <td>{@code <kindprop op="change" kind="User" property="age" newName="business-age" newType="text" newValue="{optional-default-value}"/>}</td>
 * </table>
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class ChangeSetModelKindPropOp
        implements Serializable
{
    /**
     * Possible operations defined on Kind properties
     */
    public final static String OP_ADD = "add";

    public final static String OP_REMOVE = "remove";

    public final static String OP_CHANGE = "change";

    @XmlAttribute( name = "op" )
    private String op;

    @XmlAttribute( name = "kind" )
    private String kind;

    @XmlAttribute( name = "property" )
    private String property;

    @XmlAttribute( name = "newName" )
    private String newName;

    @XmlAttribute( name = "newType" )
    private String newType;

    @XmlAttribute( name = "newValue" )
    private String newValue;

    public String getOp()
    {
        return op;
    }

    public void setOp( String op )
    {
        this.op = op;
    }

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

    public String getNewName()
    {
        return newName;
    }

    public void setNewName( String newName )
    {
        this.newName = newName;
    }

    public String getNewType()
    {
        return newType;
    }

    public void setNewType( String newType )
    {
        this.newType = newType;
    }

    public String getNewValue()
    {
        return newValue;
    }

    public void setNewValue( String newValue )
    {
        this.newValue = newValue;
    }

    @Override
    public String toString()
    {
        return "ChangeSetModelKindPropOp{" +
                "op='" + op + '\'' +
                ", kind='" + kind + '\'' +
                ", property='" + property + '\'' +
                ", newName='" + newName + '\'' +
                ", newType='" + newType + '\'' +
                ", newValue='" + newValue + '\'' +
                '}';
    }
}
