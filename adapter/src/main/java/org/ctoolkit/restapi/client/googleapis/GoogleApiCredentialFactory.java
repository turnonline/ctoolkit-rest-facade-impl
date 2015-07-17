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

package org.ctoolkit.restapi.client.googleapis;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.eventbus.EventBus;
import org.ctoolkit.restapi.client.adapter.BeforeRequestEvent;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collection;

/**
 * The factory to build credential instance to allow calls to Google APIs on behalf of the application
 * instead of an end-user by OAuth 2.0.
 * <p/>
 * OAuth 2.0 allows users to share specific data with application (for example, contact lists)
 * while keeping their usernames, passwords, and other information private.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public abstract class GoogleApiCredentialFactory
{
    protected final String serviceAccount;

    protected final String applicationName;

    protected final String fileName;

    protected final String apiKey;

    protected final String endpointUrl;

    protected final boolean isDevelopmentEnvironment;

    protected final EventBus eventBus;

    protected int numberOfRetries = 1;

    private HttpTransport httpTransport;

    private JsonFactory jsonFactory;

    /**
     * Create factory instance.
     */
    protected GoogleApiCredentialFactory( @Nonnull Builder builder, @Nonnull EventBus eventBus )
    {
        this.eventBus = eventBus;
        this.serviceAccount = builder.serviceAccount;
        this.applicationName = builder.applicationName;
        this.fileName = builder.fileName;
        this.apiKey = builder.apiKey;
        this.endpointUrl = builder.endpointUrl;
        this.isDevelopmentEnvironment = builder.developmentEnvironment;
        this.numberOfRetries = builder.numberOfRetries;
    }

    /**
     * Returns singleton instance of the HTTP transport.
     */
    public final HttpTransport getHttpTransport() throws GeneralSecurityException, IOException
    {
        if ( httpTransport == null )
        {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        }
        return httpTransport;
    }

    /**
     * Returns singleton instance of the JSON factory.
     */
    public final JsonFactory getJsonFactory()
    {
        if ( jsonFactory == null )
        {
            jsonFactory = JacksonFactory.getDefaultInstance();
        }
        return jsonFactory;
    }

    public final String getServiceAccount()
    {
        return serviceAccount;
    }

    public final String getApplicationName()
    {
        return applicationName;
    }

    public final String getFileName()
    {
        return fileName;
    }

    public final String getApiKey()
    {
        return apiKey;
    }

    public final String getEndpointUrl()
    {
        return endpointUrl;
    }

    public final boolean isDevelopmentEnvironment()
    {
        return isDevelopmentEnvironment;
    }

    /**
     * Creates the thread-safe Google-specific implementation of the OAuth 2.0.
     *
     * @param scopes      the space-separated OAuth scopes to use with the the service account flow
     *                    or {@code null} for none.
     * @param userAccount the email address. If you want to impersonate a user account, specify the email address.
     *                    Useful for domain-wide delegation.
     * @return the thread-safe credential instance
     * @throws GeneralSecurityException, IOException
     */
    public HttpRequestInitializer authorize( Collection<String> scopes, String userAccount )
            throws GeneralSecurityException, IOException
    {
        if ( serviceAccount == null || fileName == null )
        {
            throw new NullPointerException();
        }

        // p12 file load right before usage
        URL resource = GoogleApiCredentialFactory.class.getResource( fileName );

        return new ConfiguredGoogleCredential().setTransport( getHttpTransport() )
                .setJsonFactory( getJsonFactory() )
                .setServiceAccountId( serviceAccount )
                .setServiceAccountScopes( scopes )
                .setServiceAccountPrivateKeyFromP12File( new File( resource.getPath() ) )
                .setServiceAccountUser( userAccount )
                .build();
    }

    /**
     * Factory arguments builder.
     */
    public static class Builder
    {
        private String serviceAccount;

        private String applicationName;

        private String fileName;

        private String apiKey;

        private String endpointUrl;

        private boolean developmentEnvironment;

        private int numberOfRetries = 1;

        /**
         * Sets the service account ID (typically an e-mail address).
         */
        public Builder setServiceAccount( String serviceAccount )
        {
            this.serviceAccount = serviceAccount;
            return this;
        }

        /**
         * Sets the name of your application. If the application name is {@code null} or
         * blank, the application will use default name {@link org.ctoolkit.restapi.client.adapter.Constants#DEFAULT_APP_NAME}.
         */
        public Builder setApplicationName( String applicationName )
        {
            this.applicationName = applicationName;
            return this;
        }

        /**
         * Sets the name to the key p12 file.
         */
        public Builder setFileName( String fileName )
        {
            this.fileName = fileName;
            return this;
        }

        /**
         * Sets the API Key.
         *
         * @param apiKey the API Key to be set
         */
        public void setApiKey( String apiKey )
        {
            this.apiKey = apiKey;
        }

        /**
         * Sets the endpoint URL.
         */
        public Builder setEndpointUrl( String endpointUrl )
        {
            this.endpointUrl = endpointUrl;
            return this;
        }

        /**
         * Sets the boolean identification whether current environment is development or not.
         * <tt>true</tt>, means app is running on local.
         */
        public Builder setDevelopmentEnvironment( boolean developmentEnvironment )
        {
            this.developmentEnvironment = developmentEnvironment;
            return this;
        }

        /**
         * Sets the number of retries that will be allowed to execute before the request will be
         * terminated or {@code 0} to not retry requests.
         * <p>
         * The default value is {@code 1}.
         * </p>
         *
         * @see HttpRequest#setNumberOfRetries(int)
         */
        public void setNumberOfRetries( int numberOfRetries )
        {
            this.numberOfRetries = numberOfRetries;
        }
    }

    /**
     * Custom GoogleCredential implementation
     */
    private class ConfiguredGoogleCredential
            extends GoogleCredential.Builder
    {
        @Override
        public GoogleCredential build()
        {
            return new GoogleCredential( this )
            {
                @Override
                public void intercept( HttpRequest request ) throws IOException
                {
                    super.intercept( request );
                    eventBus.post( new BeforeRequestEvent( request ) );
                }

                @Override
                public void initialize( HttpRequest request ) throws IOException
                {
                    super.initialize( request );
                    request.setNumberOfRetries( numberOfRetries );
                }
            };
        }
    }
}
