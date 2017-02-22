package org.ctoolkit.restapi.client.identity.verifier;

import com.google.identitytoolkit.GitkitVerifierManager;
import com.google.identitytoolkit.JsonTokenHelper;
import com.google.identitytoolkit.RpcHelper;
import net.oauth.jsontoken.Checker;
import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.JsonTokenParser;
import net.oauth.jsontoken.crypto.SignatureAlgorithm;
import net.oauth.jsontoken.discovery.VerifierProviders;

import java.security.SignatureException;

/**
 * The specific implementation replacing {@link JsonTokenHelper} in order to add constructor with possibility
 * to provide own implementation of the audience {@link Checker}.
 * <p>
 * From the functionality point of view the constructor {@link JsonTokenHelper#JsonTokenHelper(RpcHelper, String...)}
 * is same as {@link #GtokenVerifier(RpcHelper, String...)}
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 * @see JsonTokenHelper
 */
class GtokenVerifier
{
    private final JsonTokenParser parser;

    GtokenVerifier( RpcHelper rpcHelper, String... audiences )
    {
        this( rpcHelper, new JsonTokenHelper.AudienceChecker( audiences ) );
    }

    GtokenVerifier( RpcHelper rpcHelper, Checker checker )
    {
        VerifierProviders verifierProviders = new VerifierProviders();
        verifierProviders.setVerifierProvider( SignatureAlgorithm.RS256, new GitkitVerifierManager( rpcHelper ) );
        parser = new JsonTokenParser( verifierProviders, checker );
    }

    JsonToken verifyAndDeserialize( String token ) throws SignatureException
    {
        return parser.verifyAndDeserialize( token );
    }
}
