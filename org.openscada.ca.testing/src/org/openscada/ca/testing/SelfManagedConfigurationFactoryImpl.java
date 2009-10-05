package org.openscada.ca.testing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationAlreadyExistsException;
import org.openscada.ca.ConfigurationState;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.ca.StorageListener;
import org.openscada.ca.common.ConfigurationNotFoundException;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfManagedConfigurationFactoryImpl implements SelfManagedConfigurationFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( SelfManagedConfigurationFactoryImpl.class );

    private final Set<StorageListener> listeners = new HashSet<StorageListener> ();

    private final Map<String, ConfigurationImpl> configurations = new HashMap<String, ConfigurationImpl> ();

    private ExecutorService executor;

    private ExecutorService eventExecutor;

    private final String factoryId;

    public SelfManagedConfigurationFactoryImpl ( final String factoryId )
    {
        this.factoryId = factoryId;
    }

    public synchronized void start ()
    {
        logger.info ( "Starting factory" );

        if ( this.executor == null )
        {
            this.executor = Executors.newSingleThreadExecutor ();
        }

        this.eventExecutor = Executors.newSingleThreadExecutor ();
    }

    public void stop ()
    {
        final ExecutorService executor;

        logger.info ( "Stopping factory..." );

        synchronized ( this )
        {
            executor = this.executor;
            if ( this.executor != null )
            {
                this.executor = null;
            }
        }

        if ( executor != null )
        {
            executor.shutdown ();
        }

        this.eventExecutor.shutdown ();

        logger.info ( "Stopping factory...done" );
    }

    public synchronized void addConfigurationListener ( final StorageListener listener )
    {
        this.listeners.add ( listener );
    }

    public synchronized void removeConfigurationListener ( final StorageListener listener )
    {
        this.listeners.remove ( listener );
    }

    public synchronized NotifyFuture<Configuration> delete ( final String configurationId )
    {
        if ( this.executor == null )
        {
            return new InstantErrorFuture<Configuration> ( new IllegalStateException ( "Factory is stopped" ).fillInStackTrace () );
        }

        fireChange ( new ConfigurationImpl ( configurationId, this.factoryId, new HashMap<String, String> (), ConfigurationState.APPLYING, null ), false );

        final FutureTask<Configuration> task = new FutureTask<Configuration> ( new Callable<Configuration> () {

            public Configuration call () throws Exception
            {
                return SelfManagedConfigurationFactoryImpl.this.handleDelete ( configurationId );
            }
        } );

        this.executor.execute ( task );

        return task;
    }

    public synchronized NotifyFuture<Configuration> update ( final String configurationId, final Map<String, String> properties )
    {
        if ( this.executor == null )
        {
            return new InstantErrorFuture<Configuration> ( new IllegalStateException ( "Factory is stopped" ).fillInStackTrace () );
        }

        fireChange ( new ConfigurationImpl ( configurationId, this.factoryId, properties, ConfigurationState.APPLYING, null ), false );

        final FutureTask<Configuration> task = new FutureTask<Configuration> ( new Callable<Configuration> () {

            public Configuration call () throws Exception
            {
                return SelfManagedConfigurationFactoryImpl.this.handleUpdate ( configurationId, properties );
            }
        } );

        this.executor.execute ( task );

        return task;
    }

    public synchronized NotifyFuture<Configuration> create ( final String configurationId, final Map<String, String> properties )
    {
        if ( this.executor == null )
        {
            return new InstantErrorFuture<Configuration> ( new IllegalStateException ( "Factory is stopped" ).fillInStackTrace () );
        }

        fireChange ( new ConfigurationImpl ( configurationId, this.factoryId, properties, ConfigurationState.APPLYING, null ), false );

        final FutureTask<Configuration> task = new FutureTask<Configuration> ( new Callable<Configuration> () {

            public Configuration call () throws Exception
            {
                return SelfManagedConfigurationFactoryImpl.this.handleCreate ( configurationId, properties );
            }
        } );

        this.executor.execute ( task );

        return task;
    }

    private void fireChange ( final Configuration cfg, final boolean wait )
    {
        fireChange ( new Configuration[] { cfg }, null, wait );
    }

    private synchronized void fireChange ( final Configuration[] addedOrChanged, final String[] deleted, final boolean wait )
    {
        final Future<?> future = this.eventExecutor.submit ( new Runnable () {

            public void run ()
            {
                for ( final StorageListener listener : SelfManagedConfigurationFactoryImpl.this.listeners )
                {
                    listener.configurationUpdate ( addedOrChanged, deleted );
                }
            }
        } );
        try
        {
            if ( wait )
            {
                future.get ();
            }
        }
        catch ( final InterruptedException e )
        {
            Thread.currentThread ().interrupt ();
        }
        catch ( final ExecutionException e )
        {
        }
    }

    private ConfigurationImpl makeConfiguration ( final String configurationId, final Map<String, String> properties )
    {
        final ConfigurationImpl cfg;
        if ( properties.containsKey ( "error" ) )
        {
            cfg = new ConfigurationImpl ( configurationId, this.factoryId, properties, ConfigurationState.ERROR, new RuntimeException ( "Error property is set" ).fillInStackTrace () );
        }
        else
        {
            cfg = new ConfigurationImpl ( configurationId, this.factoryId, properties, ConfigurationState.APPLIED, null );
        }
        return cfg;
    }

    protected Configuration handleDelete ( final String configurationId ) throws ConfigurationNotFoundException
    {
        synchronized ( this.configurations )
        {
            final ConfigurationImpl cfg = this.configurations.remove ( configurationId );
            if ( cfg == null )
            {
                throw new ConfigurationNotFoundException ( this.factoryId, configurationId );
            }

            fireChange ( null, new String[] { configurationId }, true );

            return cfg;
        }
    }

    protected Configuration handleCreate ( final String configurationId, final Map<String, String> properties ) throws ConfigurationAlreadyExistsException, InterruptedException
    {
        synchronized ( this.configurations )
        {
            if ( this.configurations.containsKey ( configurationId ) )
            {
                throw new ConfigurationAlreadyExistsException ( this.factoryId, configurationId );
            }

            testSleep ( properties );

            final ConfigurationImpl cfg = makeConfiguration ( configurationId, properties );
            this.configurations.put ( configurationId, cfg );

            fireChange ( cfg, true );

            return cfg;
        }
    }

    protected Configuration handleUpdate ( final String configurationId, final Map<String, String> properties ) throws ConfigurationNotFoundException, InterruptedException
    {
        synchronized ( this.configurations )
        {
            if ( !this.configurations.containsKey ( configurationId ) )
            {
                throw new ConfigurationNotFoundException ( this.factoryId, configurationId );
            }

            testSleep ( properties );

            final ConfigurationImpl cfg = makeConfiguration ( configurationId, properties );
            this.configurations.put ( configurationId, cfg );
            fireChange ( cfg, true );

            return cfg;
        }
    }

    private void testSleep ( final Map<String, String> properties ) throws InterruptedException
    {
        final Integer sleep = getSleep ( properties );
        if ( sleep != null )
        {
            Thread.sleep ( sleep );
        }
    }

    private Integer getSleep ( final Map<String, String> properties )
    {
        try
        {
            final int i = Integer.parseInt ( properties.get ( "sleep" ) );
            if ( i > 0 )
            {
                return i;
            }
            return null;
        }
        catch ( final Throwable e )
        {
            return null;
        }
    }
}
