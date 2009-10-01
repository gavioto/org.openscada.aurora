package org.openscada.ca.testing;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private ConfigurationFactoryImpl service;

    private SelfManagedConfigurationFactoryImpl service2;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.service = new ConfigurationFactoryImpl ();
        this.service2 = new SelfManagedConfigurationFactoryImpl ( "testing.selfManaged.factory" );
        this.service2.start ();

        // add plain factory
        Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, "testing.factory" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "Testing Factory" );

        context.registerService ( ConfigurationFactory.class.getName (), this.service, properties );

        // add self managed factory

        properties = new Hashtable<Object, Object> ();
        properties.put ( ConfigurationAdministrator.FACTORY_ID, "testing.selfManaged.factory" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "Self Managed Testing Factory" );

        context.registerService ( SelfManagedConfigurationFactory.class.getName (), this.service2, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.service2.stop ();
        this.service2 = null;
    }

}
