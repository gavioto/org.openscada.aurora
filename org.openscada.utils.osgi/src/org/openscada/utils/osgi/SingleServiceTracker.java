/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleServiceTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( SingleServiceTracker.class );

    private final class ServiceTrackerCustomizerImplementation implements ServiceTrackerCustomizer
    {
        public void removedService ( final ServiceReference reference, final Object service )
        {
            SingleServiceTracker.this.removedService ( reference, service );
        }

        public void modifiedService ( final ServiceReference reference, final Object service )
        {
            SingleServiceTracker.this.modifiedService ( reference, service );
        }

        public Object addingService ( final ServiceReference reference )
        {
            return SingleServiceTracker.this.addingService ( reference );
        }
    }

    private final ServiceTracker tracker;

    private final BundleContext context;

    private Object currentService;

    private final SingleServiceListener listener;

    public SingleServiceTracker ( final BundleContext context, final Filter filter, final SingleServiceListener listener )
    {
        this.context = context;
        this.listener = listener;
        this.tracker = new ServiceTracker ( context, filter, new ServiceTrackerCustomizerImplementation () );
    }

    public SingleServiceTracker ( final BundleContext context, final String clazz, final SingleServiceListener listener )
    {
        this.context = context;
        this.listener = listener;
        this.tracker = new ServiceTracker ( context, clazz, new ServiceTrackerCustomizerImplementation () );
    }

    /**
     * @see ServiceTracker#open()
     */
    public synchronized void open ()
    {
        this.tracker.open ();
    }

    /**
     * @see ServiceTracker#close()
     */
    public synchronized void close ()
    {
        this.tracker.close ();
        if ( this.currentService != null )
        {
            this.currentService = null;
            notifyService ( null, null );
        }
    }

    protected synchronized Object addingService ( final ServiceReference reference )
    {
        logger.info ( "Adding service: {}", reference );

        final Object service = this.context.getService ( reference );

        if ( this.currentService == null )
        {
            this.currentService = service;
            notifyService ( reference, this.currentService );
        }

        return service;
    }

    protected void modifiedService ( final ServiceReference reference, final Object service )
    {
        /* do nothing */
    }

    protected synchronized void removedService ( final ServiceReference reference, final Object service )
    {
        this.context.ungetService ( reference );

        if ( this.currentService == service )
        {
            final ServiceReference ref = this.tracker.getServiceReference ();
            if ( ref != null )
            {
                this.currentService = this.tracker.getService ( ref );
                logger.info ( "Setting next service: {} / {}", new Object[] { ref, this.currentService } );
                notifyService ( ref, this.currentService );
            }
            else
            {
                logger.info ( "no more services left" );
                this.currentService = null;
                notifyService ( null, null );
            }
        }
    }

    private void notifyService ( final ServiceReference reference, final Object service )
    {
        this.listener.serviceChange ( reference, service );
    }

    public Object waitForService ( final long timeout ) throws InterruptedException
    {
        return this.tracker.waitForService ( timeout );
    }

}
