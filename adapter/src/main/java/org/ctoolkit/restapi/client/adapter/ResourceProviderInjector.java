package org.ctoolkit.restapi.client.adapter;

import org.ctoolkit.restapi.client.LocalResourceProvider;

import javax.annotation.Nonnull;

/**
 * The {@link LocalResourceProvider} injector abstraction to shade from the concrete implementation of the injection.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public interface ResourceProviderInjector
{
    /**
     * Returns the binding if it already exists, or null if does not exist.
     *
     * @param resource the type of resource to get
     * @param <T>      the concrete type of the resource
     * @return the resource provider
     */
    <T> LocalResourceProvider<T> getExistingResourceProvider( @Nonnull Class<T> resource );
}
