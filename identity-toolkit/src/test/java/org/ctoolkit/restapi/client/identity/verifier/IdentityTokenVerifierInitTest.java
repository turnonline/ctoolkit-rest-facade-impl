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