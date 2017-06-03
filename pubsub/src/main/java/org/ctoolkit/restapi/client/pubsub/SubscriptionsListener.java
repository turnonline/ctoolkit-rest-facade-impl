package org.ctoolkit.restapi.client.pubsub;

import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.pubsub.model.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    private static final long serialVersionUID = -4754611197274771298L;

    private static final Logger logger = LoggerFactory.getLogger( SubscriptionsListener.class );

    private final PubsubMessageListener listener;

    private final String subscription;

    /**
     * Constructs subscriptions listener.
     *
     * @param listener     the listener to receive messages for given subscription
     * @param subscription the subscription suffix
     */
    public SubscriptionsListener( @Nonnull PubsubMessageListener listener, @Nonnull String subscription )
    {
        this.listener = checkNotNull( listener );
        this.subscription = checkNotNull( subscription );
    }

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
            listener.onReceive( parser.parseAndClose( PubsubMessage.class ), subscription );

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

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( !( o instanceof SubscriptionsListener ) ) return false;

        SubscriptionsListener that = ( SubscriptionsListener ) o;

        return subscription.equals( that.subscription );
    }

    @Override
    public int hashCode()
    {
        return subscription.hashCode();
    }
}
