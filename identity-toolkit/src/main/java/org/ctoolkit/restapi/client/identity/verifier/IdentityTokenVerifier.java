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

package org.ctoolkit.restapi.client.identity.verifier;

import com.google.gson.JsonObject;
import com.google.identitytoolkit.JsonTokenHelper;
import net.oauth.jsontoken.JsonToken;
import org.ctoolkit.restapi.client.TokenVerifier;
import org.ctoolkit.restapi.client.UnauthorizedException;
import org.ctoolkit.restapi.client.identity.Identity;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * The {@link Identity} token verifier.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class IdentityTokenVerifier
        implements TokenVerifier<Identity>
{
    private static final Logger logger = LoggerFactory.getLogger( IdentityTokenVerifier.class );

    private final GtokenVerifier verifier;

    @Inject
    public IdentityTokenVerifier( GtokenVerifier verifier )
    {
        this.verifier = verifier;
    }

    @Override
    public Identity verifyAndGet( String token )
            throws UnauthorizedException
    {
        JsonToken jsonToken;
        try
        {
            jsonToken = verifier.verifyAndDeserialize( token );
        }
        catch ( Exception e )
        {
            logger.error( "Token: " + token, e );
            throw new UnauthorizedException( e.getMessage() );
        }

        Instant issuedAt = jsonToken.getIssuedAt();
        Instant expiration = jsonToken.getExpiration();
        JsonObject json = jsonToken.getPayloadAsJsonObject();

        // populate Identity
        Identity identity = new Identity();

        identity.setIssuedAt( issuedAt == null ? null : issuedAt.toDate() );
        identity.setExpiration( expiration == null ? null : expiration.toDate() );
        identity.setLocalId( json.get( JsonTokenHelper.ID_TOKEN_USER_ID ).getAsString() );
        identity.setEmail( json.get( JsonTokenHelper.ID_TOKEN_EMAIL ).getAsString() );

        String verified = "verified";
        identity.setEmailVerified( json.has( verified ) && json.get( verified ).getAsBoolean() );

        identity.setDisplayName( json.has( JsonTokenHelper.ID_TOKEN_DISPLAY_NAME )
                ? json.get( JsonTokenHelper.ID_TOKEN_DISPLAY_NAME ).getAsString() : null );

        identity.setProviderId( json.has( JsonTokenHelper.ID_TOKEN_PROVIDER )
                ? json.get( JsonTokenHelper.ID_TOKEN_PROVIDER ).getAsString() : null );

        identity.setPhotoUrl( json.has( JsonTokenHelper.ID_TOKEN_PHOTO_URL )
                ? json.get( JsonTokenHelper.ID_TOKEN_PHOTO_URL ).getAsString() : null );

        return identity;
    }

}
