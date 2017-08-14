package org.ctoolkit.restapi.client.adapter;

import org.ctoolkit.restapi.client.adaptee.UpdateExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * The base Google client specific implementation of the {@link UpdateExecutorAdaptee}.
 * It provides a default implementation of the {@link #executeUpdate(Object, Map, Locale)} method.
 *
 * @param <C> the concrete type of the client instance
 * @param <M> the concrete type of the resource associated with this adaptee
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public abstract class AbstractUpdateExecutorAdaptee<C, M>
        extends AbstractGoogleClientAdaptee<C>
        implements UpdateExecutorAdaptee<M>
{
    public AbstractUpdateExecutorAdaptee( C client )
    {
        super( client );
    }

    @Override
    public Object executeUpdate( @Nonnull Object request,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
            throws IOException
    {
        return execute( request, parameters );
    }
}
