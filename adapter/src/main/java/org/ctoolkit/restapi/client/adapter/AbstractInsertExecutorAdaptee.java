package org.ctoolkit.restapi.client.adapter;

import org.ctoolkit.restapi.client.adaptee.InsertExecutorAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

/**
 * The base Google client specific implementation of the {@link InsertExecutorAdaptee}.
 * It provides a default implementation of the {@link #executeInsert(Object, Map, Locale)} method.
 *
 * @param <C> the concrete type of the client instance
 * @param <M> the concrete type of the resource associated with this adaptee
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public abstract class AbstractInsertExecutorAdaptee<C, M>
        extends AbstractGoogleClientAdaptee<C>
        implements InsertExecutorAdaptee<M>
{
    public AbstractInsertExecutorAdaptee( Provider<C> client )
    {
        super( client );
    }

    @Override
    public Object executeInsert( @Nonnull Object request,
                                 @Nullable Map<String, Object> parameters,
                                 @Nullable Locale locale )
            throws IOException
    {
        return execute( request, parameters );
    }
}
