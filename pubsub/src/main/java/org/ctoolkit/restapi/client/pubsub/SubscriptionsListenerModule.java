package org.ctoolkit.restapi.client.pubsub;

import com.google.inject.servlet.ServletModule;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.ctoolkit.restapi.client.pubsub.SubscriptionsListener.SUBSCRIPTION_SUFFIX_INIT_PARAM;

/**
 * The guice module to configure {@link SubscriptionsListener} to listen at {@link #PUSH_HANDLERS_URL_PATH}
 * + subscription suffix. Known as subscription endpoint URL where Pub/Sub service will push messages.
 * This will allow the endpoint to receive push messages from Google Cloud Pub/Sub.
 * <p>
 * Protect the {@link #PUSH_HANDLERS_URL_PATH} URLs by requiring administrator login.
 * <pre>
 * {@code
 *  <security-constraint>
 *      <web-resource-collection>
 *          <web-resource-name>PUSH handlers</web-resource-name>
 *          <url-pattern>/_ah/push-handlers/*</url-pattern>
 *      </web-resource-collection>
 *      <auth-constraint>
 *          <role-name>admin</role-name>
 *      </auth-constraint>
 *  </security-constraint>
 * }
 * </pre>
 * To associate {@link PubsubMessageListener} implementation with this subscription use following:
 * <pre>
 * {@code
 *  install( new SubscriptionsListenerModule( "MySubscriptionSuffix" ) );
 *
 *  MapBinder<String, PubsubMessageListener> map;
 *  map = MapBinder.newMapBinder( binder(), String.class, PubsubMessageListener.class );
 *  map.addBinding( "MySubscriptionSuffix" ).to( MyPubsubMessageListener.class );
 * }
 * </pre>
 * This configuration will create following endpoint URL:
 * '/_ah/push-handlers/MySubscriptionSuffix'
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class SubscriptionsListenerModule
        extends ServletModule
{
    public static final String PUSH_HANDLERS_URL_PATH = "/_ah/push-handlers/";

    private final String[] subscriptions;

    /**
     * Install guice module in order to configure subscription(s) specific endpoint URL.
     *
     * @param subscriptions the array of subscription(s) suffix
     */
    public SubscriptionsListenerModule( @Nonnull String... subscriptions )
    {
        this.subscriptions = checkNotNull( subscriptions );
    }

    @Override
    protected void configureServlets()
    {
        Map<String, String> params;
        for ( String suffix : subscriptions )
        {
            params = new HashMap<>();
            params.put( SUBSCRIPTION_SUFFIX_INIT_PARAM, suffix );
            serve( PUSH_HANDLERS_URL_PATH + suffix ).with( SubscriptionsListener.class, params );
        }
    }
}
