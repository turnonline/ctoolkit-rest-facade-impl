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

import net.sf.jsr107cache.Cache;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.provider.LocalResourceProvider;
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
 * The provider caching the resource.
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
    public final T get( @Nonnull Identifier identifier,
                        @Nullable Map<String, Object> parameters,
                        @Nullable Locale locale )
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
    public final void persist( @Nonnull T instance,
                               @Nonnull Identifier identifier,
                               @Nullable Map<String, Object> parameters,
                               @Nullable Locale locale,
                               @Nullable Long lastFor )
    {
        checkNotNull( instance );
        checkNotNull( identifier );

        Object key = composeKey( identifier, locale );
        cache.put( key, instance );
        logger.info( "The " + instance.getClass().getSimpleName() + " has been cached with key: " + key );
    }

    @Override
    public final List<T> list( @Nullable Map<String, Object> parameters,
                               @Nullable Locale locale,
                               @Nullable Date lastModifiedDate )
    {
        return null;
    }

    @Override
    public final void persistList( @Nonnull List<T> list,
                                   @Nullable Map<String, Object> parameters,
                                   @Nullable Locale locale,
                                   @Nullable Long lastFor )
    {
    }

    private String composeKey( @Nonnull Identifier identifier, @Nullable Locale locale )
    {
        checkNotNull( identifier );

        String prefix = keyPrefix();
        if ( prefix == null )
        {
            prefix = identifier.key();
        }
        else
        {
            prefix = prefix + ":" + identifier.key();
        }

        StringBuilder builder = new StringBuilder( prefix );

        if ( locale != null )
        {
            builder.append( "." );
            builder.append( locale.getLanguage() );
            builder.append( "_" );
            builder.append( locale.getCountry() );
        }

        return builder.toString();
    }

    /**
     * Returns a prefix to be prepended to the cache key.
     * By default returns {@code null}, override to provide your own prefix.
     *
     * @return the key prefix
     */
    protected String keyPrefix()
    {
        return null;
    }
}
