package org.ctoolkit.restapi.client.adapter;

import org.ctoolkit.restapi.client.RestExecutorAdaptee;

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

    private Map<Class<?>, RestExecutorAdaptee> adaptees = new HashMap<>();

    private Map<RestExecutorAdaptee, Class<?>> inverseAdaptees = new HashMap<>();

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
                             RestExecutorAdaptee adaptee )
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
    public RestExecutorAdaptee adaptee( Class<?> clazz )
    {
        return adaptees.get( clazz );
    }
}
