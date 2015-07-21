/*
 * Copyright (c) 2015 Comvai, s.r.o. All Rights Reserved.
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

import net.sf.jsr107cache.Cache;
import org.ctoolkit.restapi.client.LocalResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The resource provider that caches the instance where instance identifier
 * is being treated as a string type cache key + optional locale.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 * @see Cache
 */
public class GetCachedResourceProvider<T>
        implements LocalResourceProvider<T>
{
    private static final Logger logger = LoggerFactory.getLogger( GetCachedResourceProvider.class );

    private final Cache cache;

    @Inject
    public GetCachedResourceProvider( Cache cache )
    {
        this.cache = cache;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public final T get( Object identifier, Map<String, Object> parameters, Locale locale, Date lastModifiedDate )
    {
        checkNotNull( identifier );

        String key = composeKey( identifier, locale );

        if ( !cache.containsKey( key ) )
        {
            return null;
        }

        return ( T ) cache.get( key );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public final void persist( @Nonnull T instance, Object identifier, Map<String, Object> parameters, Locale locale )
    {
        checkNotNull( instance );
        checkNotNull( identifier );

        Object key = composeKey( identifier, locale );
        cache.put( key, instance );
        logger.info( "The " + instance.getClass().getSimpleName() + " has cached with key: " + key );
    }

    @Override
    public final List<T> list( Map<String, Object> parameters, Locale locale, Date lastModifiedDate )
    {
        return null;
    }

    @Override
    public final void persistList( @Nonnull List<T> instance, Map<String, Object> parameters, Locale locale )
    {
    }

    private String composeKey( @Nonnull Object identifier, @Nullable Locale locale )
    {
        checkNotNull( identifier );

        StringBuilder builder = new StringBuilder( identifier.toString() );

        if ( locale != null )
        {
            builder.append( "." );
            builder.append( locale.getLanguage() );
            builder.append( "_" );
            builder.append( locale.getCountry() );
        }

        return builder.toString();
    }
}
