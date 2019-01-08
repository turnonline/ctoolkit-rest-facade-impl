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

package org.ctoolkit.restapi.client.pubsub;

import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Charsets;
import com.google.api.services.pubsub.model.PubsubMessage;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;

/**
 * The {@link PubsubMessage} listener interface.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public interface PubsubMessageListener
        extends Serializable
{
    String PUB_SUB_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * Called once a message has been received for given subscription.
     * In case of message processing failure it throws exception to signal re-try.
     * The implementation must be idempotent to correctly process on re-try.
     *
     * @param message      the published message to be processed
     * @param subscription the subscription that received push message
     * @throws Exception the exception thrown in case of failure
     */
    void onMessage( @Nonnull PubsubMessage message, @Nonnull String subscription )
            throws Exception;

    /**
     * Encode Base64 based string. If not encoded, value will be returned as been provided.
     *
     * @param data optionally encoded string value
     * @return the decoded data
     */
    default String decode( String data )
    {
        String decoded;
        if ( Base64.isBase64( data.getBytes() ) )
        {
            decoded = new String( new PubsubMessage().setData( data ).decodeData(), Charsets.UTF_8 );
        }
        else
        {
            decoded = data;
        }
        return decoded;
    }

    /**
     * Parses an encoded string value as a JSON object, array, or value into a new instance of the given
     * destination class using {@link JsonParser#parse(Class)}.
     *
     * @param content          encoded (might be Base64) JSON string value
     * @param destinationClass destination class that has an accessible default constructor to use to
     *                         create a new instance
     * @return the new instance of the parsed destination class
     */
    default <T> T fromString( String content, Class<T> destinationClass )
            throws IOException
    {
        String decoded = decode( content );
        return JacksonFactory.getDefaultInstance().fromString( decoded, destinationClass );
    }
}
