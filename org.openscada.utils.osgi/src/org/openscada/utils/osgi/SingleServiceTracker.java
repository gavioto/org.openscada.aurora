package org.openscada.utils.osgi;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class SingleServiceTracker
{
    private final static Logger logger = Logger.getLogger ( SingleServiceTracker.class );

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
        logger.info ( "Adding service: " + reference );

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
        if ( this.currentService == service )
        {
            final ServiceReference ref = this.tracker.getServiceReference ();
            if ( ref != null )
            {
                this.currentService = this.tracker.getService ( ref );
                logger.info ( "Setting next service: " + ref + "/" + this.currentService );
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

}
