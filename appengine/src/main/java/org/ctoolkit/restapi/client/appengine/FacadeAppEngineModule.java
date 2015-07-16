package org.ctoolkit.restapi.client.appengine;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import org.ctoolkit.restapi.client.ResourceFacade;
import org.ctoolkit.restapi.client.adapter.Constants;
import org.ctoolkit.restapi.client.adapter.ResourceBinder;
import org.ctoolkit.restapi.client.adapter.ResourceFacadeAdapter;
import org.ctoolkit.restapi.client.adapter.ResourceProviderInjector;
import org.ctoolkit.restapi.client.googleapis.ApiKey;
import org.ctoolkit.restapi.client.googleapis.ApplicationName;
import org.ctoolkit.restapi.client.googleapis.DevelopmentEnvironment;
import org.ctoolkit.restapi.client.googleapis.EndpointUrl;
import org.ctoolkit.restapi.client.googleapis.GoogleApiCredentialFactory;
import org.ctoolkit.restapi.client.googleapis.P12FileName;
import org.ctoolkit.restapi.client.googleapis.ServiceAccount;

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
        bind( EventBus.class ).in( Singleton.class );
        bind( GoogleApiCredentialFactory.class ).to( GoogleApiCredentialFactoryAppEngine.class ).in( Singleton.class );
    }

    @Provides
    @Singleton
    GoogleApiCredentialFactory.Builder provideGoogleApiFactoryBuilder( GoogleApiInit holder )
    {
        GoogleApiCredentialFactory.Builder builder = new GoogleApiCredentialFactory.Builder();

        builder.setApplicationName( holder.appName );
        builder.setDevelopmentEnvironment( holder.isDevelopmentEnvironment );

        if ( holder.fileName != null )
        {
            builder.setFileName( holder.fileName );
        }

        if ( holder.serviceAccount != null )
        {
            builder.setServiceAccount( holder.serviceAccount );
        }

        if ( holder.apiKey != null )
        {
            builder.setApiKey( holder.apiKey );
        }

        if ( holder.endpointUrl != null )
        {
            builder.setEndpointUrl( holder.endpointUrl );
        }

        return builder;
    }

    static class GoogleApiInit
    {
        @Inject( optional = true )
        @ApplicationName
        String appName = Constants.DEFAULT_APP_NAME;

        @Inject( optional = true )
        @ServiceAccount
        String serviceAccount = null;

        @Inject( optional = true )
        @P12FileName
        String fileName = null;

        @Inject( optional = true )
        @ApiKey
        String apiKey = null;

        @Inject( optional = true )
        @EndpointUrl
        String endpointUrl = null;

        @Inject( optional = true )
        @DevelopmentEnvironment
        Boolean isDevelopmentEnvironment = Boolean.FALSE;
    }
}
