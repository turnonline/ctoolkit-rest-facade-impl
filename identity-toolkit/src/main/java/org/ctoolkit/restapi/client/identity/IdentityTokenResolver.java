package org.ctoolkit.restapi.client.identity;

import com.google.api.client.repackaged.com.google.common.base.Strings;
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
     * Parse identity token taken from given request. It's looking for {@link Identity#GTOKEN} cookie to parse and verify
     * in order to create instance of the {@link Identity}. If no token found or expired, returns <tt>null</tt>.
     *
     * @param httpRequest the current HTTP servlet request
     * @return the parsed token as {@link Identity} instance
     * @throws UnauthorizedException thrown in case of invalid token
     */
    public Identity resolve( HttpServletRequest httpRequest )
    {
        Cookie[] cookies = httpRequest.getCookies();

        if ( cookies == null )
        {
            return null;
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

        if ( !Strings.isNullOrEmpty( token ) )
        {
            Identity json = tokenVerifier.verifyAndGet( token );

            if ( json.getExpiration().after( new Date() ) )
            {
                return json;
            }
        }

        return null;
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
