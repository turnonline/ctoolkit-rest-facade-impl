package org.ctoolkit.restapi.client.appengine.orika;

import ma.glasnost.orika.MapperFactory;

/**
 * The class to help collect {@link MapperFactory} configuration from dependencies
 * if contribution is being required and to make sure that only single shareable instance is being used.
 * Used by injection framework responsible for proper factory instantiation.
 *
 * @author <a href="mailto:aurel.medvegy@ctoolkit.org">Aurel Medvegy</a>
 */
public interface BeanMapperConfig
{
    void config( MapperFactory factory );
}
