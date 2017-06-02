package org.ctoolkit.restapi.client.pubsub;

import com.google.inject.servlet.ServletModule;

/**
 * The guice module to configure {@link SubscriptionsMessageListener} to listen at {@link #PUSH_HANDLERS_URL_PATH}.
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
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class SubscriptionListenerModule
        extends ServletModule
{

    public static final String PUSH_HANDLERS_URL_PATH = "/_ah/push-handlers/";

    @Override
    protected void configureServlets()
    {
        serve( PUSH_HANDLERS_URL_PATH ).with( SubscriptionsMessageListener.class );
    }
}
