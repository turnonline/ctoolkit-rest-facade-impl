package org.ctoolkit.restapi.client.pubsub;

import com.google.inject.servlet.ServletModule;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

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
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class SubscriptionsListenerModule
        extends ServletModule
{
    public static final String PUSH_HANDLERS_URL_PATH = "/_ah/push-handlers/";

    private final String subscriptionSuffix;

    private final PubsubMessageListener listener;

    /**
     * Install guice module to configure subscription specific endpoint URL.
     *
     * @param suffix the subscription suffix
     */
    public SubscriptionsListenerModule( @Nonnull String suffix, @Nonnull PubsubMessageListener listener )
    {
        this.subscriptionSuffix = checkNotNull( suffix );
        this.listener = checkNotNull( listener );
    }

    @Override
    protected void configureServlets()
    {
        SubscriptionsListener servlet = new SubscriptionsListener( listener, subscriptionSuffix );
        serve( PUSH_HANDLERS_URL_PATH + subscriptionSuffix ).with( servlet );
    }
}
