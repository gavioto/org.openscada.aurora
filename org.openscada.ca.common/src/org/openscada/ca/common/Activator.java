package org.openscada.ca.common;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    private ConfigurationAdministratorImpl service;

    public void start ( final BundleContext context ) throws Exception
    {
        this.service = new ConfigurationAdministratorImpl ( context );
        this.service.start ();

        final Dictionary<String, String> properties = new Hashtable<String, String> ();

        context.registerService ( ConfigurationAdministrator.class.getName (), this.service, properties );
    }

    public void stop ( final BundleContext context ) throws Exception
    {
        this.service.stop ();
    }

}
