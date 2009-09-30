package org.openscada.ca.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationEvent;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.ca.Factory;
import org.openscada.ca.FactoryEvent;
import org.openscada.ca.FactoryState;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfigurationAdminImpl implements ConfigurationAdministrator, ServiceListener
{

    private final static class ThreadFactoryImpl implements ThreadFactory
    {
        public Thread newThread ( final Runnable r )
        {
            final Thread t = new Thread ( r );
            t.setName ( "Abstract Configuration Admin Runner" );
            return t;
        }
    }

    private final static Logger logger = LoggerFactory.getLogger ( AbstractConfigurationAdminImpl.class );

    private final ExecutorService executor;

    private final BundleContext context;

    private final Map<String, FactoryImpl> factories = new HashMap<String, FactoryImpl> ();

    private final Map<ServiceReference, String> factoryMap = new HashMap<ServiceReference, String> ();

    private final ListenerTracker listenerTracker;

    public AbstractConfigurationAdminImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        this.context = context;

        this.executor = Executors.newFixedThreadPool ( 1, new ThreadFactoryImpl () );

        this.listenerTracker = new ListenerTracker ( context, this.executor );
        this.listenerTracker.open ();
    }

    public void start ()
    {
        this.executor.execute ( new Runnable () {
            public void run ()
            {
                performInitialLoad ();
            }
        } );
        this.executor.execute ( new Runnable () {
            public void run ()
            {
                try
                {
                    init ( AbstractConfigurationAdminImpl.this.context );
                }
                catch ( final InvalidSyntaxException e )
                {
                    logger.warn ( "Failed to initialize" );
                }
            }
        } );
    }

    protected abstract void performInitialLoad ();

    private void init ( final BundleContext context ) throws InvalidSyntaxException
    {
        synchronized ( this )
        {
            this.context.addServiceListener ( this, String.format ( "(%s=%s)", Constants.OBJECTCLASS, ConfigurationFactory.class.getName () ) );
            final ServiceReference[] refs = context.getServiceReferences ( ConfigurationFactory.class.getName (), null );
            if ( refs != null )
            {
                for ( final ServiceReference ref : refs )
                {
                    addService ( ref );
                }
            }
        }
    }

    public void dispose ()
    {
        this.listenerTracker.close ();
        this.context.removeServiceListener ( this );
    }

    public synchronized Configuration createConfiguration ( final String factoryId, final String configurationId, final Map<String, String> initialProperties )
    {
        final FactoryImpl factory = addFactory ( factoryId );
        final Configuration cfg = factory.createConfiguration ( configurationId, initialProperties );

        if ( factory.getService () != null )
        {
            setFactoryState ( factory, FactoryState.CONFIGURED );
        }
        else
        {
            setFactoryState ( factory, FactoryState.LOADED );
        }

        return cfg;
    }

    public Configuration[] getConfigurations ( final String factoryId )
    {
        synchronized ( this )
        {
            final Factory factory = this.factories.get ( factoryId );
            if ( factory != null )
            {
                return factory.getConfigurations ();
            }
            return null;
        }
    }

    public Factory getFactory ( final String factoryId )
    {
        synchronized ( this )
        {
            return this.factories.get ( factoryId );
        }
    }

    public Factory[] listKnownFactories ()
    {
        synchronized ( this )
        {
            return this.factories.values ().toArray ( new Factory[0] );
        }
    }

    public void serviceChanged ( final ServiceEvent event )
    {
        switch ( event.getType () )
        {
        case ServiceEvent.REGISTERED:
            addService ( event.getServiceReference () );
            break;
        case ServiceEvent.UNREGISTERING:
            removeService ( event.getServiceReference () );
            break;
        }
    }

    protected synchronized void removeService ( final ServiceReference serviceReference )
    {
        logger.debug ( "Removing service: " + serviceReference );

        final String factoryId = this.factoryMap.remove ( serviceReference );
        if ( factoryId != null )
        {
            final FactoryImpl factory = this.factories.get ( factoryId );
            if ( factory != null )
            {
                logger.debug ( "Update factory state" );
                unbindFactory ( factory );
            }
        }
    }

    /**
     * Add an existing configured factory
     * @param factoryId
     */
    public synchronized FactoryImpl addFactory ( final String factoryId )
    {
        FactoryImpl factory = this.factories.get ( factoryId );
        if ( factory == null )
        {
            factory = new FactoryImpl ( this, FactoryState.LOADED, factoryId, "", null );
            this.factories.put ( factoryId, factory );
            triggerLoadFactory ( factory );
        }
        return factory;
    }

    protected synchronized void addService ( final ServiceReference serviceReference )
    {
        final Object idO = serviceReference.getProperty ( ConfigurationAdministrator.FACTORY_ID );
        if ( ! ( idO instanceof String ) )
        {
            logger.error ( "Unable to use factory without factory id" );
            return;
        }
        final String factoryId = (String)idO;
        if ( factoryId == null )
        {
            return;
        }

        final Object o = this.context.getService ( serviceReference );
        if ( o instanceof ConfigurationFactory )
        {
            final ConfigurationFactory configurationFactory = (ConfigurationFactory)o;

            this.factoryMap.put ( serviceReference, factoryId );

            final Object descO = serviceReference.getProperty ( Constants.SERVICE_DESCRIPTION );
            final String desc;
            if ( descO != null )
            {
                desc = descO.toString ();
            }
            else
            {
                desc = null;
            }

            final FactoryImpl factory = this.factories.get ( factoryId );
            if ( factory == null )
            {
                final FactoryImpl newFactory = new FactoryImpl ( this, FactoryState.FOUND, factoryId, desc, configurationFactory );
                triggerLoadFactory ( newFactory );
                this.factories.put ( factoryId, newFactory );
            }
            else
            {
                this.executor.execute ( new Runnable () {

                    public void run ()
                    {
                        AbstractConfigurationAdminImpl.this.bindFactory ( factory, configurationFactory, desc );
                    }
                } );

            }
        }
    }

    protected synchronized void unbindFactory ( final FactoryImpl factory )
    {
        factory.setService ( null, null );
        setFactoryState ( factory, FactoryState.LOADED );
    }

    protected synchronized void bindFactory ( final FactoryImpl factory, final ConfigurationFactory configurationFactory, final String description )
    {
        factory.setService ( configurationFactory, description );
        setFactoryState ( factory, FactoryState.CONFIGURED );
    }

    private void triggerLoadFactory ( final FactoryImpl factory )
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                loadFactory ( factory );
            }
        } );
    }

    protected void loadFactory ( final FactoryImpl factory )
    {
        try
        {
            performLoadFactory ( factory );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to load factory", e );
        }
        if ( factory.getService () != null )
        {
            setFactoryState ( factory, FactoryState.CONFIGURED );
        }
        else
        {
            setFactoryState ( factory, FactoryState.LOADED );
        }
    }

    protected abstract void performLoadFactory ( FactoryImpl factory );

    private void setFactoryState ( final FactoryImpl factory, final FactoryState state )
    {
        factory.setState ( state );
        this.listenerTracker.fireEvent ( new FactoryEvent ( FactoryEvent.Type.STATE, factory.getId () ) );
    }

    public void performUpdateConfiguration ( final ConfigurationImpl configurationImpl )
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                storeConfiguration ( configurationImpl );
            }
        } );
    }

    public void performApplyConfiguration ( final ConfigurationImpl configurationImpl )
    {
        logger.debug ( "Schedule re-apply" );
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                applyConfiguration ( configurationImpl, configurationImpl.getData () );
            }
        } );
    }

    protected synchronized void storeConfiguration ( final ConfigurationImpl configurationImpl )
    {
        try
        {
            final Map<String, String> properties = configurationImpl.getData ();
            if ( properties != null )
            {
                performStoreConfiguration ( configurationImpl, properties );
            }
            else
            {
                performDeleteConfiguration ( configurationImpl );
            }
            applyConfiguration ( configurationImpl, properties );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to store", e );
        }
    }

    protected synchronized void applyConfiguration ( final ConfigurationImpl configurationImpl, final Map<String, String> properties )
    {
        final ConfigurationFactory service = configurationImpl.getFactory ().getService ();
        try
        {
            if ( properties != null )
            {
                if ( service != null )
                {
                    service.update ( configurationImpl.getId (), properties );
                }
                this.listenerTracker.fireEvent ( new ConfigurationEvent ( ConfigurationEvent.Type.MODIFIED, configurationImpl ) );

                configurationImpl.setApplied ();
            }
            else
            {
                if ( service != null )
                {
                    service.delete ( configurationImpl.getId () );
                }
                this.listenerTracker.fireEvent ( new ConfigurationEvent ( ConfigurationEvent.Type.REMOVED, configurationImpl ) );
                configurationImpl.getFactory ().deleteConfiguration ( configurationImpl.getId () );
            }
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to apply", e );

            configurationImpl.setApplyError ( e );
        }

    }

    protected abstract void performDeleteConfiguration ( final ConfigurationImpl configurationImpl );

    protected abstract void performStoreConfiguration ( final ConfigurationImpl configurationImpl, final Map<String, String> properties ) throws Exception;

    public synchronized void performPurge ( final FactoryImpl factoryImpl, final ConfigurationFactory service )
    {
        if ( factoryImpl.getService () != null )
        {
            factoryImpl.setState ( FactoryState.FOUND );
        }
        else
        {
            this.factories.remove ( factoryImpl.getId () );
            // FIXME: we should also check the service reference .. 
            // but since the factory has no service there should also
            // be no service reference .... SHOULD!
        }

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                performPurge ( factoryImpl );
                AbstractConfigurationAdminImpl.this.listenerTracker.fireEvent ( new FactoryEvent ( FactoryEvent.Type.PURGED, factoryImpl.getId () ) );
                if ( service != null )
                {
                    service.purge ();
                }
            }
        } );

    }

    public ListenerTracker getListenerTracker ()
    {
        return this.listenerTracker;
    }

    protected abstract void performPurge ( final FactoryImpl factoryImpl );

}
