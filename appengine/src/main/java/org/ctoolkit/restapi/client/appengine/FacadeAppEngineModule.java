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
import org.ctoolkit.restapi.client.googleapis.ClientId;
import org.ctoolkit.restapi.client.googleapis.DevelopmentEnvironment;
import org.ctoolkit.restapi.client.googleapis.EndpointUrl;
import org.ctoolkit.restapi.client.googleapis.GoogleApiCredentialFactory;
import org.ctoolkit.restapi.client.googleapis.P12FileName;
import org.ctoolkit.restapi.client.googleapis.ProjectId;
import org.ctoolkit.restapi.client.googleapis.ServiceAccountEmail;

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

        if ( holder.projectId != null )
        {
            builder.setProjectId( holder.projectId );
        }

        if ( holder.clientId != null )
        {
            builder.setClientId( holder.clientId );
        }

        if ( holder.serviceAccountEmail != null )
        {
            builder.setServiceAccountEmail( holder.serviceAccountEmail );
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
        @ProjectId
        String projectId = null;

        @Inject( optional = true )
        @ClientId
        String clientId = null;

        @Inject( optional = true )
        @ApplicationName
        String appName = Constants.DEFAULT_APP_NAME;

        @Inject( optional = true )
        @ServiceAccountEmail
        String serviceAccountEmail = null;

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
