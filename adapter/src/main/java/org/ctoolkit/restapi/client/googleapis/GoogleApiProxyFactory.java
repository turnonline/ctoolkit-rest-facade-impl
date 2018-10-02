/*
 * Copyright (c) 2017 Comvai, s.r.o. All Rights Reserved.
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
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.PemReader;
import com.google.api.client.util.SecurityUtils;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import org.ctoolkit.restapi.client.ApiCredential;
import org.ctoolkit.restapi.client.ApiToken;
import org.ctoolkit.restapi.client.adapter.BeforeRequestEvent;
import org.ctoolkit.restapi.client.provider.AuthKeyProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;
import java.util.Map;
import java.util.MissingResourceException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.ctoolkit.restapi.client.ApiCredential.CREDENTIAL_ATTR;
import static org.ctoolkit.restapi.client.ApiCredential.DEFAULT_CREDENTIAL_PREFIX;
import static org.ctoolkit.restapi.client.ApiCredential.DEFAULT_NUMBER_OF_RETRIES;
import static org.ctoolkit.restapi.client.ApiCredential.DEFAULT_READ_TIMEOUT;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_API_KEY;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_APPLICATION_NAME;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_CLIENT_ID;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_CREDENTIAL_ON;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_DISABLE_GZIP_CONTENT;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_ENDPOINT_URL;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_FILE_NAME;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_FILE_NAME_JSON_STREAM;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_NUMBER_OF_RETRIES;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_PROJECT_ID;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_READ_TIMEOUT;
import static org.ctoolkit.restapi.client.ApiCredential.PROPERTY_SERVICE_ACCOUNT_EMAIL;

/**
 * The factory to build proxy instance to allow authenticated calls to Google APIs on behalf of the application
 * instead of an end-user by OAuth 2.0.
 * <p>
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
     * Optional authentication key provider (client responsibility).
     */
    private AuthKeyProvider keyProvider;

    /**
     * Create factory instance.
     */
    protected GoogleApiProxyFactory( @Nonnull Map<String, String> credential, @Nonnull EventBus eventBus )
    {
        this.credential = credential;
        this.eventBus = eventBus;
    }

    protected void setKeyProvider( AuthKeyProvider keyProvider )
    {
        this.keyProvider = keyProvider;
    }

    /**
     * Returns singleton instance of the HTTP transport.
     *
     * @return the reusable {@link HttpTransport} instance
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
     *
     * @return the reusable {@link JsonFactory} instance
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
     * Returns value set by {@link ApiCredential#setDisableGZipContent(boolean)} (boolean)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return true to disable GZip compression. Otherwise HTTP content will be compressed.
     */
    public final boolean isDisableGZipContent( @Nullable String prefix )
    {
        return getBoolean( PROPERTY_DISABLE_GZIP_CONTENT, prefix );
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
     * Returns value set by {@link ApiCredential#setFileNameJsonStream(String)}
     * or defined by property file.
     * If specific credential wouldn't not be found, default will be returned.
     *
     * @param prefix the prefix used to identify specific credential or null for default
     * @return the file name path
     * @throws MissingResourceException if default credential was requested and haven't been found
     */
    public final String getFileNameJsonStream( @Nullable String prefix )
    {
        return getStringValue( prefix, PROPERTY_FILE_NAME_JSON_STREAM );
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
        return getInteger( PROPERTY_NUMBER_OF_RETRIES, DEFAULT_NUMBER_OF_RETRIES, prefix );
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
        return getInteger( PROPERTY_READ_TIMEOUT, DEFAULT_READ_TIMEOUT, prefix );
    }

    /**
     * Returns the integer value for given property.
     *
     * @param property     the name of the property to retrieve
     * @param defaultValue the default value if no value will be found
     * @param prefix       the prefix used to identify specific credential or null for default
     * @return the integer value
     */
    private int getInteger( @Nonnull String property, @Nonnull String defaultValue, @Nullable String prefix )
    {
        checkNotNull( property );
        checkNotNull( defaultValue );

        if ( Strings.isNullOrEmpty( prefix ) )
        {
            prefix = DEFAULT_CREDENTIAL_PREFIX;
        }

        String value = credential.get( CREDENTIAL_ATTR + prefix + "." + property );
        if ( value == null )
        {
            value = credential.get( CREDENTIAL_ATTR + DEFAULT_CREDENTIAL_PREFIX + "." + property );
        }
        if ( value == null )
        {
            value = defaultValue;
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
        return getBoolean( PROPERTY_CREDENTIAL_ON, prefix );
    }

    /**
     * Returns the boolean value for given property.
     *
     * @param property the name of the property to retrieve
     * @param prefix   the prefix used to identify specific credential or null for default
     * @return the boolean value
     */
    private boolean getBoolean( @Nonnull String property, @Nullable String prefix )
    {
        if ( Strings.isNullOrEmpty( prefix ) )
        {
            prefix = DEFAULT_CREDENTIAL_PREFIX;
        }

        String fullProperty = CREDENTIAL_ATTR + prefix + "." + property;
        String value = credential.get( fullProperty );

        if ( value == null )
        {
            fullProperty = CREDENTIAL_ATTR + DEFAULT_CREDENTIAL_PREFIX + "." + property;
            value = credential.get( fullProperty );
        }

        if ( value == null )
        {
            return false;
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
     */
    public ApiToken<? extends HttpRequestInitializer> authorize( Collection<String> scopes,
                                                                 String userAccount,
                                                                 String prefix )
            throws GeneralSecurityException, IOException
    {
        GoogleCredential googleCredential;

        if ( isJsonConfiguration( prefix ) )
        {
            // json file load right before usage
            InputStream json = getServiceAccountJsonStream( prefix );

            googleCredential = new ConfiguredByJsonGoogleCredential( json, prefix )
                    .setTransport( getHttpTransport() )
                    .setJsonFactory( getJsonFactory() )
                    .setServiceAccountScopes( scopes )
                    .setServiceAccountUser( userAccount )
                    .build();
        }
        else
        {
            String serviceAccountEmail = getServiceAccountEmail( prefix );
            if ( serviceAccountEmail == null )
            {
                throw new NullPointerException( "Missing service account email." );
            }

            // p12 file load right before usage
            URL resource = getServiceAccountPrivateKeyP12Resource( prefix );

            googleCredential = new ConfiguredGoogleCredential( prefix )
                    .setTransport( getHttpTransport() )
                    .setJsonFactory( getJsonFactory() )
                    .setServiceAccountId( serviceAccountEmail )
                    .setServiceAccountScopes( scopes )
                    .setServiceAccountPrivateKeyFromP12File( new File( resource.getPath() ) )
                    .setServiceAccountUser( userAccount )
                    .build();
        }

        return new CredentialApiToken( googleCredential );
    }

    public boolean isJsonConfiguration( String prefix )
    {
        if ( keyProvider != null && keyProvider.isConfigured( prefix ) )
        {
            // if defined authentication key takes precedence
            return true;
        }

        try
        {
            return getFileNameJsonStream( prefix ) != null;
        }
        catch ( MissingResourceException e )
        {
            return false;
        }
    }

    public URL getServiceAccountPrivateKeyP12Resource( String prefix )
    {
        String fileName = getFileName( prefix );
        return GoogleApiProxyFactory.class.getResource( fileName );
    }

    /**
     * Returns the Google APIs service account key as JSON.
     *
     * @param prefix the prefix used to identify specific credential
     * @return the service account key as JSON
     */
    public InputStream getServiceAccountJsonStream( String prefix )
    {
        InputStream stream;

        if ( keyProvider != null && keyProvider.isConfigured( prefix ) )
        {
            stream = keyProvider.get( prefix );
        }
        else
        {
            String fileName = getFileNameJsonStream( prefix );
            stream = GoogleApiProxyFactory.class.getResourceAsStream( fileName );

            if ( stream == null )
            {
                throw new IllegalArgumentException( "No file has been found with name '" + fileName + "'" );
            }
        }
        return stream;
    }

    public InputStream getServiceAccountPrivateKeyP12Stream( String prefix )
    {
        String fileName = getFileName( prefix );
        return GoogleApiProxyFactory.class.getResourceAsStream( fileName );
    }

    public HttpRequestInitializer newRequestConfig( @Nullable String prefix,
                                                    @Nullable HttpResponseInterceptor interceptor )
    {
        return new RequestConfig( prefix, interceptor );
    }

    private PrivateKey privateKeyFromPkcs8( String privateKeyPem ) throws IOException
    {
        Reader reader = new StringReader( privateKeyPem );
        PemReader.Section section = PemReader.readFirstSectionAndClose( reader, "PRIVATE KEY" );
        if ( section == null )
        {
            throw new IOException( "Invalid PKCS8 data." );
        }

        byte[] bytes = section.getBase64DecodedBytes();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec( bytes );

        try
        {
            KeyFactory keyFactory = SecurityUtils.getRsaKeyFactory();
            return keyFactory.generatePrivate( keySpec );
        }
        catch ( NoSuchAlgorithmException | InvalidKeySpecException e )
        {
            throw new IOException( "Unexpected exception reading PKCS data", e );
        }
    }

    /**
     * Configure HTTP request right before execution.
     *
     * @param request         the HTTP request
     * @param numberOfRetries the number of configured retries
     * @param readTimeout     the request read timeout in milliseconds
     */
    protected final void configureHttpRequest( @Nonnull HttpRequest request, int numberOfRetries, int readTimeout )
    {
        request.setNumberOfRetries( numberOfRetries );
        request.setReadTimeout( readTimeout );
    }

    private class RequestConfig
            implements HttpRequestInitializer
    {
        private final int numberOfRetries;

        private final int readTimeout;

        private final HttpResponseInterceptor responseInterceptor;

        HttpExecuteInterceptor interceptor = new HttpExecuteInterceptor()
        {
            @Override
            public void intercept( HttpRequest request ) throws IOException
            {
                eventBus.post( new BeforeRequestEvent( request ) );
            }
        };

        private RequestConfig( String prefix, HttpResponseInterceptor responseInterceptor )
        {
            this.numberOfRetries = getNumberOfRetries( prefix );
            this.readTimeout = getReadTimeout( prefix );
            this.responseInterceptor = responseInterceptor;
        }

        public void initialize( HttpRequest request )
        {
            configureHttpRequest( request, numberOfRetries, readTimeout );
            request.setInterceptor( interceptor );

            if ( responseInterceptor != null )
            {
                request.setResponseInterceptor( responseInterceptor );
            }
        }
    }

    /**
     * Custom GoogleCredential implementation
     */
    private class ConfiguredGoogleCredential
            extends GoogleCredential.Builder
    {
        private final int numberOfRetries;

        private final int readTimeout;

        private ConfiguredGoogleCredential( String prefix )
        {
            this.numberOfRetries = getNumberOfRetries( prefix );
            this.readTimeout = getReadTimeout( prefix );
        }

        @Override
        public GoogleCredential build()
        {
            return new GoogleCredential( this )
            {
                @Override
                public void intercept( HttpRequest request ) throws IOException
                {
                    String authorization = request.getHeaders().getAuthorization();
                    // the authorization header set by facade client has a preference, see Request#authBy(String)
                    if ( authorization == null )
                    {
                        super.intercept( request );
                    }
                    eventBus.post( new BeforeRequestEvent( request ) );
                }

                @Override
                public void initialize( HttpRequest request ) throws IOException
                {
                    super.initialize( request );
                    configureHttpRequest( request, numberOfRetries, readTimeout );
                }
            };
        }
    }

    private class ConfiguredByJsonGoogleCredential
            extends ConfiguredGoogleCredential
    {
        public ConfiguredByJsonGoogleCredential( InputStream jsonStream, String prefix ) throws IOException
        {
            super( prefix );
            JsonObjectParser parser = new JsonObjectParser( GoogleApiProxyFactory.this.getJsonFactory() );
            GenericJson fileContents = parser.parseAndClose( jsonStream, Charsets.UTF_8, GenericJson.class );

            String clientId = ( String ) fileContents.get( "client_id" );
            String clientEmail = ( String ) fileContents.get( "client_email" );
            String privateKeyPem = ( String ) fileContents.get( "private_key" );
            String privateKeyId = ( String ) fileContents.get( "private_key_id" );

            if ( clientId == null || clientEmail == null || privateKeyPem == null || privateKeyId == null )
            {
                throw new IOException( "Error reading service account credential from stream, "
                        + "expecting  'client_id', 'client_email', 'private_key' and 'private_key_id'." );
            }

            PrivateKey privateKey = privateKeyFromPkcs8( privateKeyPem );

            // setup credential from json
            setServiceAccountId( clientEmail );
            setServiceAccountPrivateKey( privateKey );
            setServiceAccountPrivateKeyId( privateKeyId );
        }
    }
}
