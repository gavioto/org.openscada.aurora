package org.openscada.ca.common;

import java.util.concurrent.ExecutorService;

import org.openscada.ca.ConfigurationEvent;
import org.openscada.ca.ConfigurationListener;
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

    public ListenerTracker ( final BundleContext context, final ExecutorService executor )
    {
        this.listenerTracker = new ServiceTracker ( context, ConfigurationListener.class.getName (), null );
        this.executor = executor;
    }

    public synchronized void open ()
    {
        this.listenerTracker.open ();
    }

    public synchronized void close ()
    {
        this.listenerTracker.close ();
    }

    public synchronized void fireEvent ( final ConfigurationEvent configurationEvent )
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                logger.debug ( "Fire configuration event: {}", configurationEvent );
                final Object[] services = ListenerTracker.this.listenerTracker.getServices ();
                if ( services != null )
                {
                    for ( final Object o : services )
                    {
                        if ( o instanceof ConfigurationListener )
                        {
                            final ConfigurationListener listener = (ConfigurationListener)o;
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

    public synchronized void fireEvent ( final FactoryEvent factoryEvent )
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                logger.debug ( "Fire factory event: {}", factoryEvent );
                final Object[] services = ListenerTracker.this.listenerTracker.getServices ();
                if ( services != null )
                {
                    for ( final Object o : services )
                    {
                        if ( o instanceof ConfigurationListener )
                        {
                            final ConfigurationListener listener = (ConfigurationListener)o;
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
