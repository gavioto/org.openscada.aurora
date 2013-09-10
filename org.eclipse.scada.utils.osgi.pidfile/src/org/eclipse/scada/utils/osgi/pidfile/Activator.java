/*******************************************************************************
 * Copyright (c) 2006, 2011 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.utils.osgi.pidfile;

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
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        Activator.context = bundleContext;

        this.fl = new FrameworkListener () {
            @Override
            public void frameworkEvent ( final FrameworkEvent event )
            {
                if ( event.getType () == FrameworkEvent.STARTED )
                {
                    try
                    {
                        createPidFile ( getPidFilePath () );
                    }
                    catch ( final Throwable th )
                    {
                        logger.error ( "a unexpected error happened", th );
                    }
                }
            }
        };
        bundleContext.addFrameworkListener ( this.fl );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        if ( this.fl != null )
        {
            bundleContext.removeFrameworkListener ( this.fl );
        }
        this.fl = null;
        Activator.context = null;
    }

    private void createPidFile ( final String pidFilePath )
    {
        final File file = new File ( pidFilePath );
        final File parent = new File ( file.getParent () );
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
            final String string = ManagementFactory.getRuntimeMXBean ().getName ();
            final String pid = string.contains ( "@" ) ? string.split ( "@" )[0] : string;
            try
            {
                final FileWriter fw = new FileWriter ( file );
                fw.write ( pid );
                fw.close ();
            }
            catch ( final IOException e )
            {
                logger.error ( "could not create pid file at '{}'", file.getAbsolutePath () );
            }
            file.deleteOnExit ();
        }
    }

    private String getPidFilePath ()
    {
        final File pidFile = new File ( new File ( System.getProperty ( "user.home" ), ".scada" ), "scada.pid" );
        return System.getProperty ( "org.eclipse.scada.utils.osgi.pidfile", pidFile.getAbsolutePath () );
    }
}
