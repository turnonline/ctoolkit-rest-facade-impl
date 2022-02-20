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

package org.ctoolkit.restapi.client.pubsub;

import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Charsets;
import com.google.api.client.util.DateTime;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The common Pub / Sub command.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class PubsubCommand
        implements Serializable
{
    public static final String PUB_SUB_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    /**
     * The entity identification, unique only for same data type.
     */
    public static final String ENTITY_ID = "Entity_ID";

    /**
     * The attribute that signals entity deletion of the data type and its identification.
     */
    public static final String ENTITY_DELETION = "DataDeletion";

    /**
     * The entity unique key as a composition of its ID and optional parent IDs.
     * IDs separated by '/'.
     */
    public static final String ENCODED_UNIQUE_KEY = "EncodedUnique_Key";

    /**
     * The concrete name of the entity data type (kind).
     */
    public static final String DATA_TYPE = "DataType";

    /**
     * The account login email.
     */
    public static final String ACCOUNT_EMAIL = "AccountEmail";

    /**
     * The account audience unique identification.
     */
    public static final String ACCOUNT_AUDIENCE = "AccountAudience";

    /**
     * The user account unique identification within login provider system.
     */
    public static final String ACCOUNT_IDENTITY_ID = "AccountIdentity_ID";

    /**
     * The account unique Long type identification.
     */
    public static final String ACCOUNT_UNIQUE_ID = "AccountUnique_ID";

    /**
     * The attribute to signal an account is a new account sign-up.
     * This attribute with value TRUE will come only once per an account lifetime.
     */
    public static final String ACCOUNT_SIGN_UP = "NewAccountSign-Up";

    /**
     * The HTTP {@code Accept-Language} attribute.
     */
    public static final String ACCEPT_LANGUAGE = HttpHeaders.ACCEPT_LANGUAGE;

    private static final Logger LOGGER = LoggerFactory.getLogger( PubsubCommand.class );

    private static final long serialVersionUID = 2023063093067483562L;

    private final Map<String, String> attributes;

    private final String data;

    private final String messageId;

    private final String publishTime;

    public PubsubCommand( @Nonnull PubsubMessage message )
    {
        this( message.getAttributes(), message.getPublishTime(), message.getData(), message.getMessageId() );
    }

    public PubsubCommand( @Nullable Map<String, String> attributes,
                          @Nullable String publishTime,
                          @Nullable String data,
                          @Nullable String messageId )
    {
        this.attributes = attributes == null ? new HashMap<>() : attributes;
        this.publishTime = publishTime;
        this.data = data;
        this.messageId = messageId;
    }

    /**
     * Validates whether all of the specified attributes are being present.
     *
     * @param mandatory the expected attributes
     * @return returns true, if all specified mandatory attributes are present in the current message
     */
    public boolean validate( @Nonnull String... mandatory )
    {
        checkNotNull( mandatory );

        if ( mandatory.length == 0 )
        {
            // there is nothing to validate
            return true;
        }

        for ( String next : mandatory )
        {
            if ( Strings.isNullOrEmpty( next ) )
            {
                LOGGER.warn( "Any of the attribute cannot be null or empty! " + Arrays.toString( mandatory ) );
                return false;
            }
        }

        if ( attributes.isEmpty() )
        {
            LOGGER.warn( "No incoming attributes" );
            return false;
        }

        List<String> missing = null;
        for ( String next : mandatory )
        {
            String value = attributes.get( next );
            if ( Strings.isNullOrEmpty( value ) )
            {
                if ( missing == null )
                {
                    missing = new ArrayList<>();
                }
                missing.add( next );
            }
        }

        if ( missing != null && !missing.isEmpty() )
        {
            LOGGER.warn( "These attributes are mandatory but missing: " + missing );
            return false;
        }

        return true;
    }

    /**
     * Returns decoded (Base64) message data.
     * If this field is empty, the message must contain at least one attribute.
     *
     * @return the decoded data
     * @see #decode(String)
     */
    public String getData()
    {
        return data == null ? null : decode( data );
    }

    /**
     * Decode Base64 based string. If not encoded, value will be returned as been provided.
     *
     * @param data optionally encoded string value
     * @return the decoded data
     */
    public static String decode( @Nonnull String data )
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
     * @param content     encoded (might be Base64) JSON string value
     * @param destination destination class that has an accessible default constructor to use to
     *                    create a new instance
     * @return the new instance of the parsed destination class
     */
    public static <T> T fromString( @Nonnull String content, @Nonnull Class<T> destination )
            throws IOException
    {
        checkNotNull( destination, "Destination class to de-serialize JSON to, can't be null" );
        String decoded = decode( content );
        return JacksonFactory.getDefaultInstance().fromString( decoded, destination );
    }

    /**
     * Parses JSON object (array, or value) taken from {@link #getData()}, into a new instance of the given
     * destination class using {@link JsonParser#parse(Class)}.
     *
     * @param destination destination class that has an accessible default constructor to use to
     *                    create a new instance
     * @param <T>         the type of the class to be JSON data de-serialized to
     * @return the new instance of the parsed destination class
     */
    public <T> T fromData( @Nonnull Class<T> destination )
            throws IOException
    {
        return data == null ? null : fromString( data, destination );
    }

    /**
     * Returns the locale configured by the sender, or {@code null} if not provided.
     *
     * @return the Accept-Language locale, or {@code null} if not provided
     * @see HttpHeaders#ACCEPT_LANGUAGE
     */
    public String getAcceptLanguage()
    {
        return attributes.get( ACCEPT_LANGUAGE );
    }

    /**
     * Returns the entity identification, unique only for same data type.
     *
     * @return the entity identification
     */
    public String getEntityId()
    {
        return attributes.get( ENTITY_ID );
    }

    /**
     * Returns the entity identification as type of {@link Long}, unique only for same data type.
     *
     * @return the entity identification as long
     * @see #getEntityId()
     */
    public Long getEntityLongId()
    {
        String entityId = getEntityId();
        if ( entityId == null )
        {
            return null;
        }
        return Long.valueOf( entityId );
    }

    /**
     * Returns the entity unique key as a list of IDs.
     *
     * @return the list of IDs
     */
    public List<String> getUniqueKey()
    {
        String value = attributes.get( ENCODED_UNIQUE_KEY );
        if ( Strings.isNullOrEmpty( value ) )
        {
            return new ArrayList<>();
        }
        return Splitter.on( '/' ).trimResults().omitEmptyStrings().splitToList( value );
    }

    /**
     * Returns the single ID taken from the unique key.
     *
     * @param index the index of the ID within unique key
     * @return the specified ID
     * @see #getUniqueKey()
     */
    public String idFromKey( int index )
    {
        List<String> uniqueKey = getUniqueKey();
        return uniqueKey.get( index );
    }

    /**
     * Returns the single ID taken from the unique key and converted to {@link Long}.
     *
     * @param index the index of the ID within unique key
     * @return the specified long ID
     * @see #getUniqueKey()
     */
    public Long idFromKeyLong( int index )
    {
        List<String> uniqueKey = getUniqueKey();
        String id = uniqueKey.get( index );
        return id == null ? null : Long.valueOf( id );
    }

    /**
     * Returns the concrete name of the entity data type (kind).
     *
     * @return the entity data type
     */
    public String getDataType()
    {
        return attributes.get( DATA_TYPE );
    }

    /**
     * Returns the account login email.
     *
     * @return the account login email
     */
    public String getAccountEmail()
    {
        return attributes.get( ACCOUNT_EMAIL );
    }

    /**
     * Returns the account audience unique identification.
     *
     * @return the account audience
     */
    public String getAccountAudience()
    {
        return attributes.get( ACCOUNT_AUDIENCE );
    }

    /**
     * Returns the user account unique identification within login provider system.
     *
     * @return the user account identity ID
     */
    public String getAccountIdentityId()
    {
        return attributes.get( ACCOUNT_IDENTITY_ID );
    }

    /**
     * Returns the account unique identification.
     *
     * @return the account unique ID
     */
    public Long getAccountId()
    {
        String id = attributes.get( ACCOUNT_UNIQUE_ID );
        if ( id == null )
        {
            return null;
        }
        return Long.valueOf( id );
    }

    /**
     * Returns the boolean indication whether account represents a new account sign-up.
     *
     * @return true if account is a new account sign-up
     */
    public boolean isAccountSignUp()
    {
        return Boolean.parseBoolean( attributes.get( ACCOUNT_SIGN_UP ) );
    }

    /**
     * Returns the boolean indication whether event represents an entity deletion.
     * Identified by data type and its identification.
     *
     * @return true if it signals an entity deletion
     */
    public boolean isDelete()
    {
        return Boolean.parseBoolean( attributes.get( ENTITY_DELETION ) );
    }

    /**
     * ID of this message, assigned by the server when the message is published.
     * Guaranteed to be unique within the topic.
     *
     * @return message ID or {@code null} for none
     * @see PubsubMessage#getMessageId()
     */
    public String getMessageId()
    {
        return messageId;
    }

    /**
     * Returns the time at which the message was published, populated by the server
     * when it receives the 'Publish' call.
     *
     * @return the date at which the message was published
     */
    public Date getPublishDate()
    {
        Date parsed = null;
        if ( !Strings.isNullOrEmpty( publishTime ) )
        {
            DateTime dateTime = DateTime.parseRfc3339( publishTime );
            parsed = new Date( dateTime.getValue() );
        }

        return parsed;
    }

    /**
     * Returns the time at which the message was published, populated by the server
     * when it receives the 'Publish' call.
     *
     * @return the date at which the message was published
     */
    public DateTime getPublishDateTime()
    {
        DateTime parsed = null;
        if ( !Strings.isNullOrEmpty( publishTime ) )
        {
            parsed = DateTime.parseRfc3339( publishTime );
        }

        return parsed;
    }
}
