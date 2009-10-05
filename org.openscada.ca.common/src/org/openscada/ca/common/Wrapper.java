package org.openscada.ca.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationAlreadyExistsException;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.ca.ConfigurationState;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.ca.StorageListener;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wrapper implements SelfManagedConfigurationFactory, StorageManager
{

    private final static Logger logger = LoggerFactory.getLogger ( Wrapper.class );

    private final String factoryId;

    private final ConfigurationFactory service;

    private final Storage storage;

    private final Set<StorageListener> listeners = new HashSet<StorageListener> ();

    private ExecutorService executor;

    private ExecutorService listenerExecutor;

    private final Map<String, ConfigurationImpl> configurations = new HashMap<String, ConfigurationImpl> ();

    public Wrapper ( final String factoryId, final ConfigurationFactory service, final Storage storage )
    {
        this.factoryId = factoryId;
        this.service = service;
        this.storage = storage;

        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "WrapperStorage/" + factoryId ) );
        this.listenerExecutor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "WrapperNotifier/" + factoryId ) );

        synchronized ( this )
        {
            storage.setStorageManager ( this );
        }
    }

    public synchronized void dispose ()
    {
        this.executor.shutdown ();
        this.executor = null;

        this.listenerExecutor.shutdown ();
        this.listenerExecutor = null;
    }

    public synchronized void addConfigurationListener ( final StorageListener listener )
    {
        this.listeners.add ( listener );
    }

    public synchronized void removeConfigurationListener ( final StorageListener listener )
    {
        this.listeners.remove ( listener );
    }

    public synchronized NotifyFuture<Configuration> create ( final String configurationId, final Map<String, String> properties )
    {
        final ConfigurationImpl cfg = this.configurations.get ( configurationId );
        if ( cfg != null )
        {
            return new InstantErrorFuture<Configuration> ( new ConfigurationAlreadyExistsException ( this.factoryId, configurationId ).fillInStackTrace () );
        }

        return invokeStore ( configurationId, properties );
    }

    public synchronized NotifyFuture<Configuration> update ( final String configurationId, final Map<String, String> properties )
    {
        final ConfigurationImpl cfg = this.configurations.get ( configurationId );
        if ( cfg == null )
        {
            return new InstantErrorFuture<Configuration> ( new ConfigurationNotFoundException ( this.factoryId, configurationId ).fillInStackTrace () );
        }

        return invokeStore ( configurationId, properties );
    }

    public synchronized NotifyFuture<Configuration> delete ( final String configurationId )
    {
        final ConfigurationImpl cfg = this.configurations.remove ( configurationId );
        if ( cfg == null )
        {
            return new InstantErrorFuture<Configuration> ( new ConfigurationNotFoundException ( this.factoryId, configurationId ).fillInStackTrace () );
        }

        return invokeDelete ( configurationId );
    }

    private synchronized ConfigurationFuture invokeStore ( final String configurationId, final Map<String, String> properties )
    {
        final ConfigurationFuture future = new ConfigurationFuture ();
        this.storage.store ( configurationId, properties, future );
        return future;
    }

    private synchronized ConfigurationFuture invokeDelete ( final String configurationId )
    {
        final ConfigurationFuture future = new ConfigurationFuture ();
        this.storage.delete ( configurationId, future );
        return future;
    }

    public synchronized void changeConfiguration ( final String configurationId, final Map<String, String> properties, final ConfigurationFuture future )
    {
        if ( this.service != null )
        {
            logger.info ( "Found bound service: {}", configurationId );
            updateConfiguration ( new ConfigurationImpl ( configurationId, this.factoryId, properties, ConfigurationState.APPLYING, null ) );

            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    handleApply ( configurationId, properties, future );
                }
            } );
        }
        else
        {
            logger.info ( "No service bound at the moment" );
            final ConfigurationImpl cfg = new ConfigurationImpl ( configurationId, this.factoryId, properties, ConfigurationState.AVAILABLE, null );
            updateConfiguration ( cfg );
            future.setResult ( cfg );
        }
    }

    private Future<?> updateConfiguration ( final ConfigurationImpl cfg )
    {
        if ( cfg.getData () == null )
        {
            this.configurations.remove ( cfg.getId () );
            return fireDeleted ( cfg.getId () );
        }
        else
        {
            this.configurations.put ( cfg.getId (), cfg );
            return fireAddedOrModified ( cfg );
        }
    }

    private Future<?> fireDeleted ( final String configurationId )
    {
        return fireListeners ( null, new String[] { configurationId } );
    }

    private Future<?> fireAddedOrModified ( final ConfigurationImpl cfg )
    {
        return fireListeners ( new ConfigurationImpl[] { cfg }, null );
    }

    private synchronized Future<?> fireListeners ( final ConfigurationImpl[] cfg, final String[] deleted )
    {
        final Set<StorageListener> listeners = new HashSet<StorageListener> ( this.listeners );

        return this.listenerExecutor.submit ( new Runnable () {

            public void run ()
            {
                for ( final StorageListener listener : listeners )
                {
                    listener.configurationUpdate ( cfg, deleted );
                }
            }
        } );
    }

    protected ConfigurationImpl applyConfiguration ( final String configurationId, final Map<String, String> properties, final ConfigurationFuture future )
    {
        final ConfigurationFactory factory;

        synchronized ( this )
        {
            factory = this.service;
            if ( factory == null )
            {
                logger.warn ( "Not bound to a factory" );
                return new ConfigurationImpl ( configurationId, this.factoryId, properties, ConfigurationState.AVAILABLE, null );
            }
        }

        try
        {
            if ( properties == null )
            {
                logger.info ( "Calling service delete: {} ...", configurationId );
                this.service.delete ( configurationId );
                logger.info ( "Calling service delete: {} ... done", configurationId );
            }
            else
            {
                logger.info ( "Calling service store: {} -> {} ...", new Object[] { configurationId, properties } );
                this.service.update ( configurationId, properties );
                logger.info ( "Calling service store: {} ... done", configurationId );
            }
            return new ConfigurationImpl ( configurationId, this.factoryId, properties, ConfigurationState.APPLIED, null );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to apply: {} -> {}", new Object[] { configurationId, properties }, e );

            return new ConfigurationImpl ( configurationId, this.factoryId, properties, ConfigurationState.ERROR, e );
        }
    }

    private void handleApply ( final String configurationId, final Map<String, String> properties, final ConfigurationFuture future )
    {
        final ConfigurationImpl cfg = Wrapper.this.applyConfiguration ( configurationId, properties, future );

        final Future<?> f = updateConfiguration ( cfg );

        try
        {
            f.get ();
        }
        catch ( final InterruptedException e )
        {
            future.setError ( e );
        }
        catch ( final ExecutionException e )
        {
            future.setError ( e );
        }

        if ( future != null )
        {
            future.setResult ( cfg );
        }
    }

    public Configuration getConfiguration ( final String configurationId )
    {
        return this.configurations.get ( configurationId );
    }

    public synchronized Configuration[] getConfigurations ()
    {
        return this.configurations.values ().toArray ( new Configuration[0] );
    }

}
