package org.openscada.ca.file;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.common.AbstractConfigurationAdminImpl;
import org.openscada.ca.file.internal.ConfigurationAdminImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private AbstractConfigurationAdminImpl service;

    private ServiceRegistration handle;

    public void start ( final BundleContext context ) throws Exception
    {
        this.service = new ConfigurationAdminImpl ( context );

        this.service.start ();

        final Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "An OpenSCADA CA File Implementation" );

        this.handle = context.registerService ( ConfigurationAdministrator.class.getName (), this.service, properties );
    }

    public void stop ( final BundleContext context ) throws Exception
    {
        this.handle.unregister ();
        this.handle = null;

        this.service.dispose ();
        this.service = null;
    }

}
