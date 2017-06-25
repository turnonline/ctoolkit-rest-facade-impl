package org.ctoolkit.restapi.client.pubsub;

import com.google.api.client.json.JsonParser;
import com.google.api.services.pubsub.model.PubsubMessage;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
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

    @Test
    public void getSubscriptionSuffix( @Mocked final HttpServletRequest request )
            throws ServletException
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
    public void getSubscriptionNoSuffix( @Mocked final HttpServletRequest request )
            throws ServletException
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
    public void onSubscriptionMessageNoListenerRegistered( @Mocked final HttpServletRequest request,
                                                           @Mocked final HttpServletResponse response )
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
    public void onSubscriptionMessage( @Mocked final HttpServletRequest request,
                                       @Mocked final HttpServletResponse response,
                                       @Mocked final PubsubMessageListener listener,
                                       @Mocked final PubsubMessage message,
                                       @Mocked final JsonParser parser )
            throws Exception
    {
        final String subscription = "content.delete";
        listeners.put( subscription, listener );

        new Expectations()
        {
            {
                request.getRequestURI();
                result = SubscriptionsListener.PUSH_HANDLERS_URL_PATH + subscription;

                parser.parseAndClose( PubsubMessage.class );
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