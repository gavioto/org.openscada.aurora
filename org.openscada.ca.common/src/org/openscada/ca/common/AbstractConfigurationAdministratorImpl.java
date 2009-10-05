package org.openscada.ca.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
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

public abstract class AbstractConfigurationAdministratorImpl implements ConfigurationAdministrator
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractConfigurationAdministratorImpl.class );

    private final BundleContext context;

    protected final ExecutorService executor;

    private final ListenerTracker listenerTracker;

    private final ServiceTracker serviceListener;

    private final Map<String, FactoryHandler> factories = new HashMap<String, FactoryHandler> ();

    private final Map<ConfigurationFactory, SelfManagedConfigurationFactory> wrappers = new HashMap<ConfigurationFactory, SelfManagedConfigurationFactory> ();

    private final ServiceTracker serviceListener2;

    public AbstractConfigurationAdministratorImpl ( final BundleContext context )
    {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "Configuration Administrator" ) );

        this.listenerTracker = new ListenerTracker ( context );

        this.serviceListener = new ServiceTracker ( context, SelfManagedConfigurationFactory.class.getName (), new ServiceTrackerCustomizer () {

            public void removedService ( final ServiceReference reference, final Object service )
            {
                AbstractConfigurationAdministratorImpl.this.removedService ( reference, service );
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
            }

            public Object addingService ( final ServiceReference reference )
            {
                return AbstractConfigurationAdministratorImpl.this.addingService ( reference );
            }
        } );

        this.serviceListener2 = new ServiceTracker ( context, ConfigurationFactory.class.getName (), new ServiceTrackerCustomizer () {

            public void removedService ( final ServiceReference reference, final Object service )
            {
                AbstractConfigurationAdministratorImpl.this.removedService ( reference, service );
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
            }

            public Object addingService ( final ServiceReference reference )
            {
                return AbstractConfigurationAdministratorImpl.this.addingService ( reference );
            }
        } );
    }

    protected void removedService ( final ServiceReference reference, final Object service )
    {
        final String factoryId = getFactoryId ( reference );

        if ( factoryId == null )
        {
            logger.warn ( "Factory does not have a factoryId: {}", reference );
            return;
        }

        if ( service instanceof SelfManagedConfigurationFactory )
        {
            removeFactory ( factoryId, (SelfManagedConfigurationFactory)service );
        }
        else if ( service instanceof ConfigurationFactory )
        {
            removeFactory ( factoryId, (ConfigurationFactory)service );
        }
    }

    private synchronized void removeFactory ( final String factoryId, final ConfigurationFactory service )
    {
        logger.info ( "Removing wrapper for: {}", factoryId );

        final SelfManagedConfigurationFactory wrapper = this.wrappers.remove ( service );
        removeFactory ( factoryId, wrapper );

    }

    private synchronized void removeFactory ( final String factoryId, final SelfManagedConfigurationFactory service )
    {
        final FactoryHandler handler = this.factories.get ( factoryId );

        if ( handler != null )
        {
            handler.removeService ( service );
        }
    }

    protected Object addingService ( final ServiceReference reference )
    {
        final String factoryId = getFactoryId ( reference );

        if ( factoryId == null )
        {
            logger.warn ( "Factory does not have a factoryId: {}", reference );
            return null;
        }

        try
        {
            final Object o = this.context.getService ( reference );

            if ( o instanceof SelfManagedConfigurationFactory )
            {
                final SelfManagedConfigurationFactory service = (SelfManagedConfigurationFactory)o;
                addFactory ( factoryId, service );
                return service;
            }
            else if ( o instanceof ConfigurationFactory )
            {
                addFactory ( factoryId, (ConfigurationFactory)o );
                return o;
            }

            // ups
            this.context.ungetService ( reference );
            return null;
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to add service instance", e );
            this.context.ungetService ( reference );
            return null;
        }
    }

    protected abstract Storage createStorage ( String factoryId );

    private synchronized void addFactory ( final String factoryId, final ConfigurationFactory service )
    {
        logger.info ( "Adding wrapper for: {}", factoryId );

        final SelfManagedConfigurationFactory wrapper = new Wrapper ( factoryId, service, createStorage ( factoryId ) );
        this.wrappers.put ( service, wrapper );
        addFactory ( factoryId, wrapper );
    }

    private synchronized void addFactory ( final String factoryId, final SelfManagedConfigurationFactory service )
    {
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
        this.serviceListener2.open ();
    }

    public synchronized void stop ()
    {
        this.serviceListener.close ();
        this.serviceListener2.close ();
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
