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
import com.google.api.client.http.HttpHeaders;
import org.ctoolkit.restapi.client.AuthRequest;
import org.ctoolkit.restapi.client.DownloadRequest;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.Request;
import org.ctoolkit.restapi.client.RequestCredential;
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
class DownloadRequestImpl
        implements DownloadRequest
{
    private final RestFacadeAdapter adapter;

    private final DownloadExecutorAdaptee adaptee;

    private final MediaHttpDownloader downloader;

    private final Class resource;

    private final Identifier identifier;

    private final OutputStream output;

    private final DownloadResponseInterceptor interceptor;

    private Map<String, Object> params;

    private Locale withLocale;

    private GoogleRequestHeaders filler;

    private String token;

    /**
     * Constructor.
     *
     * @param adapter     the resource facade adapter instance
     * @param adaptee     the download adaptee configured for given resource
     * @param downloader  the http downloader to interact with
     * @param resource    the type of resource to download as a media
     * @param identifier  the unique identifier of content to download
     * @param output      the output stream where desired content will be downloaded to
     * @param interceptor the response interceptor
     * @param type        the content type or {@code null} to expect default
     */
    DownloadRequestImpl( @Nonnull RestFacadeAdapter adapter,
                         @Nonnull DownloadExecutorAdaptee adaptee,
                         @Nonnull MediaHttpDownloader downloader,
                         @Nonnull Class resource,
                         @Nonnull Identifier identifier,
                         @Nonnull OutputStream output,
                         @Nonnull DownloadResponseInterceptor interceptor,
                         @Nullable String type )
    {
        this.adapter = checkNotNull( adapter );
        this.adaptee = checkNotNull( adaptee );
        this.downloader = checkNotNull( downloader );
        this.resource = checkNotNull( resource );
        this.identifier = checkNotNull( identifier );
        this.output = checkNotNull( output );
        this.interceptor = checkNotNull( interceptor );
        this.params = new HashMap<>();
        this.filler = new GoogleRequestHeaders( new HttpHeaders() );
        this.filler.contentType( type );
    }

    @Override
    public Map<String, Object> finish()
    {
        return finish( null, withLocale );
    }

    @Override
    public Map<String, Object> finish( @Nonnull RequestCredential credential )
    {
        checkNotNull( credential );
        credential.populate( this.params );
        return finish();
    }

    @Override
    public Map<String, Object> finish( @Nullable Map<String, Object> parameters )
    {
        return finish( parameters, withLocale );
    }

    @Override
    public Map<String, Object> finish( @Nullable Locale locale )
    {
        return finish( null, locale );
    }

    @Override
    public Map<String, Object> finish( @Nullable Map<String, Object> parameters, @Nullable Locale locale )
    {
        if ( parameters != null )
        {
            params.putAll( parameters );
        }

        filler.acceptLanguage( locale );
        filler.fillInCredential( params );
        if ( token != null )
        {
            filler.authorization( token );
        }

        HttpHeaders headers = filler.getHeaders();

        return adapter.executeDownload( downloader, adaptee, resource, identifier, output, interceptor,
                headers, params, locale );
    }

    @Override
    public Request<Map<String, Object>> configWith( @Nonnull Properties properties )
    {
        checkNotNull( properties );
        RequestCredential.populate( properties, this.params );
        return this;
    }

    @Override
    public Request<Map<String, Object>> forLang( @Nonnull Locale locale )
    {
        this.withLocale = checkNotNull( locale );
        return this;
    }

    @Override
    public Request<Map<String, Object>> add( @Nonnull String name, @Nonnull Object value )
    {
        checkNotNull( name );
        checkNotNull( value );

        params.put( name, value );
        return this;
    }

    @Override
    public Request<Map<String, Object>> add( @Nonnull String name, @Nonnull String value )
    {
        checkNotNull( name );
        checkNotNull( value );

        params.put( name, value );
        return this;
    }

    @Override
    public Request<Map<String, Object>> addHeader( @Nonnull String header, @Nonnull String value )
    {
        checkNotNull( header );
        checkNotNull( value );

        filler.addHeader( header, value );
        return this;
    }

    @Override
    public AuthRequest<Map<String, Object>> authBy( @Nonnull String authorization )
    {
        checkNotNull( authorization );

        this.token = authorization;
        return new AuthRequestImpl<>( this, filler );
    }
}
