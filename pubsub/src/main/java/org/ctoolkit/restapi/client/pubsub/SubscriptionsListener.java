package org.ctoolkit.restapi.client.pubsub;

import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Pub/Sub subscriptions listener. The servlet receiving {@link PubsubMessage}
 * and routing to the associated {@link PubsubMessageListener}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
public class SubscriptionsListener
        extends HttpServlet
{
    public static final String PUSH_HANDLERS_URL_PATH = "/_ah/push-handlers/";

    private static final long serialVersionUID = -4754611197274771298L;

    private static final Logger logger = LoggerFactory.getLogger( SubscriptionsListener.class );

    private final Map<String, PubsubMessageListener> listeners;

    /**
     * Constructs subscriptions listener.
     *
     * @param listeners the map of configured listeners
     */
    @Inject
    public SubscriptionsListener( Map<String, PubsubMessageListener> listeners )
    {
        this.listeners = checkNotNull( listeners );
    }

    @Override
    public final void doPost( final HttpServletRequest request, final HttpServletResponse response )
            throws IOException
    {
        String subscription = getSubscriptionSuffix( request );

        try
        {
            ServletInputStream inputStream = request.getInputStream();

            PubsubMessageListener listener = listeners.get( subscription );
            if ( listener == null )
            {
                response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
                response.getWriter().close();

                String message = PubsubMessageListener.class.getSimpleName() + " not found for '" + subscription + "'";
                throw new IllegalArgumentException( message );
            }

            // Parse the JSON message to the POJO model class
            JsonParser parser = JacksonFactory.getDefaultInstance().createJsonParser( inputStream );
            parser.skipToKey( "message" );
            listener.onMessage( parser.parseAndClose( PubsubMessage.class ), subscription );

            // Acknowledge the message by returning a success code .
            // 204 status code is considered as an implicit acknowledgement.
            response.setStatus( HttpServletResponse.SC_NO_CONTENT );
            response.getWriter().close();
        }
        catch ( IOException e )
        {
            logger.error( "Receiving of the message for subscription " + subscription + " has failed.", e );
            response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            response.getWriter().close();
        }
    }

    @VisibleForTesting
    String getSubscriptionSuffix( HttpServletRequest request )
    {
        String uri = request.getRequestURI();
        Splitter splitter = Splitter.on( PUSH_HANDLERS_URL_PATH );
        List<String> strings = splitter.omitEmptyStrings().splitToList( uri );
        if ( strings.isEmpty() )
        {
            String message = "There is missing push handler suffix configuration for current URI: " + uri;
            throw new IllegalArgumentException( message );
        }
        return strings.get( 0 );
    }
}
