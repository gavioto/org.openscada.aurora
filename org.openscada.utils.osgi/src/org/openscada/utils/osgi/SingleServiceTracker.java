/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.utils.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
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

    private ServiceReference currentRef;

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
            logger.warn ( "Should be null by the tracker#close call" );
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
            // take the first we have
            this.currentService = service;
            this.currentRef = reference;

            notifyService ( this.currentRef, this.currentService );
        }
        else if ( isHigher ( reference, this.currentRef ) )
        {
            this.currentRef = reference;
            this.currentService = service;

            notifyService ( this.currentRef, this.currentService );
        }

        return service;
    }

    private boolean isHigher ( final ServiceReference reference, final ServiceReference currentRef )
    {
        int ref1 = 0;
        int ref2 = 0;

        try
        {
            ref1 = (Integer)reference.getProperty ( Constants.SERVICE_RANKING );
        }
        catch ( final Exception e )
        {
        }

        try
        {
            ref2 = (Integer)currentRef.getProperty ( Constants.SERVICE_RANKING );
        }
        catch ( final Exception e )
        {
        }

        return ref1 > ref2;
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
                this.currentRef = ref;
                logger.info ( "Setting next service: {} / {}", new Object[] { ref, this.currentService } );
                notifyService ( this.currentRef, this.currentService );
            }
            else
            {
                logger.info ( "no more services left" );
                this.currentService = null;
                this.currentRef = null;
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
