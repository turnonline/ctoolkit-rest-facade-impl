/*
 * Copyright (c) 2018 Comvai, s.r.o. All Rights Reserved.
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

import com.google.api.client.http.HttpHeaders;
import mockit.Tested;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Unit testing of the {@link GoogleRequestHeaders}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GoogleRequestHeadersTest
{
    private static final String fakeToken = "y23.789abc";

    private static final String bearerToken = GoogleRequestHeaders.AuthScheme.BEARER.getValue() + " " + fakeToken;

    private static final String oauthToken = GoogleRequestHeaders.AuthScheme.OAUTH.getValue() + " " + fakeToken;

    @Tested( fullyInitialized = true )
    private GoogleRequestHeaders tested;

    @Test
    public void authorizationNone()
    {
        final HttpHeaders httpHeaders = tested.getHeaders();
        assertThat( httpHeaders.getAuthorization() ).isNull();
    }

    @Test
    public void authorizationNoAuthScheme()
    {
        tested.authorization( fakeToken );
        final HttpHeaders httpHeaders = tested.getHeaders();

        assertThat( httpHeaders.getAuthorization() ).isEqualTo( fakeToken );
    }

    @Test
    public void authorizationBearer()
    {
        tested.setAuthScheme( GoogleRequestHeaders.AuthScheme.BEARER );
        tested.authorization( fakeToken );
        final HttpHeaders httpHeaders = tested.getHeaders();

        assertThat( httpHeaders.getAuthorization() ).isEqualTo( bearerToken );
    }

    @Test
    public void authorizationOauth()
    {
        tested.setAuthScheme( GoogleRequestHeaders.AuthScheme.OAUTH );
        tested.authorization( fakeToken );
        final HttpHeaders httpHeaders = tested.getHeaders();

        assertThat( httpHeaders.getAuthorization() ).isEqualTo( oauthToken );
    }

    @Test
    public void authorizationAlreadyWithBearer()
    {
        tested.authorization( bearerToken );
        final HttpHeaders httpHeaders = tested.getHeaders();

        assertThat( httpHeaders.getAuthorization() ).isEqualTo( bearerToken );
    }

    @Test
    public void authorizationAlreadyWithOauth()
    {
        tested.authorization( oauthToken );
        final HttpHeaders httpHeaders = tested.getHeaders();

        assertThat( httpHeaders.getAuthorization() ).isEqualTo( oauthToken );
    }
}