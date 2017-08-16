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

package org.ctoolkit.restapi.client.identity;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import org.ctoolkit.restapi.client.TokenVerifier;
import org.ctoolkit.restapi.client.UnauthorizedException;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * The helper class to wrap identity token verification in to a standalone class.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
public class IdentityTokenResolver
{
    private final TokenVerifier<Identity> tokenVerifier;

    @Inject
    public IdentityTokenResolver( TokenVerifier<Identity> tokenVerifier )
    {

        this.tokenVerifier = tokenVerifier;
    }

    /**
     * Parse identity token taken from given request. It's looking for {@link Identity#GTOKEN} cookie to parse
     * and verify in order to create instance of the {@link Identity}. If fails throws {@link UnauthorizedException}.
     *
     * @param httpRequest the current HTTP servlet request
     * @return the parsed token as {@link Identity} instance
     * @throws UnauthorizedException thrown in case of missing, invalid, or expired token
     */
    public Identity verifyOrThrow( HttpServletRequest httpRequest )
    {
        String token = getToken( httpRequest );
        if ( Strings.isNullOrEmpty( token ) )
        {
            throw new UnauthorizedException( "No authorization token has found in the request!" );
        }

        return internalVerifyAndGet( token, true );
    }

    /**
     * Parse identity token taken from given request. It's looking for {@link Identity#GTOKEN} cookie to parse
     * and verify in order to create instance of the {@link Identity}.
     * If fails for whatever reason returns <tt>null</tt>.
     *
     * @param httpRequest the current HTTP servlet request
     * @return the parsed token as {@link Identity} instance or <tt>null</tt>
     */
    public Identity verifyAndGet( HttpServletRequest httpRequest )
    {
        Identity identity;
        try
        {
            String token = getToken( httpRequest );
            identity = internalVerifyAndGet( token, false );
        }
        catch ( Exception e )
        {
            identity = null;
        }
        return identity;
    }

    private Identity internalVerifyAndGet( String token, boolean throwIfExpired )
    {
        if ( !Strings.isNullOrEmpty( token ) )
        {
            Identity json = tokenVerifier.verifyAndGet( token );

            if ( json.getExpiration().after( new Date() ) )
            {
                return json;
            }
            else
            {
                if ( throwIfExpired )
                {
                    throw new UnauthorizedException( "The given token has expired!" );
                }
            }
        }

        return null;
    }

    private String getToken( HttpServletRequest httpRequest )
    {
        Cookie[] cookies = httpRequest.getCookies();

        if ( cookies == null )
        {
            return httpRequest.getHeader( HttpHeaders.AUTHORIZATION );
        }

        String token = null;

        for ( Cookie cookie : cookies )
        {
            if ( Identity.GTOKEN.equals( cookie.getName() ) )
            {
                token = cookie.getValue();
                break;
            }
        }

        if ( token == null )
        {
            return httpRequest.getHeader( HttpHeaders.AUTHORIZATION );
        }

        return token;
    }

    /**
     * Verifies the given token and for positive verification returns an identity instance.
     * Otherwise returns <tt>null</tt>.
     * In case of {@link UnauthorizedException} it will be catched and converted to <tt>null</tt> return value.
     *
     * @param token the token to be verified
     * @return the verified and parsed identity instance or <tt>null</tt> if fails
     */
    public Identity verifyAndGet( @Nullable String token )
    {
        if ( !Strings.isNullOrEmpty( token ) )
        {
            Identity json;
            try
            {
                json = tokenVerifier.verifyAndGet( token );
            }
            catch ( UnauthorizedException e )
            {
                return null;
            }

            if ( json.getExpiration().after( new Date() ) )
            {
                return json;
            }
        }

        return null;
    }

    /**
     * Delete identity toolkit token cookie.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     */
    public void delete( HttpServletRequest request, HttpServletResponse response )
    {
        Cookie[] cookies = request.getCookies();

        if ( cookies == null )
        {
            return;
        }

        for ( Cookie cookie : cookies )
        {
            if ( Identity.GTOKEN.equals( cookie.getName() ) )
            {
                //the zero value causes the cookie to be deleted
                cookie.setMaxAge( 0 );
                cookie.setValue( "" );
                cookie.setPath( "/" );

                response.addCookie( cookie );
            }
        }
    }
}
