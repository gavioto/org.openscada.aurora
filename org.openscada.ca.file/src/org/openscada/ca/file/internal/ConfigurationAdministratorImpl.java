package org.openscada.ca.file.internal;

import java.io.File;

import org.openscada.ca.common.AbstractConfigurationAdministratorImpl;
import org.openscada.ca.common.Storage;
import org.osgi.framework.BundleContext;

public class ConfigurationAdministratorImpl extends AbstractConfigurationAdministratorImpl
{

    private final File root;

    public ConfigurationAdministratorImpl ( final BundleContext context, final File root )
    {
        super ( context );
        this.root = root;
    }

    @Override
    protected synchronized Storage createStorage ( final String factoryId )
    {
        final FileStorage storage = new FileStorage ( factoryId, this.root );
        return storage;
    }

}
