package org.ctoolkit.restapi.client.migration;

import com.google.common.eventbus.EventBus;
import org.ctoolkit.restapi.client.googleapis.GoogleApiCredentialFactory;

import javax.annotation.Nonnull;

/**
 * @author <a href="mailto:pohorelec@comvai.com">Jozef Pohorelec</a>
 */
public class CtoolkitApiMigrationAgentGoogleApiCredentialFactory
        extends GoogleApiCredentialFactory
{
    public CtoolkitApiMigrationAgentGoogleApiCredentialFactory( @Nonnull Builder builder, @Nonnull EventBus eventBus )
    {
        super( builder, eventBus );
    }
}
