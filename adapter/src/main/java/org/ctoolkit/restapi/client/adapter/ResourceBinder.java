/*
 * Copyright (c) 2015 Comvai, s.r.o. All Rights Reserved.
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

package org.ctoolkit.restapi.client.adapter;

import org.ctoolkit.restapi.client.adaptee.RestExecutorAdaptee;

import java.util.HashMap;
import java.util.Map;

/**
 * Map of bidirectional bindings between source class type, target class type and adaptee implementation.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class ResourceBinder
{
    private Map<Class<?>, Class<?>> modelMapper = new HashMap<>();

    private Map<Class<?>, Object> adaptees = new HashMap<>();

    private Map<Object, Class<?>> inverseAdaptees = new HashMap<>();

    public ResourceBinder()
    {
    }

    /**
     * Makes binding between given class types and concrete adaptee implementation.
     *
     * @param target  the target class type
     * @param source  the source class type
     * @param adaptee the concrete implementation of the adaptee to be associated
     */
    public <S, T> void bind( Class<T> target,
                             Class<S> source,
                             Object adaptee )
    {
        modelMapper.put( target, source );
        adaptees.put( target, adaptee );
        inverseAdaptees.put( adaptee, target );
    }

    /**
     * Returns the source class type associated with given target class.
     *
     * @param clazz the class type as a key
     * @return the source class type associated with given class
     */
    public Class<?> getSourceClassFor( Class<?> clazz )
    {
        return modelMapper.get( clazz );
    }

    /**
     * Returns the target model class type associated with given adaptee implementation.
     *
     * @param adaptee the concrete implementation of the adaptee as a key
     * @return the model class type
     */
    public Class<?> targetType( RestExecutorAdaptee adaptee )
    {
        return inverseAdaptees.get( adaptee );
    }

    /**
     * Returns the adaptee implementation associated with given model class type.
     *
     * @param clazz the class type as a key
     * @return the class type specific adaptee implementation
     */
    @SuppressWarnings( "unchecked" )
    public RestExecutorAdaptee<Object, Object, Object> adaptee( Class<?> clazz )
    {
        return ( RestExecutorAdaptee<Object, Object, Object> ) adaptees.get( clazz );
    }
}
