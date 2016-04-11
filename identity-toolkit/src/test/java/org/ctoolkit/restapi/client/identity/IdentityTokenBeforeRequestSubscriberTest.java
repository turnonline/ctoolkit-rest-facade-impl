/*
 * Copyright (c) 2016 Comvai, s.r.o. All Rights Reserved.
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

package org.ctoolkit.restapi.client.identity;

import com.google.api.client.http.HttpHeaders;
import com.google.common.eventbus.EventBus;
import mockit.Injectable;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;
import org.ctoolkit.restapi.client.adapter.BeforeRequestEvent;
import org.testng.annotations.Test;

import javax.inject.Provider;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Unit tests to test {@link IdentityTokenBeforeRequestSubscriber}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class IdentityTokenBeforeRequestSubscriberTest
{
    @Tested
    private IdentityTokenBeforeRequestSubscriber tested;

    @Injectable
    private EventBus eventBus;

    @Injectable
    private Provider<HttpServletRequest> provider;

    @Test
    public void noCookies( @Mocked final BeforeRequestEvent event,
                           @Mocked final HttpHeaders headers ) throws Exception
    {
        new NonStrictExpectations()
        {
            {
                event.getRequest().getHeaders();
                result = headers;

                provider.get().getCookies();
                result = null;
            }
        };

        tested.onBeforeRequestEvent( event );

        new Verifications()
        {
            {
                headers.put( anyString, any );
                times = 0;
            }
        };
    }

    @Test
    public void gtokenNotFound( @Mocked final BeforeRequestEvent event,
                                @Mocked final HttpHeaders headers,
                                @Mocked final Cookie cookie1,
                                @Mocked final Cookie cookie2 ) throws Exception
    {
        final Cookie[] cookies = new Cookie[2];
        cookies[0] = cookie1;
        cookies[1] = cookie2;

        new NonStrictExpectations()
        {
            {
                event.getRequest().getHeaders();
                result = headers;

                provider.get().getCookies();
                result = cookies;
            }
        };

        tested.onBeforeRequestEvent( event );

        new Verifications()
        {
            {
                cookie1.getName();
                times = 1;

                cookie2.getName();
                times = 1;

                cookie1.getValue();
                times = 0;

                cookie2.getValue();
                times = 0;

                headers.put( anyString, any );
                times = 0;
            }
        };
    }

    @Test
    public void onBeforeRequestEventFull( @Mocked final BeforeRequestEvent event,
                                          @Mocked final HttpHeaders headers,
                                          @Mocked final Cookie cookie1,
                                          @Mocked final Cookie cookie2 ) throws Exception
    {
        final String token = "tokenvalue";
        final Cookie[] cookies = new Cookie[2];
        cookies[0] = cookie1;
        cookies[1] = cookie2;

        new NonStrictExpectations()
        {
            {
                event.getRequest().getHeaders();
                result = headers;

                provider.get().getCookies();
                result = cookies;

                cookie1.getName();
                result = "a";

                cookie2.getName();
                result = Identity.GTOKEN;

                cookie2.getValue();
                result = token;
            }
        };

        tested.onBeforeRequestEvent( event );

        new Verifications()
        {
            {
                headers.put( Identity.GTOKEN, token );
                times = 1;
            }
        };
    }
}