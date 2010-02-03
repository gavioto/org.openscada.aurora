package org.openscada.osgi.equinox.console;

import org.jibble.pircbot.PircBot;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{
    private PircBot bot;

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
        Activator.context = context;
        this.bot = new ConsoleBot ( "localhost", 6667 );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( BundleContext context ) throws Exception
    {
        this.bot.dispose ();
        context = null;
    }

}
