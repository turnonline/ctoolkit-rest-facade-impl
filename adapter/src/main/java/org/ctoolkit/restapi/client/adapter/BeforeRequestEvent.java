package org.ctoolkit.restapi.client.adapter;

import com.google.api.client.http.HttpRequest;

/**
 * The event called right before call to the remote REST endpoint.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class BeforeRequestEvent
{
    private HttpRequest request;

    public BeforeRequestEvent( HttpRequest request )
    {
        this.request = request;
    }

    public HttpRequest getRequest()
    {
        return request;
    }
}
