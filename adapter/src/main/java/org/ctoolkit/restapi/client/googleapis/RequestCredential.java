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

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * The request credential class with convenience methods to access credential and configuration.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class RequestCredential
        extends ApiCredential
{
    private static final String CREDENTIAL_PREFIX = "request-scope";

    private static final long serialVersionUID = 4050136829585508854L;

    public RequestCredential()
    {
        super( CREDENTIAL_PREFIX );
    }

    public RequestCredential( @Nonnull Map<String, String> properties )
    {
        super( properties, CREDENTIAL_PREFIX );
    }

    /**
     * Returns Google Cloud Project ID also known as applicationId (AppId).
     *
     * @return the project ID
     */
    public String getProjectId()
    {
        return getStringValue( PROPERTY_PROJECT_ID );
    }

    /**
     * Returns the Google API OAuth 2.0 Client ID Credential.
     *
     * @return the client ID
     */
    public String getClientId()
    {
        return getStringValue( PROPERTY_CLIENT_ID );
    }

    /**
     * Returns the service account ID (typically an e-mail address).
     *
     * @return the service email
     */
    public String getServiceAccountEmail()
    {
        return getStringValue( PROPERTY_SERVICE_ACCOUNT_EMAIL );
    }

    /**
     * Returns the name of the client application.
     *
     * @return the name of the client application.
     */
    public String getApplicationName()
    {
        return getStringValue( PROPERTY_APPLICATION_NAME );
    }

    /**
     * Returns the path name to the private key file.
     *
     * @return the relative path to file
     */
    public String getFileName()
    {
        return getStringValue( PROPERTY_FILE_NAME );
    }

    /**
     * Returns the API authentication key.
     *
     * @return the API key
     */
    public String getApiKey()
    {
        return getStringValue( PROPERTY_API_KEY );
    }

    /**
     * Returns the backend service API endpoint URL.
     *
     * @return the endpoint URL
     */
    public String getEndpointUrl()
    {
        return getStringValue( PROPERTY_ENDPOINT_URL );
    }

    /**
     * Returns the boolean identification whether current environment should use these credential
     * in order to authenticate client calls or use cloud native environment for authentication.
     *
     * @return the true if credential will be used to authenticate
     */
    public final boolean isCredentialOn()
    {
        String value = getProperty( CREDENTIAL_ATTR + prefix + PROPERTY_CREDENTIAL_ON );
        if ( value == null )
        {
            return false;
        }

        return Boolean.valueOf( value );
    }

    /**
     * Returns the number of retries that will be allowed to execute before the request will be
     * terminated or {@code 0} to not retry requests.
     * <p>
     * The default value is {@code 1}.
     * </p>
     *
     * @return the number of retries
     */
    public int getNumberOfRetries()
    {
        String value = getProperty( CREDENTIAL_ATTR + prefix + PROPERTY_NUMBER_OF_RETRIES );
        if ( value == null )
        {
            value = DEFAULT_NUMBER_OF_RETRIES;
        }
        return Integer.valueOf( value );
    }

    /**
     * Returns the timeout in milliseconds to read data from an established connection or {@code 0} for
     * an infinite timeout.
     * <p>
     * By default it is 20000 (20 seconds).
     * </p>
     *
     * @return the timeout in milliseconds
     */
    public int getRequestReadTimeout()
    {
        String value = getProperty( CREDENTIAL_ATTR + prefix + PROPERTY_READ_TIMEOUT );
        if ( value == null )
        {
            value = DEFAULT_READ_TIMEOUT;
        }
        return Integer.valueOf( value );
    }
}
