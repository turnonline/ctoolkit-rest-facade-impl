/*
 * Copyright (c) 2017 Comvai, s.r.o. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.ctoolkit.restapi.client.adapter;

import com.google.api.client.http.HttpRequest;

/**
 * The event called right before call to the remote REST endpoint.
 * <p>
 * <strong>Example how to subscribe to this event</strong>
 * <pre>
 * import com.google.common.eventbus.EventBus;
 * import com.google.common.eventbus.Subscribe;
 * import org.ctoolkit.restapi.client.adapter.BeforeRequestEvent;
 *
 * import javax.inject.Inject;
 * import javax.inject.Provider;
 * import javax.inject.Singleton;
 * import javax.servlet.http.HttpServletRequest;
 *
 * &#64;Singleton
 * class MySubscriber
 * {
 *     // Provider to provide current http servlet request
 *     private final Provider&#60;HttpServletRequest&#62; provider;
 *
 *     &#64;Inject
 *     public MySubscriber( EventBus eventBus, Provider&#60;HttpServletRequest&#62; provider )
 *     {
 *         eventBus.register( this );
 *         this.provider = provider;
 *     }
 *
 *     &#64;Subscribe
 *     public void onBeforeRequestEvent( BeforeRequestEvent event )
 *     {
 *         com.google.api.client.http.HttpRequest clientRequest = event.getRequest();
 *         javax.servlet.http.HttpServletRequest originRequest = provider.get();
 *
 *         // forward some headers from origin HTTP request or do something else in order to populate client request
 *     }
 * }
 * Then include in to injection graph in Guice AbstractModule
 * bind( MySubscriber.class ).asEagerSingleton();
 * </pre>
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class BeforeRequestEvent
{
    private HttpRequest request;

    public BeforeRequestEvent( HttpRequest request )
    {
        this.request = request;
    }

    public HttpRequest getRequest()
    {
        return request;
    }
}
