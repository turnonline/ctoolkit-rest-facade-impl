/*
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ctoolkit.restapi.client.identity;

import com.google.common.base.Optional;
import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitClientException;
import com.google.identitytoolkit.GitkitServerException;
import com.google.identitytoolkit.RpcHelper;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * The origin class {@link GitkitClient} rewritten in order to work on AppEngine.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
public class IdentityToolkitClient
{
    private final RpcHelper rpcHelper;

    @Inject
    public IdentityToolkitClient( RpcHelper rpcHelper )
    {
        this.rpcHelper = rpcHelper;
    }

    /**
     * Gets out-of-band response. Used by oob endpoint for ResetPassword and ChangeEmail operation.
     * The web site needs to send user an email containing the oobUrl in the response. The user needs
     * to click the oobUrl to finish the operation.
     *
     * @param request http request for the oob endpoint
     * @return the oob response.
     * @throws GitkitServerException
     */
    public OobResponse getOobResponse( HttpServletRequest request, String widgetUrl )
            throws GitkitServerException
    {
        String gitkitToken = lookupCookie( request, Identity.GTOKEN );
        return getOobResponse( request, gitkitToken, widgetUrl );
    }

    /**
     * Gets out-of-band response. Used by oob endpoint for ResetPassword and ChangeEmail operation.
     * The web site needs to send user an email containing the oobUrl in the response. The user needs
     * to click the oobUrl to finish the operation.
     *
     * @param req         http request for the oob endpoint
     * @param gitkitToken Gitkit token of authenticated user, required for ChangeEmail operation
     * @return the oob response.
     * @throws GitkitServerException
     */
    public OobResponse getOobResponse( HttpServletRequest req, String gitkitToken, String widgetUrl )
            throws GitkitServerException
    {
        try
        {
            String action = req.getParameter( "action" );
            if ( "resetPassword".equals( action ) )
            {
                String oobLink = buildOobLink( buildPasswordResetRequest( req ), widgetUrl, action );
                return new OobResponse(
                        req.getParameter( "email" ),
                        null,
                        oobLink,
                        GitkitClient.OobAction.RESET_PASSWORD );
            }
            else if ( "changeEmail".equals( action ) )
            {
                if ( gitkitToken == null )
                {
                    return new OobResponse( "login is required" );
                }
                else
                {
                    String oobLink = buildOobLink( buildChangeEmailRequest( req, gitkitToken ), widgetUrl, action );
                    return new OobResponse(
                            req.getParameter( "oldEmail" ),
                            req.getParameter( "newEmail" ),
                            oobLink,
                            GitkitClient.OobAction.CHANGE_EMAIL );
                }
            }
            else
            {
                return new OobResponse( "unknown request" );
            }
        }
        catch ( GitkitClientException e )
        {
            return new OobResponse( e.getMessage() );
        }
    }

    private String lookupCookie( HttpServletRequest request, String cookieName )
    {
        Cookie[] cookies = request.getCookies();
        if ( cookies == null )
        {
            return null;
        }
        for ( Cookie cookie : cookies )
        {
            if ( cookieName.equals( cookie.getName() ) )
            {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String buildOobLink( JSONObject oobReq, String widgetUrl, String modeParam )
            throws GitkitClientException, GitkitServerException
    {
        JSONObject result = null;
        try
        {
            result = rpcHelper.getOobCode( oobReq );
            String code = result.getString( "oobCode" );
            return widgetUrl + "?mode=" + modeParam + "&oobCode="
                    + URLEncoder.encode( code, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            // should never happen
            throw new GitkitServerException( e );
        }
        catch ( JSONException e )
        {
            throw new GitkitServerException( e.getMessage()
                    + " Response is " + ( result != null ? result.toString() : "(null)" ) );
        }
    }

    private JSONObject buildPasswordResetRequest( HttpServletRequest req ) throws JSONException
    {
        return new JSONObject()
                .put( "email", req.getParameter( "email" ) )
                .put( "userIp", req.getRemoteAddr() )
                .put( "challenge", req.getParameter( "challenge" ) )
                .put( "captchaResp", req.getParameter( "response" ) )
                .put( "requestType", "PASSWORD_RESET" );
    }

    private JSONObject buildChangeEmailRequest( HttpServletRequest req, String gitkitToken )
            throws JSONException
    {
        return new JSONObject()
                .put( "email", req.getParameter( "oldEmail" ) )
                .put( "userIp", req.getRemoteAddr() )
                .put( "newEmail", req.getParameter( "newEmail" ) )
                .put( "idToken", gitkitToken )
                .put( "requestType", "NEW_EMAIL_ACCEPT" );
    }

    private JSONObject buildEmailVerificationRequest( String email )
            throws JSONException
    {
        return new JSONObject()
                .put( "email", email )
                .put( "requestType", "VERIFY_EMAIL" );
    }

    public class OobResponse
    {
        private static final String SUCCESS_RESPONSE = "{\"success\": true}";

        private static final String ERROR_PREFIX = "{\"error\": \"";

        private final String email;

        private final String newEmail;

        private final Optional<String> oobUrl;

        private final GitkitClient.OobAction oobAction;

        private final String responseBody;

        private final String recipient;

        public OobResponse( String responseBody )
        {
            this( null, null, Optional.<String>absent(), null, ERROR_PREFIX + responseBody + "\" }" );
        }

        public OobResponse( String email, String newEmail, String oobUrl, GitkitClient.OobAction oobAction )
        {
            this( email, newEmail, Optional.of( oobUrl ), oobAction, SUCCESS_RESPONSE );
        }

        public OobResponse( String email, String newEmail, Optional<String> oobUrl, GitkitClient.OobAction oobAction,
                            String responseBody )
        {
            this.email = email;
            this.newEmail = newEmail;
            this.oobUrl = oobUrl;
            this.oobAction = oobAction;
            this.responseBody = responseBody;
            this.recipient = newEmail == null ? email : newEmail;
        }

        public Optional<String> getOobUrl()
        {
            return oobUrl;
        }

        public GitkitClient.OobAction getOobAction()
        {
            return oobAction;
        }

        public String getResponseBody()
        {
            return responseBody;
        }

        public String getEmail()
        {
            return email;
        }

        public String getNewEmail()
        {
            return newEmail;
        }

        public String getRecipient()
        {
            return recipient;
        }
    }
}
