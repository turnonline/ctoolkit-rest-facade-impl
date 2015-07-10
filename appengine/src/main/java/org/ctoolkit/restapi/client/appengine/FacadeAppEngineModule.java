package org.ctoolkit.restapi.client.appengine;

import com.google.inject.AbstractModule;
import org.ctoolkit.restapi.client.ResourceFacade;
import org.ctoolkit.restapi.client.adapter.ResourceBinder;
import org.ctoolkit.restapi.client.adapter.ResourceFacadeAdapter;
import org.ctoolkit.restapi.client.adapter.ResourceProviderInjector;

import javax.inject.Singleton;

/**
 * The client facade API AppEngine guice module.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class FacadeAppEngineModule
        extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ResourceFacade.class ).to( ResourceFacadeAdapter.class ).in( Singleton.class );
        bind( ResourceProviderInjector.class ).to( ResourceProviderGuiceInjector.class );
        bind( ResourceBinder.class ).asEagerSingleton();
    }
}
