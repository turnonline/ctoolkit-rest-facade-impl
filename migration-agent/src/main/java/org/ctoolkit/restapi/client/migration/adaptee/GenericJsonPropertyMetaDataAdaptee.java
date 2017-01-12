package org.ctoolkit.restapi.client.migration.adaptee;

import org.ctoolkit.api.migration.CtoolkitAgent;
import org.ctoolkit.api.migration.CtoolkitAgentRequest;
import org.ctoolkit.api.migration.model.PropertyMetaData;
import org.ctoolkit.api.migration.model.PropertyMetaDataCollection;
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
public class GenericJsonPropertyMetaDataAdaptee
        extends AbstractGoogleClientAdaptee<Provider<CtoolkitAgent>, PropertyMetaData>
        implements ListExecutorAdaptee<PropertyMetaData>
{
    @Inject
    public GenericJsonPropertyMetaDataAdaptee( Provider<CtoolkitAgent> client )
    {
        super( client );
    }

    @Override
    public Object prepareList( @Nullable Identifier parentKey ) throws IOException
    {
        return client().get().metadata().kind().property().list( parentKey.getString() );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public List<PropertyMetaData> executeList( @Nonnull Object o, @Nullable Map<String, Object> map, @Nullable Locale locale, int start, int length )
            throws IOException
    {
        CtoolkitAgentRequest<PropertyMetaDataCollection> request = ( CtoolkitAgentRequest<PropertyMetaDataCollection> ) o;

        fill( request, map, locale );
        return request.execute().getItems();
    }
}
