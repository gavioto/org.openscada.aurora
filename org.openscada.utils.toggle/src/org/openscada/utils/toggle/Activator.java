package org.openscada.utils.toggle;

import java.util.Properties;

import org.openscada.utils.toggle.internal.ToggleServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{

    private ToggleServiceImpl service;

    private ServiceRegistration registration;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( BundleContext context ) throws Exception
    {
        service = new ToggleServiceImpl ();
        Properties props = new Properties ();
        registration = context.registerService ( new String[] { ToggleService.class.getCanonicalName () }, service, props );
        service.start ();
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( BundleContext context ) throws Exception
    {
        service.stop ();
        registration.unregister ();
    }
}
