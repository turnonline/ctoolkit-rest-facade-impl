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

import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.http.HttpHeaders;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.DeleteExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.DownloadExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.NewExecutorAdaptee;
import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The plugin to give possibility substitute a remote resource with local record if desirable.
 * Mainly for the testing purpose avoiding a remote calls.
 * <p>
 * The implementation is optional and once configured will take a precedence over remote calls.
 * <p>
 * {@code bind( Substitute.class ).to( MySubstituteImpl.class );}
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public interface Substitute
{
    /**
     * Same input parameters except Adaptee that is not provided here.
     *
     * @throws ProceedWithRemoteCall if it is preferred to continue with remote call for concrete use cases
     * @see RestFacadeAdapter#callbackNewInstance(NewExecutorAdaptee, Object, Class, Map, Locale)
     */
    <R> R newInstance( @Nonnull Object remoteRequest,
                       @Nonnull Class<R> responseType,
                       @Nullable Map<String, Object> parameters,
                       @Nullable Locale locale );

    /**
     * Same input parameters except Adaptee and DownloadResponseInterceptor that is not provided here.
     *
     * @throws ProceedWithRemoteCall if it is preferred to continue with remote call for concrete use cases
     * @see RestFacadeAdapter#executeDownload(MediaHttpDownloader, DownloadExecutorAdaptee, Class, Identifier,
     * OutputStream, DownloadResponseInterceptor, HttpHeaders, Map, Locale)
     */
    void download( @Nonnull Class resource,
                   @Nonnull Identifier identifier,
                   @Nonnull OutputStream output,
                   @Nullable HttpHeaders headers,
                   @Nullable Map<String, Object> params,
                   @Nullable Locale locale );

    /**
     * Same input parameters except Adaptee that is not provided here.
     *
     * @throws ProceedWithRemoteCall if it is preferred to continue with remote call for concrete use cases
     * @see RestFacadeAdapter#callbackExecuteGet(GetExecutorAdaptee, Object, Class, Identifier, Map, Locale)
     */
    <R> R get( @Nonnull Object remoteRequest,
               @Nonnull Class<R> responseType,
               @Nonnull Identifier identifier,
               @Nullable Map<String, Object> parameters,
               @Nullable Locale locale );

    /**
     * Same input parameters except Adaptee that is not provided here.
     *
     * @throws ProceedWithRemoteCall if it is preferred to continue with remote call for concrete use cases
     * @see RestFacadeAdapter#callbackExecuteList(ListExecutorAdaptee, Object, Class, Map, Locale, int, int,
     * String, Boolean)
     */
    <R> List<R> list( @Nonnull Object remoteRequest,
                      @Nonnull Class<R> responseType,
                      @Nullable Map<String, Object> criteria,
                      @Nullable Locale locale,
                      int start,
                      int length,
                      @Nullable String orderBy,
                      @Nullable Boolean ascending );

    /**
     * Same input parameters except Adaptee that is not provided here.
     *
     * @throws ProceedWithRemoteCall if it is preferred to continue with remote call for concrete use cases
     * @see RestFacadeAdapter#callbackExecuteInsert(InsertExecutorAdaptee, Object, Class, Identifier, Map, Locale)
     */
    <R> R insert( @Nonnull Object remoteRequest,
                  @Nonnull Class<R> responseType,
                  @Nullable Identifier parentKey,
                  @Nullable Map<String, Object> parameters,
                  @Nullable Locale locale );

    /**
     * Same input parameters except Adaptee that is not provided here.
     *
     * @throws ProceedWithRemoteCall if it is preferred to continue with remote call for concrete use cases
     * @see RestFacadeAdapter#callbackExecuteUpdate(UpdateExecutorAdaptee, Object, Class, Object, Map, Locale)
     */
    <R> R update( @Nonnull Object remoteRequest,
                  @Nonnull Class<R> responseType,
                  @Nonnull Object identifier,
                  @Nullable Map<String, Object> parameters,
                  @Nullable Locale locale );

    /**
     * Same input parameters except Adaptee that is not provided here.
     *
     * @throws ProceedWithRemoteCall if it is preferred to continue with remote call for concrete use cases
     * @see RestFacadeAdapter#callbackExecuteDelete(DeleteExecutorAdaptee, Object, Object, Class, Map, Locale)
     */
    <R> R delete( @Nonnull Object remoteRequest,
                  @Nonnull Object identifier,
                  @Nullable Class<R> responseType,
                  @Nullable Map<String, Object> parameters,
                  @Nullable Locale locale );

    /**
     * Conditional way to tell {@link RestFacadeAdapter} to continue with remote call.
     */
    class ProceedWithRemoteCall
            extends RuntimeException
    {
        private static final long serialVersionUID = 2512251444836265184L;
    }
}
