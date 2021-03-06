/*
 * Copyright (c) 2018 Comvai, s.r.o. All Rights Reserved.
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

import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Google Firebase token verification wrapper with convenient methods.
 *
 * @author <a href="mailto:jozef.pohorelec@ctoolkit.org">Jozef Pohorelec</a>
 */
@Singleton
public final class IdentityHandler
{
    /**
     * Default cookie name of the Firebase token.
     */
    public static final String FTOKEN = "ftoken";

    private static final Logger logger = LoggerFactory.getLogger( IdentityHandler.class );

    private final FirebaseAuth firebase;

    @Inject
    IdentityHandler( FirebaseAuth firebase )
    {
        this.firebase = firebase;
    }

    /**
     * Verifies firebase token taken from the request.
     * Once verification is successful returns populated identity instance.
     * If verification fails or token has expired returns <code>null</code>.
     *
     * @param request the HTTP request
     * @return the successfully verified firebase token instance or null
     */
    public FirebaseToken resolveVerifyToken( @Nonnull HttpServletRequest request )
    {
        String token = getToken( checkNotNull( request ) );
        FirebaseToken decodedToken = null;

        if ( !Strings.isNullOrEmpty( token ) )
        {
            try
            {
                decodedToken = firebase.verifyIdTokenAsync( token ).get();
            }
            catch ( InterruptedException | ExecutionException e )
            {
                logger.error( "Token verification has failed.", e );
            }
        }

        return decodedToken;
    }

    /**
     * Returns the firebase token from the request. Searched either in headers (first) or cookies.
     * If not found returns <code>null</code>.
     *
     * @param request the HTTP request
     * @return the firebase token
     */
    public final String getToken( @Nonnull HttpServletRequest request )
    {
        String token = request.getHeader( FTOKEN );
        if ( !Strings.isNullOrEmpty( token ) )
        {
            return token;
        }

        // not found in header thus search in cookies
        Cookie[] cookies = request.getCookies();

        if ( cookies == null )
        {
            return null;
        }

        for ( Cookie cookie : cookies )
        {
            if ( FTOKEN.equals( cookie.getName() ) )
            {
                token = cookie.getValue();
            }
        }
        return token;
    }

    /**
     * Delete firebase token cookie.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     */
    public void delete( @Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response )
    {
        Cookie[] cookies = request.getCookies();

        if ( cookies == null )
        {
            return;
        }

        for ( Cookie cookie : cookies )
        {
            if ( FTOKEN.equals( cookie.getName() ) )
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
