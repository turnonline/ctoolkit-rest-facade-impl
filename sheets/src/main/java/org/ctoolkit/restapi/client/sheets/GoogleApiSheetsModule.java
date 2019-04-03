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

package org.ctoolkit.restapi.client.sheets;

import com.google.api.services.sheets.v4.Sheets;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import org.ctoolkit.restapi.client.adaptee.UnderlyingClientAdaptee;
import org.ctoolkit.restapi.client.sheets.adaptee.ClientAdaptee;

/**
 * The Google Sheets guice module as a default configuration.
 * Install this module if {@link Sheets} needs to be injected.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class GoogleApiSheetsModule
        extends AbstractModule
{
    public static final String API_PREFIX = "sheets";

    @Override
    protected void configure()
    {
        bind( Sheets.class ).toProvider( SheetsProvider.class );

        bind( new TypeLiteral<UnderlyingClientAdaptee<Sheets>>()
        {
        } ).to( ClientAdaptee.class );
    }
}
