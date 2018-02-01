package org.ctoolkit.restapi.client.adapter;

import com.google.api.client.util.DateTime;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import java.util.Date;

/**
 * The bidirectional Google API client {@link DateTime} vs. standard java {@link Date} converter.
 * If needed configure your orika mapper:
 * <pre>
 *
 * import ma.glasnost.orika.converter.ConverterFactory;
 * import ma.glasnost.orika.MapperFactory;
 * ..
 *
 *    &#64;Inject
 *    MapperFactory factory;
 *
 *    ..
 *    ConverterFactory converterFactory = factory.getConverterFactory();
 *    converterFactory.registerConverter( new DateTimeToDateConverter() );
 * </pre>
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public class DateTimeToDateConverter
        extends BidirectionalConverter<DateTime, Date>
{
    @Override
    public Date convertTo( DateTime source, Type<Date> destinationType, MappingContext context )
    {
        return new Date( source.getValue() );
    }

    @Override
    public DateTime convertFrom( Date source, Type<DateTime> destinationType, MappingContext context )
    {
        return new DateTime( source );
    }
}
