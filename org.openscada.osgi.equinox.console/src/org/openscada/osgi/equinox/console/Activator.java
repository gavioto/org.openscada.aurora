package org.openscada.osgi.equinox.console;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    private ConsoleServerImpl server;

    private static BundleContext context;

    public static BundleContext getDefault ()
    {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.server = new ConsoleServerImpl ( 1502 );
        Activator.context = context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( BundleContext context ) throws Exception
    {
        this.server.dispose ();
        context = null;
    }

}
