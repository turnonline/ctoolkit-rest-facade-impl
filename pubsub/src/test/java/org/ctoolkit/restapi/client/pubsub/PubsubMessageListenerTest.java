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

package org.ctoolkit.restapi.client.pubsub;

import com.google.api.client.util.Charsets;
import com.google.api.services.pubsub.model.PubsubMessage;
import com.google.common.io.CharStreams;
import mockit.Tested;
import org.testng.annotations.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.google.common.truth.Truth.assertThat;

/**
 * {@link PubsubMessageListener} unit testing.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class PubsubMessageListenerTest
{
    @Tested
    private PubsubMessageListenerImpl tested;

    @Test
    public void fromString() throws IOException
    {
        InputStream stream = InvoicingConfig.class.getResourceAsStream( "invoicing.base64" );
        String result = CharStreams.toString( new InputStreamReader( stream, Charsets.UTF_8 ) );
        InvoicingConfig invoicing = tested.fromString( result, InvoicingConfig.class );

        assertThat( invoicing ).isNotNull();
        assertThat( invoicing.getId() ).isEqualTo( 13579246810291L );
        assertThat( invoicing.getCurrency() ).isEqualTo( "EUR" );
        assertThat( invoicing.getNumberOfDays() ).isEqualTo( 30 );
        assertThat( invoicing.getHasBillingAddress() ).isEqualTo( true );

        InvoicingConfigBillingAddress billingAddress = invoicing.getBillingAddress();
        assertThat( billingAddress ).isNotNull();
        assertThat( billingAddress.getBusinessName() ).isEqualTo( "My own business, Ltd" );
        assertThat( billingAddress.getStreet() ).isEqualTo( "Bajkalská 717/29" );
        assertThat( billingAddress.getCity() ).isEqualTo( "Bratislava" );
        assertThat( billingAddress.getCountry() ).isEqualTo( "SK" );
        assertThat( billingAddress.getPostcode() ).isEqualTo( "82105" );
        assertThat( billingAddress.getLatitude() ).isEqualTo( 48.1570754D );
        assertThat( billingAddress.getLongitude() ).isEqualTo( 17.1662531 );
    }

    private static class PubsubMessageListenerImpl
            implements PubsubMessageListener
    {
        private static final long serialVersionUID = -7050097340251109501L;

        @Override
        public void onMessage( @Nonnull PubsubMessage message, @Nonnull String subscription )
        {
        }
    }
}