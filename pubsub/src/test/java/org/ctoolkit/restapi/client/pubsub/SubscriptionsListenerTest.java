package org.ctoolkit.restapi.client.pubsub;

import com.google.api.client.json.JsonParser;
import com.google.api.services.pubsub.model.PubsubMessage;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * {@link SubscriptionsListener} unit testing.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class SubscriptionsListenerTest
{
    @Tested
    private SubscriptionsListener tested;

    @Injectable
    private Map<String, PubsubMessageListener> listeners = new HashMap<>();

    @Mocked
    private HttpServletRequest request;

    @Mocked
    private HttpServletResponse response;

    @Mocked
    private PubsubMessageListener listener;

    @Mocked
    private JsonParser parser;

    @Test
    public void getSubscriptionSuffix()
    {
        new Expectations()
        {
            {
                request.getRequestURI();
                result = SubscriptionsListener.PUSH_HANDLERS_URL_PATH + "content.delete";
            }
        };

        String suffix = tested.getSubscriptionSuffix( request );
        assertEquals( suffix, "content.delete" );
    }

    @Test( expectedExceptions = IllegalArgumentException.class )
    public void getSubscriptionNoSuffix()
    {
        new Expectations()
        {
            {
                request.getRequestURI();
                result = SubscriptionsListener.PUSH_HANDLERS_URL_PATH;
            }
        };

        tested.getSubscriptionSuffix( request );
    }

    @Test( expectedExceptions = IllegalArgumentException.class )
    public void onSubscriptionMessageNoListenerRegistered()
            throws IOException
    {
        new Expectations()
        {
            {
                request.getRequestURI();
                result = SubscriptionsListener.PUSH_HANDLERS_URL_PATH + "content.delete";

                response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            }
        };

        tested.doPost( request, response );
    }

    @Test
    public void onSubscriptionMessage()
            throws Exception
    {
        final String subscription = "content.delete";
        listeners.put( subscription, listener );

        PubsubMessage message = new PubsubMessage();

        new Expectations( tested )
        {
            {
                request.getRequestURI();
                result = SubscriptionsListener.PUSH_HANDLERS_URL_PATH + subscription;

                //noinspection unchecked,ConstantConditions
                parser.parseAndClose( ( Class<PubsubMessage> ) any );
                result = message;
            }
        };

        tested.doPost( request, response );
        // cleanup for rest of the tests
        listeners.clear();

        new Verifications()
        {
            {
                listener.onMessage( message, "content.delete" );
                response.setStatus( HttpServletResponse.SC_NO_CONTENT );
            }
        };
    }
}