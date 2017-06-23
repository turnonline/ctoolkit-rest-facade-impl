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

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RequestCredential;
import org.ctoolkit.restapi.client.SimpleRequest;
import org.ctoolkit.restapi.client.adaptee.DownloadExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The download request that will delegate a call to remote endpoint.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class DownloadRequest
        implements SimpleRequest<Void>
{
    private final RestFacadeAdapter adapter;

    private final DownloadExecutorAdaptee adaptee;

    private final MediaHttpDownloader downloader;

    private final Class resource;

    private final Identifier identifier;

    private final OutputStream output;

    private final String type;

    private Map<String, Object> params;

    private Locale withLocale;

    /**
     * Constructor.
     *
     * @param adapter    the resource facade adapter instance
     * @param adaptee    the download adaptee configured for given resource
     * @param downloader the http downloader to interact with
     * @param resource   the type of resource to download as a media
     * @param identifier the unique identifier of content to download
     * @param output     the output stream where desired content will be downloaded to.
     * @param type       the content type or {@code null} to expect default
     */
    DownloadRequest( @Nonnull RestFacadeAdapter adapter,
                     @Nonnull DownloadExecutorAdaptee adaptee,
                     @Nonnull MediaHttpDownloader downloader,
                     @Nonnull Class resource,
                     @Nonnull Identifier identifier,
                     @Nonnull OutputStream output,
                     @Nullable String type )
    {
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.downloader = checkNotNull( downloader );
        this.resource = checkNotNull( resource );
        this.identifier = checkNotNull( identifier );
        this.output = checkNotNull( output );
        this.type = type;
        this.params = new HashMap<>();
    }

    @Override
    public Void finish()
    {
        return finish( null, withLocale );
    }

    @Override
    public Void finish( @Nonnull RequestCredential credential )
    {
        checkNotNull( credential );
        credential.populate( this.params );
        return finish();
    }

    @Override
    public Void finish( @Nullable Map<String, Object> parameters )
    {
        return finish( parameters, withLocale );
    }

    @Override
    public Void finish( @Nullable Locale locale )
    {
        return finish( null, locale );
    }

    @Override
    public Void finish( @Nullable Map<String, Object> parameters, @Nullable Locale locale )
    {
        if ( parameters != null )
        {
            params.putAll( parameters );
        }

        return adapter.executeDownload( downloader, adaptee, resource, identifier, output, type, params, locale );
    }

    @Override
    public Request<Void> configWith( @Nonnull Properties properties )
    {
        checkNotNull( properties );
        RequestCredential.populate( properties, this.params );
        return this;
    }

    @Override
    public Request<Void> forLang( @Nonnull Locale locale )
    {
        this.withLocale = checkNotNull( locale );
        return this;
    }

    @Override
    public Request<Void> add( @Nonnull String name, @Nonnull Object value )
    {
        checkNotNull( name );
        checkNotNull( value );

        params.put( name, value );
        return this;
    }

    @Override
    public Request<Void> add( @Nonnull String name, @Nonnull String value )
    {
        checkNotNull( name );
        checkNotNull( value );

        params.put( name, value );
        return this;
    }
}
