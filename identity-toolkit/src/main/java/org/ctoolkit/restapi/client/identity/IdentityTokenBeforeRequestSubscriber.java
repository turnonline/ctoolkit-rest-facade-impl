package org.ctoolkit.restapi.client.identity;

import com.google.api.client.http.HttpRequest;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.ctoolkit.restapi.client.adapter.BeforeRequestEvent;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * The event bus subscriber to listen on {@link BeforeRequestEvent} in order to forward origin request token value
 * (identity toolkit cookie {@link Identity#GTOKEN}), if any, via Google API client request (value of the event) header.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
public class IdentityTokenBeforeRequestSubscriber
{
    /*
     * Provider to provide current http servlet request
     */
    private final Provider<HttpServletRequest> provider;

    @Inject
    public IdentityTokenBeforeRequestSubscriber( EventBus eventBus, Provider<HttpServletRequest> provider )
    {
        eventBus.register( this );
        this.provider = provider;
    }

    @Subscribe
    public void onBeforeRequestEvent( BeforeRequestEvent event )
    {
        HttpRequest request = event.getRequest();
        HttpServletRequest originServletRequest = provider.get();

        Cookie[] cookies = originServletRequest.getCookies();

        if ( cookies == null )
        {
            return;
        }

        String token = null;
        for ( Cookie cookie : cookies )
        {
            if ( Identity.GTOKEN.equals( cookie.getName() ) )
            {
                token = cookie.getValue();
                break;
            }
        }

        if ( token != null )
        {
            request.getHeaders().put( Identity.GTOKEN, token );
        }
    }
}
