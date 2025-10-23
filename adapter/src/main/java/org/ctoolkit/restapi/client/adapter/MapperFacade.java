/*
 * Copyright (c) 2025 CtoolkiT.
 */
package org.ctoolkit.restapi.client.adapter;

import java.util.List;

/**
 * Minimal mapper facade abstraction used by the adapter implementation.
 * This decouples the adapter from any specific mapping library (e.g., Orika, MapStruct).
 */
public interface MapperFacade
{
    /**
     * Maps the given source object to a new instance of the destinationClass type.
     *
     * @param source           the source object
     * @param destinationClass the target class
     * @param <D>              type of destination
     * @return mapped instance of destinationClass
     */
    <D> D map( Object source, Class<D> destinationClass );

    /**
     * Maps the given iterable of source objects to a list of destinationClass items.
     *
     * @param source           the iterable of source values
     * @param destinationClass the target class
     * @param <D>              type of destination
     * @return list of mapped items; never null
     */
    <D> List<D> mapAsList( Iterable<?> source, Class<D> destinationClass );

    /**
     * Returns a mapped remote class for the given resource type if available; otherwise returns the resource itself.
     * Implementations may return the same class when no mapping metadata is present.
     *
     * @param resource type of local resource
     * @return mapped remote class or the resource itself if not mapped
     */
    Class<?> getMappedClassOrSelf( Class<?> resource );
}
