package org.ctoolkit.restapi.client.googleapis;

import com.google.api.client.http.HttpRequest;
import com.google.common.base.Strings;
import org.ctoolkit.restapi.client.adapter.Constants;

import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The Google API credential holder represents a map of credential used to authenticate client calls to APIs.
 * Default credential will be always used if any specific wouldn't be found.
 * Specific credential are optional unless default credential are defined.
 * <p>
 * Values are part of the binding thus available via injection like:
 * <pre>
 *   public class MyClass {
 *     &#064;Inject <b>@Named("credential.default.clientId")</b> String clientId;
 *     ...
 *   }</pre>
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GoogleApiCredential
        extends Properties
{
    static final String DEFAULT_CREDENTIAL_PREFIX = "default";

    static final String PROPERTY_PROJECT_ID = "projectId";

    static final String PROPERTY_CLIENT_ID = "clientId";

    static final String PROPERTY_SERVICE_ACCOUNT_EMAIL = "serviceAccountEmail";

    static final String PROPERTY_APPLICATION_NAME = "appName";

    static final String PROPERTY_FILE_NAME = "fileName";

    static final String PROPERTY_API_KEY = "apiKey";

    static final String PROPERTY_ENDPOINT_URL = "endpointUrl";

    static final String PROPERTY_CREDENTIAL_ON = "credentialOn";

    static final String PROPERTY_NUMBER_OF_RETRIES = "numberOfRetries";

    static final String CREDENTIAL_ATTR = "credential.";

    private static final long serialVersionUID = -2258904700906913513L;

    private final String prefix;

    /**
     * Creates default credential used to authenticate all calls unless specified another instance for a specific API.
     * Setting following properties will be configured as default credential.
     * <ul>
     * <li>{@link #setProjectId(String)}</li>
     * <li>{@link #setClientId(String)}</li>
     * <li>{@link #setServiceAccountEmail(String)}</li>
     * <li>{@link #setApplicationName(String)}</li>
     * <li>{@link #setFileName(String)}</li>
     * <li>{@link #setApiKey(String)}</li>
     * <li>{@link #setEndpointUrl(String)}</li>
     * <li>{@link #setCredentialOn(boolean)}</li>
     * <li>{@link #setNumberOfRetries(int)}</li>
     * </ul>
     */
    public GoogleApiCredential()
    {
        this( DEFAULT_CREDENTIAL_PREFIX );
    }

    /**
     * Creates specific credential used to authenticate calls to a specific API.
     * Setting following properties will be configured as specific credential - prefix based.
     * <ul>
     * <li>{@link #setProjectId(String)}</li>
     * <li>{@link #setClientId(String)}</li>
     * <li>{@link #setServiceAccountEmail(String)}</li>
     * <li>{@link #setApplicationName(String)}</li>
     * <li>{@link #setFileName(String)}</li>
     * <li>{@link #setApiKey(String)}</li>
     * <li>{@link #setEndpointUrl(String)}</li>
     * <li>{@link #setCredentialOn(boolean)}</li>
     * <li>{@link #setNumberOfRetries(int)}</li>
     * </ul>
     *
     * @param prefix the prefix used to identify specific credential
     */
    public GoogleApiCredential( String prefix )
    {
        checkArgument( !Strings.isNullOrEmpty( prefix ), "Prefix cannot be null or empty!" );
        this.prefix = prefix + ".";
        setApplicationName( null );
    }

    /**
     * Creates credential map used to authenticate API calls.
     * It's client responsibility to make sure that map has at least a default credential configured.
     * <p>
     * <ul>
     * <li>credential.default.projectId</li>
     * <li>credential.default.clientId</li>
     * <li>credential.default.serviceAccountEmail</li>
     * <li>credential.default.appName</li>
     * <li>credential.default.fileName</li>
     * <li>credential.default.apiKey</li>
     * <li>credential.default.endpointUrl</li>
     * <li>credential.default.credentialOn</li>
     * <li>credential.default.numberOfRetries</li>
     * <li>credential.drive.projectId</li>
     * <li>credential.drive.clientId</li>
     * <li>..</li>
     * </ul>
     *
     * @param defaults map of credential
     */
    public GoogleApiCredential( Properties defaults )
    {
        super( defaults );
        this.prefix = DEFAULT_CREDENTIAL_PREFIX + ".";
    }

    /**
     * Sets the Google Cloud Project ID also known as applicationId (AppId).
     *
     * @param projectId the application ID
     * @return this instance to chain
     */
    public GoogleApiCredential setProjectId( String projectId )
    {
        if ( !Strings.isNullOrEmpty( projectId ) )
        {
            setProperty( CREDENTIAL_ATTR + prefix + PROPERTY_PROJECT_ID, projectId );
        }
        return this;
    }

    /**
     * Sets the Google API OAuth 2.0 Client ID Credential.
     *
     * @param clientId the Client ID
     * @return this instance to chain
     */
    public GoogleApiCredential setClientId( String clientId )
    {
        if ( !Strings.isNullOrEmpty( clientId ) )
        {
            setProperty( CREDENTIAL_ATTR + prefix + PROPERTY_CLIENT_ID, clientId );
        }
        return this;
    }

    /**
     * Sets the service account ID (typically an e-mail address).
     *
     * @param serviceEmail the service email
     * @return this instance to chain
     */
    public GoogleApiCredential setServiceAccountEmail( String serviceEmail )
    {
        if ( !Strings.isNullOrEmpty( serviceEmail ) )
        {
            setProperty( CREDENTIAL_ATTR + prefix + PROPERTY_SERVICE_ACCOUNT_EMAIL, serviceEmail );
        }
        return this;
    }

    /**
     * Sets the name of the client application. If the application name is {@code null} or blank,
     * the application will use default name {@link org.ctoolkit.restapi.client.adapter.Constants#DEFAULT_APP_NAME}.
     *
     * @param applicationName the application name to be used as caller name
     * @return this instance to chain
     */
    public GoogleApiCredential setApplicationName( String applicationName )
    {
        if ( !Strings.isNullOrEmpty( applicationName ) )
        {
            setProperty( CREDENTIAL_ATTR + prefix + PROPERTY_APPLICATION_NAME, applicationName );
        }
        else
        {
            setProperty( CREDENTIAL_ATTR + prefix + PROPERTY_APPLICATION_NAME, Constants.DEFAULT_APP_NAME );
        }
        return this;
    }

    /**
     * Sets the path name to the private key file.
     * <p/>
     * Use package relative path for example '/biz/turnonline/server/impl/PrivateKeyFile.p12'
     * or locate the private key file in resource package org.ctoolkit.restapi.client.googleapis
     * directory -> 'PrivateKeyFile.p12'.
     *
     * @param fileName the relative path to file
     * @return this instance to chain
     */
    public GoogleApiCredential setFileName( String fileName )
    {
        if ( !Strings.isNullOrEmpty( fileName ) )
        {
            setProperty( CREDENTIAL_ATTR + prefix + PROPERTY_FILE_NAME, fileName );
        }
        return this;
    }

    /**
     * Sets the API Key to be used with any API that supports it.
     *
     * @param apiKey the API Key to be set
     * @return this instance to chain
     */
    public GoogleApiCredential setApiKey( String apiKey )
    {
        if ( !Strings.isNullOrEmpty( apiKey ) )
        {
            setProperty( CREDENTIAL_ATTR + prefix + PROPERTY_API_KEY, apiKey );
        }
        return this;
    }

    /**
     * Sets the backend service API endpoint URL to be called.
     *
     * @param endpointUrl the endpoint URL to be set
     * @return this instance to chain
     */
    public GoogleApiCredential setEndpointUrl( String endpointUrl )
    {
        if ( !Strings.isNullOrEmpty( endpointUrl ) )
        {
            setProperty( CREDENTIAL_ATTR + prefix + PROPERTY_ENDPOINT_URL, endpointUrl );
        }
        return this;
    }

    /**
     * Sets the boolean identification whether current environment should use these credential
     * in order to authenticate client calls or use cloud native environment for authentication.
     *
     * @param credentialOn true use these credential in order to authenticate calls
     * @return this instance to chain
     */
    public GoogleApiCredential setCredentialOn( boolean credentialOn )
    {
        String valueOf = String.valueOf( credentialOn );
        setProperty( CREDENTIAL_ATTR + prefix + PROPERTY_CREDENTIAL_ON, valueOf );
        return this;
    }

    /**
     * Sets the number of retries that will be allowed to execute before the request will be
     * terminated or {@code 0} to not retry requests.
     * <p>
     * The default value is {@code 1}.
     * </p>
     *
     * @param numberOfRetries the number of retries
     * @return this instance to chain
     * @see HttpRequest#setNumberOfRetries(int)
     */
    public GoogleApiCredential setNumberOfRetries( int numberOfRetries )
    {
        if ( numberOfRetries < 0 )
        {
            numberOfRetries = 1;
        }
        setProperty( CREDENTIAL_ATTR + prefix + PROPERTY_NUMBER_OF_RETRIES, String.valueOf( numberOfRetries ) );
        return this;
    }
}
