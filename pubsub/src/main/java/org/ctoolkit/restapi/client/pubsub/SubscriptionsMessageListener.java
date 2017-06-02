package org.ctoolkit.restapi.client.pubsub;

import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.common.base.Charsets;

import javax.inject.Singleton;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Pub/Sub subscriptions listener. The servlet receiving messages and routing to the targets.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
public class SubscriptionsMessageListener
        extends HttpServlet
{
    @Override
    public final void doPost( final HttpServletRequest request, final HttpServletResponse response )
            throws IOException
    {

        try
        {
            ServletInputStream inputStream = request.getInputStream();

            // Parse the JSON message to the POJO model class
            JsonParser parser = JacksonFactory.getDefaultInstance().createJsonParser( inputStream );
            parser.skipToKey( "message" );
            PubsubMessage message = parser.parseAndClose( PubsubMessage.class );
            String jsonPayload = new String( message.decodeData(), Charsets.UTF_8 );
            System.out.println( "jsonPayload = " + jsonPayload );

            // Acknowledge the message by returning a success code .
            // 204 status code is considered as an implicit acknowledgement.
            response.setStatus( HttpServletResponse.SC_NO_CONTENT );
            response.getWriter().close();
        }
        catch ( IOException e )
        {
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            response.getWriter().close();
        }
    }
}
