package org.ctoolkit.restapi.client.migration.adaptee;

import org.ctoolkit.api.migration.CtoolkitAgent;
import org.ctoolkit.api.migration.CtoolkitAgentRequest;
import org.ctoolkit.api.migration.model.KindMetaData;
import org.ctoolkit.api.migration.model.KindMetaDataCollection;
import org.ctoolkit.restapi.client.Identifier;
import org.ctoolkit.restapi.client.adaptee.ListExecutorAdaptee;
import org.ctoolkit.restapi.client.adapter.AbstractGoogleClientAdaptee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class GenericJsonKindMetaDataAdaptee
        extends AbstractGoogleClientAdaptee<Provider<CtoolkitAgent>, KindMetaData>
        implements ListExecutorAdaptee<KindMetaData>
{
    @Inject
    public GenericJsonKindMetaDataAdaptee( Provider<CtoolkitAgent> client )
    {
        super( client );
    }

    @Override
    public Object prepareList( @Nullable Identifier parentKey ) throws IOException
    {
        return client().get().metadata().kind().list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<KindMetaData> executeList( @Nonnull Object o, @Nullable Map<String, Object> map, @Nullable Locale locale, int start, int length )
            throws IOException
    {
        CtoolkitAgentRequest<KindMetaDataCollection> request = ( CtoolkitAgentRequest<KindMetaDataCollection> ) o;

        fill( request, map, locale );
        return request.execute().getItems();
    }
}
