/*
 * Copyright (c) 2019 Comvai, s.r.o. All Rights Reserved.
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

package org.ctoolkit.restapi.client.sheets.adaptee;

import com.google.api.services.sheets.v4.Sheets;
import org.ctoolkit.restapi.client.adaptee.UnderlyingClientAdaptee;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Underlying {@link Sheets} client adaptee binding.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
@Singleton
public class ClientAdaptee
        extends AbstractGoogleClientAdaptee<Sheets>
        implements UnderlyingClientAdaptee<Sheets>
{
    @Inject
    ClientAdaptee( Provider<Sheets> client )
    {
        super( client );
    }

    @Override
    public Sheets getUnderlyingClient()
    {
        return client();
    }
}
