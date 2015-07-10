package org.ctoolkit.restapi.client.appengine;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import org.ctoolkit.restapi.client.LocalResourceProvider;
import org.ctoolkit.restapi.client.adapter.ResourceProviderInjector;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;

/**
 * The Guice implementation of the injector.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class ResourceProviderGuiceInjector
        implements ResourceProviderInjector
{
    private final Injector injector;

    @Inject
    public ResourceProviderGuiceInjector( Injector injector )
    {
        this.injector = injector;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <T> LocalResourceProvider<T> getExistingResourceProvider( @Nonnull Class<T> resource )
    {
        LocalResourceProvider<T> provider = null;

        ParameterizedType pt = Types.newParameterizedType( LocalResourceProvider.class, resource );
        Binding<?> binding = injector.getExistingBinding( Key.get( TypeLiteral.get( pt ) ) );

        if ( binding != null )
        {
            provider = ( LocalResourceProvider<T> ) binding.getProvider().get();
        }

        return provider;
    }
}
