package org.openscada.ca.file.internal;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ca.common.ConfigurationFuture;
import org.openscada.ca.common.NamedThreadFactory;
import org.openscada.ca.common.Storage;
import org.openscada.ca.common.StorageManager;

public abstract class AbstractStorage implements Storage
{

    protected final ExecutorService executor;

    protected final String factoryId;

    protected volatile StorageManager manager;

    public AbstractStorage ( final String factoryId )
    {
        this.factoryId = factoryId;
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( "AbstractStorage/" + this.factoryId ) );
    }

    protected abstract void performStore ( final String configurationId, final Map<String, String> properties, final ConfigurationFuture future ) throws Exception;

    protected abstract void performDelete ( final String configurationId, final ConfigurationFuture future ) throws Exception;

    public synchronized void delete ( final String configurationId, final ConfigurationFuture future )
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                try
                {
                    AbstractStorage.this.performDelete ( configurationId, future );
                }
                catch ( final Exception e )
                {
                    future.setError ( e );
                }
            }
        } );
    }

    public synchronized void store ( final String configurationId, final Map<String, String> properties, final ConfigurationFuture future )
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                try
                {
                    AbstractStorage.this.performStore ( configurationId, properties, future );
                }
                catch ( final Exception e )
                {
                    future.setError ( e );
                }
            }
        } );
    }

    public void setStorageManager ( final StorageManager manager )
    {
        this.manager = manager;
    }

}