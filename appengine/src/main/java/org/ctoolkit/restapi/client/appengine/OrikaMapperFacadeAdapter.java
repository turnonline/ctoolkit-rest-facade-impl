package org.ctoolkit.restapi.client.appengine;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;
import org.ctoolkit.restapi.client.adapter.MapperFacade;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Adapter that bridges Orika's MapperFacade/MapperFactory to the internal MapperFacade abstraction.
 */
public class OrikaMapperFacadeAdapter
        implements MapperFacade
{
    private final ma.glasnost.orika.MapperFacade delegate;
    private final MapperFactory factory;

    @Inject
    public OrikaMapperFacadeAdapter( ma.glasnost.orika.MapperFacade delegate, MapperFactory factory )
    {
        this.delegate = delegate;
        this.factory = factory;
    }

    @Override
    public <D> D map( Object source, Class<D> destinationClass )
    {
        return delegate.map( source, destinationClass );
    }

    @Override
    public <D> List<D> mapAsList( Iterable<?> source, Class<D> destinationClass )
    {
        // Orika has mapAsList for Iterable
        if ( source == null )
        {
            return new ArrayList<>();
        }
        return delegate.mapAsList( source, destinationClass );
    }

    @Override
    public Class<?> getMappedClassOrSelf( Class<?> resource )
    {
        Set<Type<?>> types = factory.lookupMappedClasses( TypeFactory.valueOf( resource ) );
        Iterator<Type<?>> iterator = types.iterator();
        if ( iterator.hasNext() )
        {
            return iterator.next().getRawType();
        }
        return resource;
    }
}
