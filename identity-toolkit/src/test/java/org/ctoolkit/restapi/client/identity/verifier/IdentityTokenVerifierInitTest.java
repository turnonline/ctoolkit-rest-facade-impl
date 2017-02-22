package org.ctoolkit.restapi.client.identity.verifier;

import com.google.gson.JsonObject;
import net.oauth.jsontoken.Checker;
import org.ctoolkit.restapi.client.TokenVerifier;
import org.ctoolkit.restapi.client.identity.GuiceTestCase;
import org.ctoolkit.restapi.client.identity.Identity;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.security.SignatureException;

import static org.testng.AssertJUnit.assertNotNull;

/**
 * Identity token verifier, guice initialization test incl. configuration of own audience checker implementation.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class IdentityTokenVerifierInitTest
        extends GuiceTestCase
{
    @Inject
    private TokenVerifier<Identity> verifier;

    @Test
    public void verifyAndGet()
    {
        // Always pass, as guice will fail if there is missing binding for this injection
        assertNotNull( verifier );
    }

    @Override
    public void configureTestBinder()
    {
        super.configureTestBinder();

        bind( Checker.class ).to( AudienceChecker.class );
        install( new IdentityVerifierModule() );
    }

    private static class AudienceChecker
            implements Checker
    {
        private AudienceChecker()
        {
        }

        @Override
        public void check( JsonObject payload ) throws SignatureException
        {
        }
    }
}