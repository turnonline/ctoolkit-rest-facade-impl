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

package org.ctoolkit.restapi.client.firebase;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.ctoolkit.restapi.client.TokenVerifier;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Unit tests to test {@link IdentityTokenResolver}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class IdentityTokenResolverTest
{
    private static final String TOKEN = "tokenvalue";

    @Tested
    private IdentityTokenResolver tested;

    @Injectable
    private TokenVerifier<Identity> tokenVerifier;

    @Mocked
    private Identity identity;

    @Mocked
    private HttpServletRequest request;

    @Mocked
    private HttpServletResponse response;

    @Mocked
    private Cookie cookie;

    @Test( expectedExceptions = UnauthorizedException.class )
    public void verifyOrThrowTokenNotFound()
    {
        tested.verifyOrThrow( request );
    }

    @Test( expectedExceptions = UnauthorizedException.class )
    public void verifyOrThrowTokenExpired()
    {
        getTokenExpectations();

        new Expectations()
        {
            {
                tokenVerifier.verifyAndGet( TOKEN );
                result = identity;

                //noinspection ConstantConditions
                identity.getExpiration().after( ( Date ) any );
                result = false;
            }
        };

        tested.verifyOrThrow( request );
    }

    @Test( expectedExceptions = UnauthorizedException.class )
    public void verifyOrThrowNotValid()
    {
        getTokenExpectations();

        new Expectations()
        {
            {
                tokenVerifier.verifyAndGet( anyString );
                result = new UnauthorizedException();
            }
        };

        tested.verifyOrThrow( request );
    }

    @Test
    public void verifyOrThrow()
    {
        getTokenExpectations();

        new Expectations()
        {
            {
                tokenVerifier.verifyAndGet( TOKEN );
                result = identity;

                //noinspection ConstantConditions
                identity.getExpiration().after( ( Date ) any );
                result = true;
            }
        };

        Identity result = tested.verifyOrThrow( request );
        assertEquals( result, identity );
    }

    @Test
    public void verifyAndGetTokenNotFound()
    {
        assertNull( tested.verifyAndGet( request ) );
    }

    @Test
    public void verifyAndGetTokenExpired()
    {
        getTokenExpectations();

        new Expectations()
        {
            {
                tokenVerifier.verifyAndGet( TOKEN );
                result = identity;

                //noinspection ConstantConditions
                identity.getExpiration().after( ( Date ) any );
                result = false;
            }
        };

        assertNull( tested.verifyAndGet( request ) );
    }

    @Test
    public void verifyAndGetNotValid()
    {
        getTokenExpectations();

        new Expectations()
        {
            {
                tokenVerifier.verifyAndGet( anyString );
                result = new UnauthorizedException();
            }
        };

        assertNull( tested.verifyAndGet( request ) );
    }

    @Test
    public void verifyAndGet()
    {
        getTokenExpectations();

        new Expectations()
        {
            {
                tokenVerifier.verifyAndGet( TOKEN );
                result = identity;

                //noinspection ConstantConditions
                identity.getExpiration().after( ( Date ) any );
                result = true;
            }
        };

        Identity result = tested.verifyAndGet( request );
        assertEquals( result, identity );
    }

    @Test
    public void verifyAndGetInputTokenNull()
    {
        new Expectations()
        {
            {
                tokenVerifier.verifyAndGet( TOKEN );
                result = identity;
                times = 0;
            }
        };

        assertNull( tested.verifyAndGet( ( String ) null ) );
    }

    @Test
    public void verifyAndGetInputTokenExpired()
    {
        new Expectations()
        {
            {
                tokenVerifier.verifyAndGet( TOKEN );
                result = identity;

                //noinspection ConstantConditions
                identity.getExpiration().after( ( Date ) any );
                result = false;
            }
        };

        assertNull( tested.verifyAndGet( TOKEN ) );
    }

    @Test
    public void verifyAndGetInputTokenNotValid()
    {
        new Expectations()
        {
            {
                tokenVerifier.verifyAndGet( TOKEN );
                result = new UnauthorizedException();
            }
        };

        assertNull( tested.verifyAndGet( TOKEN ) );
    }

    @Test
    public void verifyAndGetInputToken()
    {
        new Expectations()
        {
            {
                tokenVerifier.verifyAndGet( TOKEN );
                result = identity;

                //noinspection ConstantConditions
                identity.getExpiration().after( ( Date ) any );
                result = true;
            }
        };

        Identity result = tested.verifyAndGet( TOKEN );
        assertEquals( result, identity );
    }

    @Test
    public void deleteNoCookie()
    {
        new Expectations()
        {
            {
                request.getCookies();
                result = null;
            }
        };

        tested.delete( request, response );
    }

    @Test
    public void delete()
    {
        getTokenExpectations();

        tested.delete( request, response );

        new Verifications()
        {
            {
                cookie.setMaxAge( 0 );
                times = 1;

                cookie.setValue( "" );
                times = 1;

                cookie.setPath( "/" );
                times = 1;

                response.addCookie( cookie );
                times = 1;
            }
        };
    }

    private void getTokenExpectations()
    {
        new Expectations()
        {
            {
                request.getCookies();
                result = new Cookie[]{cookie};
                minTimes = 0;

                cookie.getName();
                result = Identity.GTOKEN;
                minTimes = 0;

                cookie.getValue();
                result = TOKEN;
                minTimes = 0;
            }
        };
    }
}