package org.openscada.utils.osgi.pidfile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{
    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private static BundleContext context;

    static BundleContext getContext ()
    {
        return context;
    }

    private FrameworkListener fl;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        fl = new FrameworkListener () {
            @Override
            public void frameworkEvent ( FrameworkEvent event )
            {
                if ( event.getType () == FrameworkEvent.STARTED )
                {
                    createPidFile ( getPidFilePath () );
                }
            }
        };
        context.addFrameworkListener ( fl );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( BundleContext bundleContext ) throws Exception
    {
        context.removeFrameworkListener ( fl );
        fl = null;
        Activator.context = null;
    }

    private void createPidFile ( String pidFilePath )
    {
        File file = new File ( pidFilePath );
        File parent = new File ( file.getParent () );
        if ( !parent.exists () )
        {
            if ( !parent.mkdirs () )
            {
                logger.warn ( "could not create parent directory '{}' for pid file", parent.getAbsolutePath () );
            }
        }
        if ( parent.exists () )
        {
            if ( file.exists () )
            {
                if ( !file.delete () )
                {
                    logger.warn ( "could not remove old pidfile at '{}'", file.getAbsolutePath () );
                }
            }
            String string = ManagementFactory.getRuntimeMXBean ().getName ();
            String pid = string.contains ( "@" ) ? string.split ( "@" )[0] : string;
            try
            {
                FileWriter fw = new FileWriter ( file );
                fw.write ( pid );
                fw.close ();
            }
            catch ( IOException e )
            {
                logger.error ( "could not create pid file at '{}'", file.getAbsolutePath () );
            }
            file.deleteOnExit ();
        }
    }

    private String getPidFilePath ()
    {
        File pidFile = new File ( new File ( System.getProperty ( "user.home" ), ".openscada" ), "openscada.pid" );
        return System.getProperty ( "org.openscada.utils.osgi.pidfile", pidFile.getAbsolutePath () );
    }
}
