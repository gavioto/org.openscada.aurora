package org.openscada.utils.osgi.jaxws;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    private EndpointExporter exporter;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.exporter = new EndpointExporter ( context, System.getProperty ( "org.openscada.utils.osgi.jaxws.baseAddress", "http://localhost:9999" ) );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.exporter.dispose ();
    }

}
