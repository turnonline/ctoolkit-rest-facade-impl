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
    public void isCredentialOnMissingConfig()
    {
        boolean credentialOn = tested.isCredentialOn( null );
        assertEquals( credentialOn, true );
    }
}