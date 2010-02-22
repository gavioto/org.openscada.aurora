/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.ca.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openscada.ca.ConfigurationAdministratorListener;
import org.openscada.ca.ConfigurationEvent;
import org.openscada.ca.FactoryEvent;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( ListenerTracker.class );

    private final ServiceTracker listenerTracker;

    private final ExecutorService executor;

    public ListenerTracker ( final BundleContext context )
    {
        this.listenerTracker = new ServiceTracker ( context, ConfigurationAdministratorListener.class.getName (), null );
        this.executor = new ThreadPoolExecutor ( 0, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable> () );
    }

    public void open ()
    {
        this.listenerTracker.open ();
    }

    public void close ()
    {
        this.listenerTracker.close ();
    }

    public void fireEvent ( final ConfigurationEvent configurationEvent )
    {
        logger.debug ( "Fire configuration event: {}", configurationEvent );

        final Object[] services = this.listenerTracker.getServices ();

        if ( services != null && services.length > 0 )
        {
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    if ( services != null )
                    {
                        for ( final Object o : services )
                        {
                            if ( o instanceof ConfigurationAdministratorListener )
                            {
                                final ConfigurationAdministratorListener listener = (ConfigurationAdministratorListener)o;
                                try
                                {
                                    listener.configurationEvent ( configurationEvent );
                                }
                                catch ( final Throwable e )
                                {
                                    logger.warn ( "Failed to handle listener", e );
                                }
                            }
                        }
                    }
                }
            } );
        }
    }

    public void fireEvent ( final FactoryEvent factoryEvent )
    {
        logger.debug ( "Fire factory event: {}", factoryEvent );

        final Object[] services = this.listenerTracker.getServices ();
        if ( services != null && services.length > 0 )
        {
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    if ( services != null )
                    {
                        for ( final Object o : services )
                        {
                            if ( o instanceof ConfigurationAdministratorListener )
                            {
                                final ConfigurationAdministratorListener listener = (ConfigurationAdministratorListener)o;
                                try
                                {
                                    listener.factoryEvent ( factoryEvent );
                                }
                                catch ( final Throwable e )
                                {
                                    logger.warn ( "Failed to handle listener", e );
                                }
                            }
                        }
                    }
                }
            } );
        }
    }
}
