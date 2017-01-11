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

import com.google.common.eventbus.EventBus;
import mockit.Injectable;
import mockit.Tested;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.MissingResourceException;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests to test {@link GoogleApiProxyFactory}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GoogleApiProxyFactoryTest
{
    @Tested
    private GoogleApiProxyFactory tested;

    @Injectable
    private EventBus eventBus;

    @Injectable
    private Map<String, String> credential;

    @Test( expectedExceptions = MissingResourceException.class )
    public void getProjectIdMissingConfig()
    {
        tested.getProjectId( null );
    }

    @Test( expectedExceptions = MissingResourceException.class )
    public void getClientIdMissingConfig() throws Exception
    {
        tested.getClientId( null );
    }

    @Test( expectedExceptions = MissingResourceException.class )
    public void getServiceAccountEmailMissingConfig()
    {
        tested.getServiceAccountEmail( null );
    }

    @Test( expectedExceptions = MissingResourceException.class )
    public void getApplicationNameMissingConfig()
    {
        tested.getApplicationName( null );
    }

    @Test( expectedExceptions = MissingResourceException.class )
    public void getFileNameMissingConfig()
    {
        tested.getFileName( null );
    }

    @Test( expectedExceptions = MissingResourceException.class )
    public void getApiKeyMissingConfig()
    {
        tested.getApiKey( null );
    }

    @Test( expectedExceptions = MissingResourceException.class )
    public void getEndpointUrlMissingConfig()
    {
        tested.getEndpointUrl( null );
    }

    @Test
    public void getNumberOfRetriesMissingConfig()
    {
        int numberOfRetries = tested.getNumberOfRetries( null );
        assertEquals( numberOfRetries, 1 );
    }

    @Test
    public void getReadTimeoutMissingConfig()
    {
        int numberOfRetries = tested.getReadTimeout( null );
        assertEquals( numberOfRetries, 20000 );
    }

    @Test
    public void isCredentialOnMissingConfig()
    {
        boolean credentialOn = tested.isCredentialOn( null );
        assertEquals( credentialOn, true );
    }
}