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

package org.ctoolkit.restapi.client.appengine;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * The optional JCache provider to provide cache instance with own dedicated namespace: ${@link #CACHE_NAMESPACE}.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class JCacheProvider
        implements Provider<Cache>
{
    public static final String CACHE_NAMESPACE = "REST_API_CLIENT_FACADE";

    @Override
    public Cache get()
    {
        Map<String, String> properties = new HashMap<>();
        properties.put( GCacheFactory.NAMESPACE, CACHE_NAMESPACE );

        CacheFactory factory;
        try
        {
            factory = CacheManager.getInstance().getCacheFactory();
            return factory.createCache( properties );
        }
        catch ( CacheException e )
        {
            throw new RuntimeException( e );
        }
    }
}
