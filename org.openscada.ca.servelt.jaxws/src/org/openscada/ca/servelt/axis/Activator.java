package org.openscada.ca.servelt.axis;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.utils.osgi.jaxws.JaxWsExporter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private ConfigurationAdministratorExportImpl service;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.service = new ConfigurationAdministratorExportImpl ( context );

        final Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();
        properties.put ( JaxWsExporter.EXPORT_ENABLED, Boolean.TRUE );
        properties.put ( Constants.SERVICE_PID, "test1" );
        context.registerService ( ConfigurationAdministratorExport.class.getName (), this.service, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.service.dispose ();
    }

}
