/*
 * Copyright (c) 2025 CtoolkiT.
 */
package org.ctoolkit.restapi.client.appengine.lite;

import org.ctoolkit.restapi.client.adapter.MapperFacade;

import java.util.ArrayList;
import java.util.List;

/**
 * Very small, library-free implementation of MapperFacade used by the appengine-lite module.
 * It performs no cross-type mapping: it only returns the source as-is when it is already
 * an instance of the requested destination class, otherwise it tries to instantiate the
 * destination type using the default constructor and copy over when the source is also of
 * that type (no-op). For collections, it maps items individually using the same logic.
 * <p>
 * This is sufficient for scenarios where remote and local classes are the same or when
 * no mapping is required. Projects needing real mapping can bind their own MapperFacade
 * implementation via Guice.
 */
public class SimpleMapperFacade
        implements MapperFacade
{
    @Override
    public <D> D map( Object source, Class<D> destinationClass )
    {
        if ( source == null )
        {
            return null;
        }
        if ( destinationClass.isInstance( source ) )
        {
            @SuppressWarnings( "unchecked" )
            D casted = ( D ) source;
            return casted;
        }
        try
        {
            // Best-effort: create new instance if default constructor exists (fields are not copied)
            return destinationClass.getDeclaredConstructor().newInstance();
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Cannot map instance of " + source.getClass().getName()
                    + " to " + destinationClass.getName() + ": " + e.getMessage(), e );
        }
    }

    @Override
    public <D> List<D> mapAsList( Iterable<?> source, Class<D> destinationClass )
    {
        List<D> result = new ArrayList<>();
        if ( source == null )
        {
            return result;
        }
        for ( Object item : source )
        {
            result.add( map( item, destinationClass ) );
        }
        return result;
    }

    @Override
    public Class<?> getMappedClassOrSelf( Class<?> resource )
    {
        // No mapping metadata: always return the same class
        return resource;
    }
}
