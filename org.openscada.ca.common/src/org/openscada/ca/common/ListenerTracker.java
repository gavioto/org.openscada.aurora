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
        this.executor = new ThreadPoolExecutor ( 0, 1, 1000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable> (), new NamedThreadFactory ( "Configuration / Listener Tracker" ) );
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
