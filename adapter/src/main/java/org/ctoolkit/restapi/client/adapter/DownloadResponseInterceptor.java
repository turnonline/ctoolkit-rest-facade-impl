package org.ctoolkit.restapi.client.adapter;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseInterceptor;

import java.io.IOException;

/**
 * The helper download interceptor class to get an access to the response headers.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
class DownloadResponseInterceptor
        implements HttpResponseInterceptor
{
    private HttpHeaders headers;

    @Override
    public void interceptResponse( HttpResponse response ) throws IOException
    {
        headers = response.getHeaders();
    }

    /**
     * Returns the download's response headers.
     *
     * @return the response headers
     */
    HttpHeaders getHeaders()
    {
        return headers == null ? new HttpHeaders() : headers;
    }
}
