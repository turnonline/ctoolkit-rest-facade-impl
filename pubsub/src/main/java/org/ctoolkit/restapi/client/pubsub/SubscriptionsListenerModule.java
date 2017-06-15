package org.ctoolkit.restapi.client.pubsub;

import com.google.inject.servlet.ServletModule;

/**
 * The guice module to configure {@link SubscriptionsListener} to listen for all incoming messages at
 * /_ah/push-handlers/* known as subscription endpoint URL where Pub/Sub service will push messages.
 * This will allow the endpoint to receive push messages from Google Cloud Pub/Sub.
 * <p>
 * Protect the /_ah/push-handlers/* URLs by requiring administrator login.
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
 *  install( new SubscriptionsListenerModule() );
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
    @Override
    protected void configureServlets()
    {
        serve( SubscriptionsListener.PUSH_HANDLERS_URL_PATH + "*" ).with( SubscriptionsListener.class );
    }
}
