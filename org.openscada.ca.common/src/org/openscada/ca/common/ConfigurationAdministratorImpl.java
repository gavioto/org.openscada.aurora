package org.openscada.ca.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.Factory;
import org.openscada.ca.FactoryNotFoundException;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationAdministratorImpl implements ConfigurationAdministrator
{
    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationAdministratorImpl.class );

    private final BundleContext context;

    protected final ExecutorService executor;

    private final ListenerTracker listenerTracker;

    private final ServiceTracker serviceListener;

    private final Map<String, FactoryHandler> factories = new HashMap<String, FactoryHandler> ();

    public ConfigurationAdministratorImpl ( final BundleContext context )
    {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "Configuration Administrator" ) );

        this.listenerTracker = new ListenerTracker ( context );

        this.serviceListener = new ServiceTracker ( context, SelfManagedConfigurationFactory.class.getName (), new ServiceTrackerCustomizer () {

            public void removedService ( final ServiceReference reference, final Object service )
            {
                ConfigurationAdministratorImpl.this.removedService ( reference, service );
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
            }

            public Object addingService ( final ServiceReference reference )
            {
                return ConfigurationAdministratorImpl.this.addingService ( reference );
            }
        } );
    }

    protected synchronized void removedService ( final ServiceReference reference, final Object service )
    {
        final String factoryId = getFactoryId ( reference );

        if ( factoryId == null )
        {
            logger.warn ( "Factory does not have a factoryId: {}", reference );
            return;
        }

        final FactoryHandler handler = this.factories.get ( factoryId );
        if ( handler != null )
        {
            handler.removeService ( (SelfManagedConfigurationFactory)service );
        }
    }

    protected synchronized Object addingService ( final ServiceReference reference )
    {
        final String factoryId = getFactoryId ( reference );

        if ( factoryId == null )
        {
            logger.warn ( "Factory does not have a factoryId: {}", reference );
            return null;
        }

        try
        {
            final SelfManagedConfigurationFactory service = (SelfManagedConfigurationFactory)this.context.getService ( reference );

            FactoryHandler handler;
            if ( this.factories.containsKey ( factoryId ) )
            {
                handler = this.factories.get ( factoryId );
            }
            else
            {
                handler = new FactoryHandler ( factoryId );
                this.factories.put ( factoryId, handler );
            }

            handler.addService ( service );
            return service;
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to add service instance", e );
            this.context.ungetService ( reference );
            return null;
        }
    }

    private String getFactoryId ( final ServiceReference reference )
    {
        final Object o = reference.getProperty ( FACTORY_ID );
        if ( o instanceof String )
        {
            return (String)o;
        }
        return null;
    }

    public synchronized void start ()
    {
        this.listenerTracker.open ();
        this.serviceListener.open ();
    }

    public synchronized void stop ()
    {
        this.serviceListener.close ();
        this.listenerTracker.close ();
    }

    public Future<Configuration> createConfiguration ( final String factoryId, final String configurationId, final Map<String, String> properties )
    {
        final FactoryHandler handler = getFactory ( factoryId );

        if ( handler == null )
        {
            return new InstantErrorFuture<Configuration> ( new FactoryNotFoundException ( factoryId ).fillInStackTrace () );
        }

        return handler.createConfiguration ( configurationId, properties );
    }

    public Future<Configuration> updateConfiguration ( final String factoryId, final String configurationId, final Map<String, String> properties )
    {
        final FactoryHandler handler = getFactory ( factoryId );

        if ( handler == null )
        {
            return new InstantErrorFuture<Configuration> ( new FactoryNotFoundException ( factoryId ).fillInStackTrace () );
        }

        return handler.updateConfiguration ( configurationId, properties );
    }

    public Future<Configuration> deleteConfiguration ( final String factoryId, final String configurationId )
    {
        final FactoryHandler handler = getFactory ( factoryId );

        if ( handler == null )
        {
            return new InstantErrorFuture<Configuration> ( new FactoryNotFoundException ( factoryId ).fillInStackTrace () );
        }

        return handler.deleteConfiguration ( configurationId );
    }

    public Configuration getConfiguration ( final String factoryId, final String configurationId )
    {
        final FactoryHandler handler = getFactory ( factoryId );

        if ( handler != null )
        {
            return handler.getConfiguration ( configurationId );
        }

        return null;
    }

    public Configuration[] getConfigurations ( final String factoryId )
    {
        final FactoryHandler handler = getFactory ( factoryId );

        if ( handler != null )
        {
            return handler.getConfigurations ();
        }

        return null;
    }

    public synchronized FactoryHandler getFactory ( final String factoryId )
    {
        return this.factories.get ( factoryId );
    }

    public synchronized Factory[] getKnownFactories ()
    {
        return this.factories.values ().toArray ( new Factory[0] );
    }

}
