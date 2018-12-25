package org.ctoolkit.restapi.client.pubsub.outside;

import com.google.api.services.pubsub.model.PubsubMessage;
import org.ctoolkit.restapi.client.pubsub.PubsubCommand;
import org.ctoolkit.restapi.client.pubsub.TopicMessage;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ACCOUNT_EMAIL;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ACCOUNT_SIGN_UP;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ACCOUNT_UNIQUE_ID;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.DATA_TYPE;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ENCODED_UNIQUE_KEY;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ENTITY_DELETION;
import static org.ctoolkit.restapi.client.pubsub.PubsubCommand.ENTITY_ID;

/**
 * {@link PubsubCommand} unit testing.
 *
 * @author <a href="mailto:medvegy@turnonline.biz">Aurel Medvegy</a>
 */
public class PubsubCommandTest
{
    private static final Long PRODUCT_ID = 13572468L;

    private static final Long ACCOUNT_ID = 246813579L;

    @Test
    public void validate_NothingValidate()
    {
        PubsubCommand tested = new PubsubCommand( ( Map<String, String> ) null );
        tested.validate();
    }

    @Test( expectedExceptions = NullPointerException.class )
    public void validate_NullOrEmpty()
    {
        PubsubCommand tested = new PubsubCommand( ( Map<String, String> ) null );
        tested.validate( "", null );
    }

    @Test( expectedExceptions = IllegalArgumentException.class )
    public void validate_NoAttributesDefined()
    {
        PubsubCommand tested = new PubsubCommand( ( Map<String, String> ) null );
        tested.validate( DATA_TYPE );
    }

    @Test( expectedExceptions = IllegalArgumentException.class )
    public void validate_MissingAttributes()
    {
        PubsubCommand tested = command( false );
        tested.validate( DATA_TYPE, ENCODED_UNIQUE_KEY );
    }

    @Test
    public void deletion_True()
    {
        PubsubCommand tested = command( true );
        assertThat( tested.isDelete() ).isTrue();
    }

    @Test
    public void deletion_False()
    {
        PubsubCommand tested = command( false );
        assertThat( tested.isDelete() ).isFalse();
    }

    @Test
    public void deletion_Missing()
    {
        PubsubCommand tested = new PubsubCommand( new HashMap<>() );
        assertThat( tested.isDelete() ).isFalse();
    }

    @Test
    public void accountSignUp_True()
    {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put( ACCOUNT_SIGN_UP, String.valueOf( true ) );

        PubsubCommand tested = new PubsubCommand( attributes );
        assertThat( tested.isAccountSignUp() ).isTrue();
    }

    @Test
    public void accountSignUp_False()
    {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put( ACCOUNT_SIGN_UP, String.valueOf( false ) );

        PubsubCommand tested = new PubsubCommand( attributes );
        assertThat( tested.isAccountSignUp() ).isFalse();
    }

    @Test
    public void accountSignUp_Missing()
    {
        PubsubCommand tested = new PubsubCommand( new HashMap<>() );
        assertThat( tested.isAccountSignUp() ).isFalse();
    }

    @Test
    public void getEntityId_StringType()
    {
        PubsubCommand tested = command( false );
        tested.validate( ENTITY_ID );

        assertThat( tested.getEntityId() ).isEqualTo( String.valueOf( PRODUCT_ID ) );
    }

    @Test
    public void getEntityId_LongType()
    {
        PubsubCommand tested = command( false );
        tested.validate( ENTITY_ID );

        assertThat( tested.getEntityLongId() ).isEqualTo( PRODUCT_ID );
    }

    @Test
    public void getEntityLongId_Null()
    {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put( ENCODED_UNIQUE_KEY, "123/456" );
        PubsubCommand tested = new PubsubCommand( attributes );

        assertThat( tested.getEntityLongId() ).isNull();
    }

    @Test
    public void getDataType()
    {
        PubsubCommand tested = command( true );
        tested.validate( DATA_TYPE );

        assertThat( tested.getDataType() ).isEqualTo( "Product" );
    }

    @Test
    public void getAccountEmail()
    {
        PubsubCommand tested = command( true );
        tested.validate( ACCOUNT_EMAIL );

        assertThat( tested.getAccountEmail() ).isEqualTo( "my.account@turnonline.biz" );
    }

    @Test
    public void getAccountId()
    {
        PubsubCommand tested = command( true );
        tested.validate( ACCOUNT_UNIQUE_ID );

        assertThat( tested.getAccountId() ).isEqualTo( ACCOUNT_ID );
    }

    @Test
    public void getAccountId_Null()
    {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put( ENCODED_UNIQUE_KEY, "123/456" );
        PubsubCommand tested = new PubsubCommand( attributes );

        assertThat( tested.getAccountId() ).isNull();
    }

    @Test
    public void getUniqueKey_Empty()
    {
        PubsubCommand tested = command( false );

        List<String> uniqueKey = tested.getUniqueKey();
        assertThat( uniqueKey ).hasSize( 0 );
    }

    @Test
    public void getUniqueKey()
    {
        long id1 = 135L;
        long id2 = 246L;
        long id3 = 357L;
        PubsubCommand tested = command( false, id1, id2, id3 );
        tested.validate( ENCODED_UNIQUE_KEY );

        List<String> uniqueKey = tested.getUniqueKey();
        assertThat( uniqueKey ).hasSize( 3 );
        assertThat( uniqueKey ).contains( String.valueOf( id1 ) );
        assertThat( uniqueKey ).contains( String.valueOf( id2 ) );
        assertThat( uniqueKey ).contains( String.valueOf( id3 ) );
    }

    @Test
    public void idFromKey()
    {
        long id1 = 246L;
        long id2 = 357L;
        PubsubCommand tested = command( false, id1, id2 );
        tested.validate( ENCODED_UNIQUE_KEY );

        assertThat( tested.idFromKey( 0 ) ).isEqualTo( String.valueOf( id1 ) );
        assertThat( tested.idFromKey( 1 ) ).isEqualTo( String.valueOf( id2 ) );
    }

    @Test
    public void idFromKey_ExtraLastSlash()
    {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put( ENCODED_UNIQUE_KEY, "/5678987/4680/1358/" );
        PubsubCommand tested = new PubsubCommand( attributes );

        List<String> uniqueKey = tested.getUniqueKey();
        assertThat( uniqueKey ).hasSize( 3 );

        assertThat( tested.idFromKey( 0 ) ).isEqualTo( "5678987" );
        assertThat( tested.idFromKey( 1 ) ).isEqualTo( "4680" );
        assertThat( tested.idFromKey( 2 ) ).isEqualTo( "1358" );
    }

    @Test
    public void idFromKeyLong()
    {
        long id1 = 246L;
        long id2 = 357L;
        PubsubCommand tested = command( false, id1, id2 );
        tested.validate( ENCODED_UNIQUE_KEY );

        assertThat( tested.idFromKeyLong( 0 ) ).isEqualTo( id1 );
        assertThat( tested.idFromKeyLong( 1 ) ).isEqualTo( id2 );
    }

    @Test
    public void idFromKey_MixedType()
    {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put( ENCODED_UNIQUE_KEY, "/456bn/5678" );
        PubsubCommand tested = new PubsubCommand( attributes );
        tested.validate( ENCODED_UNIQUE_KEY );

        assertThat( tested.idFromKey( 0 ) ).isEqualTo( "456bn" );
        assertThat( tested.idFromKeyLong( 1 ) ).isEqualTo( 5678L );
    }

    @Test
    public void idFromKeyLong_SingleId()
    {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put( ENCODED_UNIQUE_KEY, "5678987" );
        PubsubCommand tested = new PubsubCommand( attributes );
        tested.validate( ENCODED_UNIQUE_KEY );

        assertThat( tested.idFromKeyLong( 0 ) ).isEqualTo( 5678987L );
    }

    @Test
    public void idFromKeyLong_SingleIdWithSlash()
    {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put( ENCODED_UNIQUE_KEY, "/5678987" );
        PubsubCommand tested = new PubsubCommand( attributes );
        tested.validate( ENCODED_UNIQUE_KEY );

        assertThat( tested.idFromKeyLong( 0 ) ).isEqualTo( 5678987L );
    }

    @Test( expectedExceptions = NumberFormatException.class )
    public void idFromKeyLong_Invalid()
    {
        HashMap<String, String> attributes = new HashMap<>();
        attributes.put( ENCODED_UNIQUE_KEY, "123ab/456bn" );
        PubsubCommand tested = new PubsubCommand( attributes );
        tested.validate( ENCODED_UNIQUE_KEY );

        tested.idFromKeyLong( 0 );
    }

    private PubsubCommand command( boolean deletion, long... ids )
    {
        TopicMessage.Builder builder = TopicMessage.newBuilder();
        builder.setProjectId( "projectId" ).setTopicId( "a-topic" )
                .addMessage( "{}", ENTITY_ID, String.valueOf( PRODUCT_ID ) )
                .addAttribute( DATA_TYPE, "Product" )
                .addAttribute( ACCOUNT_EMAIL, "my.account@turnonline.biz" )
                .addAttribute( ACCOUNT_UNIQUE_ID, String.valueOf( ACCOUNT_ID ) )
                .addAttribute( ENTITY_DELETION, String.valueOf( deletion ) );

        if ( ids.length > 0 )
        {
            StringBuilder key = new StringBuilder();
            for ( long id : ids )
            {
                key.append( "/" ).append( id );
            }
            builder.addAttribute( ENCODED_UNIQUE_KEY, key.toString() );
        }

        PubsubMessage message = builder.build().getMessages().get( 0 );
        return new PubsubCommand( message );
    }
}