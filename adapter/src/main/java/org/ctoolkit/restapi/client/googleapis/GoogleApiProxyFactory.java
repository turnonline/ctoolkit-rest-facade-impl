/*
 * Copyright (c) 2016 Comvai, s.r.o. All Rights Reserved.
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
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import org.ctoolkit.restapi.client.ApiCredential;
import org.ctoolkit.restapi.client.adapter.BeforeRequestEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Map;
import java.util.MissingResourceException;

import static com.google.common.base.Preconditions.checkArgument;
import static org.ctoolkit.restapi.client.ApiCredential.CREDENTIAL_ATTR;
import static org.ctoolkit.restapi.client.ApiCredential.DEFAULT_CREDENTIAL_PREFIX;
import static org.ctoolkit.restapi.client.ApiCredential.DEFAULT_NUMBER_OF_RETRIES;
import static org.ctoolkit.restapi.client.ApiCredential.DEFAULT_READ_TIMEOUT;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_API_KEY;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_APPLICATION_NAME;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_CLIENT_ID;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_CREDENTIAL_ON;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_ENDPOINT_URL;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_FILE_NAME;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_NUMBER_OF_RETRIES;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_PROJECT_ID;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_READ_TIMEOUT;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_SERVICE_ACCOUNT_EMAIL;

/**
 * The factory to build proxy instance to allow authenticated calls to Google APIs on behalf of the application
 * instead of an end-user by OAuth 2.0.
 * <p/>
 * OAuth 2.0 allows users to share specific data with application (for example, contact lists)
 * while keeping their usernames, passwords, and other information private.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public abstract class GoogleApiProxyFactory
{
    protected final EventBus eventBus;

    private final Map<String, String> credential;

    private HttpTransport httpTransport;

    private JsonFactory jsonFactory;

    /**
     * Create factory instance.
     */
    protected GoogleApiProxyFactory( @Nonnull Map<String, String> credential, @Nonnull EventBus eventBus )
    {
        this.credential = credential;
        this.eventBus = eventBus;
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

    /**
     * Returns value set by {@link ApiCredential#setProjectId(String)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the project ID
     * @throws MissingResourceException if default credential was requested and haven't been found
     */
    public String getProjectId( @Nullable String prefix )
    {
        return getStringValue( prefix, PROPERTY_PROJECT_ID );
    }

    /**
     * Returns value set by {@link ApiCredential#setClientId(String)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the client ID
     * @throws MissingResourceException if default credential was requested and haven't been found
     */
    public String getClientId( @Nullable String prefix )
    {
        return getStringValue( prefix, PROPERTY_CLIENT_ID );
    }

    /**
     * Returns value set by {@link ApiCredential#setServiceAccountEmail(String)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the service email
     * @throws MissingResourceException if default credential was requested and haven't been found
     */
    public final String getServiceAccountEmail( @Nullable String prefix )
    {
        return getStringValue( prefix, PROPERTY_SERVICE_ACCOUNT_EMAIL );
    }

    /**
     * Returns value set by {@link ApiCredential#setApplicationName(String)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the application name
     * @throws MissingResourceException if default credential was requested and haven't been found
     */
    public final String getApplicationName( @Nullable String prefix )
    {
        return getStringValue( prefix, PROPERTY_APPLICATION_NAME );
    }

    /**
     * Returns value set by {@link ApiCredential#setFileName(String)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the file name path
     * @throws MissingResourceException if default credential was requested and haven't been found
     */
    public final String getFileName( @Nullable String prefix )
    {
        return getStringValue( prefix, PROPERTY_FILE_NAME );
    }

    /**
     * Returns value set by {@link ApiCredential#setApiKey(String)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the API key
     * @throws MissingResourceException if default credential was requested and haven't been found
     */
    public final String getApiKey( @Nullable String prefix )
    {
        return getStringValue( prefix, PROPERTY_API_KEY );
    }

    /**
     * Returns value set by {@link ApiCredential#setEndpointUrl(String)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the endpoint URL
     * @throws MissingResourceException if default credential was requested and haven't been found
     */
    public final String getEndpointUrl( @Nullable String prefix )
    {
        return getStringValue( prefix, PROPERTY_ENDPOINT_URL );
    }

    /**
     * Returns value set by {@link ApiCredential#setNumberOfRetries(int)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the number of configured retries
     */
    public int getNumberOfRetries( @Nullable String prefix )
    {
        if ( Strings.isNullOrEmpty( prefix ) )
        {
            prefix = DEFAULT_CREDENTIAL_PREFIX;
        }

        String property = PROPERTY_NUMBER_OF_RETRIES;
        String value = credential.get( CREDENTIAL_ATTR + prefix + "." + property );
        if ( value == null )
        {
            value = credential.get( CREDENTIAL_ATTR + DEFAULT_CREDENTIAL_PREFIX + "." + property );
        }
        if ( value == null )
        {
            value = DEFAULT_NUMBER_OF_RETRIES;
        }
        return Integer.valueOf( value );
    }

    /**
     * Returns value set by {@link ApiCredential#setRequestReadTimeout(int)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned 20000 (20 seconds).
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the request read timeout in milliseconds
     */
    public int getReadTimeout( @Nullable String prefix )
    {
        if ( Strings.isNullOrEmpty( prefix ) )
        {
            prefix = DEFAULT_CREDENTIAL_PREFIX;
        }

        String property = PROPERTY_READ_TIMEOUT;
        String value = credential.get( CREDENTIAL_ATTR + prefix + "." + property );
        if ( value == null )
        {
            value = credential.get( CREDENTIAL_ATTR + DEFAULT_CREDENTIAL_PREFIX + "." + property );
        }
        if ( value == null )
        {
            value = DEFAULT_READ_TIMEOUT;
        }
        return Integer.valueOf( value );
    }

    /**
     * Returns value set by {@link ApiCredential#setCredentialOn(boolean)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the true if credential will be used to authenticate
     */
    public final boolean isCredentialOn( @Nullable String prefix )
    {
        if ( Strings.isNullOrEmpty( prefix ) )
        {
            prefix = DEFAULT_CREDENTIAL_PREFIX;
        }

        String fullProperty = CREDENTIAL_ATTR + prefix + "." + PROPERTY_CREDENTIAL_ON;
        String value = credential.get( fullProperty );

        if ( value == null )
        {
            fullProperty = CREDENTIAL_ATTR + DEFAULT_CREDENTIAL_PREFIX + "." + PROPERTY_CREDENTIAL_ON;
            value = credential.get( fullProperty );
        }

        if ( value == null )
        {
            return true;
        }

        return Boolean.valueOf( value );
    }

    private String getStringValue( String prefix, String property )
    {
        checkArgument( !Strings.isNullOrEmpty( property ) );
        if ( Strings.isNullOrEmpty( prefix ) )
        {
            prefix = DEFAULT_CREDENTIAL_PREFIX;
        }

        String value = credential.get( CREDENTIAL_ATTR + prefix + "." + property );
        if ( value == null )
        {
            value = credential.get( CREDENTIAL_ATTR + DEFAULT_CREDENTIAL_PREFIX + "." + property );
        }

        defaultPropertyValueCheck( prefix, property, value );

        return value;
    }

    private void defaultPropertyValueCheck( String prefix, String property, String value )
    {
        if ( value != null || !DEFAULT_CREDENTIAL_PREFIX.equals( prefix ) )
        {
            return;
        }

        String fullProperty = CREDENTIAL_ATTR + DEFAULT_CREDENTIAL_PREFIX + "." + property;
        String className = ApiCredential.class.getName();
        String message = "No value configured for default credential: '" + fullProperty + "'";
        throw new MissingResourceException( message, className, fullProperty );
    }

    /**
     * Creates the thread-safe Google-specific implementation of the OAuth 2.0.
     *
     * @param scopes      the space-separated OAuth scopes to use with the service account flow
     *                    or {@code null} for none.
     * @param userAccount the email address. If you want to impersonate a user account, specify the email address.
     *                    Useful for domain-wide delegation.
     * @return the thread-safe credential instance
     * @throws GeneralSecurityException, IOException
     */
    public HttpRequestInitializer authorize( Collection<String> scopes, String userAccount, String prefix )
            throws GeneralSecurityException, IOException
    {
        String serviceAccountEmail = getServiceAccountEmail( prefix );
        if ( serviceAccountEmail == null )
        {
            throw new NullPointerException( "Missing service account email." );
        }

        // p12 file load right before usage
        URL resource = getServiceAccountPrivateKeyP12Resource( prefix );

        return new ConfiguredGoogleCredential( prefix ).setTransport( getHttpTransport() )
                .setJsonFactory( getJsonFactory() )
                .setServiceAccountId( serviceAccountEmail )
                .setServiceAccountScopes( scopes )
                .setServiceAccountPrivateKeyFromP12File( new File( resource.getPath() ) )
                .setServiceAccountUser( userAccount )
                .setRequestInitializer( newRequestConfig( prefix ) )
                .build();
    }

    public URL getServiceAccountPrivateKeyP12Resource( String prefix )
    {
        String fileName = getFileName( prefix );
        return GoogleApiProxyFactory.class.getResource( fileName );
    }

    public InputStream getServiceAccountPrivateKeyP12Stream( String prefix )
    {
        String fileName = getFileName( prefix );
        return GoogleApiProxyFactory.class.getResourceAsStream( fileName );
    }

    public HttpRequestInitializer newRequestConfig( @Nullable String prefix )
    {
        return new RequestConfig( prefix );
    }

    private class RequestConfig
            implements HttpRequestInitializer
    {
        private final int numberOfRetries;

        private final int readTimeout;

        private RequestConfig( String prefix )
        {
            this.numberOfRetries = getNumberOfRetries( prefix );
            this.readTimeout = getReadTimeout( prefix );
        }

        public void initialize( HttpRequest request )
        {
            request.setNumberOfRetries( numberOfRetries );
            request.setReadTimeout( readTimeout );
        }
    }

    /**
     * Custom GoogleCredential implementation
     */
    private class ConfiguredGoogleCredential
            extends GoogleCredential.Builder
    {
        private final String prefix;

        ConfiguredGoogleCredential( String prefix )
        {
            this.prefix = prefix;
        }

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
            };
        }
    }
}
