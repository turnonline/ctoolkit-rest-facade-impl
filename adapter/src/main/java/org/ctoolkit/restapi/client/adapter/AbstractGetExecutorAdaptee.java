package org.ctoolkit.restapi.client.adapter;

import org.ctoolkit.restapi.client.adaptee.GetExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * The base Google client specific implementation of the {@link GetExecutorAdaptee}.
 * It provides a default implementation of the {@link #executeGet(Object, Map, Locale)} method.
 *
 * @param <C> the concrete type of the client instance
 * @param <M> the concrete type of the resource associated with this adaptee
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public abstract class AbstractGetExecutorAdaptee<C, M>
        extends AbstractGoogleClientAdaptee<C>
        implements GetExecutorAdaptee<M>
{
    public AbstractGetExecutorAdaptee( C client )
    {
        super( client );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public M executeGet( @Nonnull Object request,
                         @Nullable Map<String, Object> parameters,
                         @Nullable Locale locale )
            throws IOException
    {
        return ( M ) execute( request, parameters );
    }
}
