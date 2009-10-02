package org.openscada.ca.file.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationData;
import org.openscada.ca.StorageListener;
import org.openscada.ca.common.AbstractConfigurationAdministrator;
import org.openscada.ca.common.ConfigurationDataImpl;
import org.openscada.ca.common.ConfigurationImpl;
import org.openscada.ca.common.Storage;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationAdminImpl extends AbstractConfigurationAdministrator
{
    private final static class DataFilenameFilter implements FilenameFilter
    {
        public boolean accept ( final File dir, final String name )
        {
            if ( ".meta".equals ( name ) )
            {
                return false;
            }
            return true;
        }
    }

    private class InternalStorage implements Storage
    {
        private final String factoryId;

        private final Set<StorageListener> listeners = new HashSet<StorageListener> ();

        private final Configuration[] configurations;

        private final ExecutorService executor;

        public InternalStorage ( final ExecutorService executor, final String factoryId, final Configuration[] initialConfigurations )
        {
            this.executor = executor;
            this.factoryId = factoryId;
            this.configurations = initialConfigurations;
        }

        public Future<Configuration> store ( final String configurationId, final Map<String, String> properties )
        {
            return invokeStore ( this, this.factoryId, configurationId, properties );
        }

        public Future<Configuration> delete ( final String configurationId )
        {
            return invokeDelete ( this, this.factoryId, configurationId );
        }

        public synchronized void addConfigurationListener ( final StorageListener listener )
        {
            this.listeners.add ( listener );
            listener.configurationUpdate ( this.configurations, null );
        }

        public synchronized void removeConfigurationListener ( final StorageListener listener )
        {
            this.listeners.remove ( listener );
        }

        public synchronized void changeConfiguration ( final ConfigurationData[] addedOrChanged, final String[] deleted )
        {
            for ( final StorageListener listener : this.listeners )
            {
                this.executor.execute ( new Runnable () {

                    public void run ()
                    {
                        listener.configurationUpdate ( addedOrChanged, deleted );
                    }
                } );
            }
        }
    }

    private static final String META_FILE = ".meta";

    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationAdminImpl.class );

    private static final String STORE = "openscadaConfigStore";

    private final BundleContext context;

    private final File root;

    public ConfigurationAdminImpl ( final BundleContext context ) throws InvalidSyntaxException
    {
        super ( context );
        this.context = context;
        this.root = initRoot ();
    }

    private File initRoot ()
    {
        final File file = this.context.getDataFile ( STORE );
        if ( file != null )
        {
            if ( !file.exists () )
            {
                logger.info ( "Storage root does not exist: " + file.getName () );
                file.mkdir ();
            }
            if ( file.isDirectory () )
            {
                return file;
            }
            else
            {
                logger.warn ( "File exists but is not a directory: " + file.getName () );
            }
        }
        else
        {
            logger.warn ( "No file system support" );
        }
        return null;
    }

    @Override
    public synchronized void start ()
    {
        super.start ();
        performInitialLoad ();
    }

    protected void performInitialLoad ()
    {
        if ( this.root == null )
        {
            logger.warn ( "No root found" );
            return;
        }

        for ( final String pathName : this.root.list () )
        {
            final File path = new File ( this.root, pathName );
            if ( path.isDirectory () )
            {
                logger.debug ( "Checking for path: " + path.getName () );
                final String factoryId = detectFactory ( path );
                if ( factoryId != null )
                {
                    logger.debug ( String.format ( "Path %s is a possible factory (%s). Adding...", path.getName (), factoryId ) );
                    performLoadFactory ( factoryId );
                }
            }
        }
    }

    private String detectFactory ( final File path )
    {
        final File meta = new File ( path, META_FILE );
        final Properties p = new Properties ();
        FileInputStream stream = null;
        try
        {
            stream = new FileInputStream ( meta );
            p.load ( stream );
        }
        catch ( final Exception e )
        {
            return null;
        }
        finally
        {
            if ( stream != null )
            {
                try
                {
                    stream.close ();
                }
                catch ( final IOException e )
                {
                    logger.warn ( "Failed to close stream", e );
                }
            }
        }
        return p.getProperty ( "id" );
    }

    protected void performLoadFactory ( final String factoryId )
    {
        if ( this.root == null )
        {
            logger.warn ( "No root found" );
            return;
        }

        final File path = getFactoryPath ( factoryId );
        loadAll ( path, factoryId );
    }

    private void createStore ( final File factoryRoot, final String factoryId )
    {
        if ( !factoryRoot.mkdir () )
        {
            logger.warn ( "Failed to create store: " + factoryRoot );
            return;
        }
        final File meta = new File ( factoryRoot, META_FILE );
        final Properties p = new Properties ();
        p.put ( "id", factoryId );
        FileOutputStream stream = null;
        try
        {
            stream = new FileOutputStream ( meta );

            logger.debug ( "Creating new store: {}", factoryRoot );
            p.store ( stream, "" );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to initialize store: " + factoryRoot );
        }
        finally
        {
            if ( stream != null )
            {
                try
                {
                    stream.close ();
                }
                catch ( final IOException e )
                {
                    logger.warn ( "Failed to close stream", e );
                }
            }
        }
    }

    private void loadAll ( final File configurationRoot, final String factoryId )
    {
        logger.info ( "Loading from: " + configurationRoot.getName () );

        final List<ConfigurationDataImpl> configurations = new LinkedList<ConfigurationDataImpl> ();

        for ( final File file : configurationRoot.listFiles ( new DataFilenameFilter () ) )
        {
            logger.info ( "Loading file: " + file.getName () );
            final ConfigurationDataImpl cfg = loadConfiguration ( factoryId, file );

            if ( cfg != null )
            {
                configurations.add ( cfg );
            }
        }

        // FIXME: fucked up
        // addStoredFactory ( factoryId, new InternalStorage ( this.executor, factoryId, configurations.toArray ( new ConfigurationImpl[0] ) ) );
    }

    private NotifyFuture<Configuration> invokeStore ( final InternalStorage storage, final String factoryId, final String configurationId, final Map<String, String> properties )
    {
        final ConfigurationFuture future = new ConfigurationFuture ();
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                try
                {
                    performStoreConfiguration ( factoryId, storage, configurationId, properties, future );
                }
                catch ( final Throwable e )
                {
                    future.setError ( e );
                }
            }
        } );
        return future;
    }

    private NotifyFuture<Configuration> invokeDelete ( final InternalStorage storage, final String factoryId, final String configurationId )
    {
        final ConfigurationFuture future = new ConfigurationFuture ();
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                try
                {
                    performDeleteConfiguration ( storage, factoryId, configurationId, future );
                }
                catch ( final Throwable e )
                {
                    future.setError ( e );
                }
            }
        } );
        return future;
    }

    private ConfigurationDataImpl loadConfiguration ( final String factoryId, final File file )
    {
        try
        {
            final Properties p = new Properties ();

            final FileInputStream stream = new FileInputStream ( file );
            try
            {
                p.load ( stream );
            }
            finally
            {
                stream.close ();
            }

            final Map<String, String> result = new HashMap<String, String> ();
            for ( final Entry<Object, Object> entry : p.entrySet () )
            {
                result.put ( entry.getKey ().toString (), entry.getValue ().toString () );
            }
            final String id = result.get ( "id" );
            if ( id == null )
            {
                return null;
            }
            return new ConfigurationDataImpl ( id, result );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to load" );
            return null;
        }
    }

    private String getPath ( final String factoryId )
    {
        return encode ( factoryId );
    }

    private String encode ( String path )
    {
        path = path.replace ( "_", "__" );
        path = path.replaceAll ( "[^a-zA-Z0-9_-]", "_" );
        return path;
    }

    protected void performStoreConfiguration ( final String factoryId, final InternalStorage storage, final String configurationId, final Map<String, String> properties, final ConfigurationFuture future ) throws FileNotFoundException, IOException
    {
        if ( this.root == null )
        {
            logger.warn ( "Unable to store : no root" );
            return;
        }

        final File path = getFactoryPath ( factoryId );

        final File file = new File ( path, encode ( configurationId ) );

        logger.info ( String.format ( "Storing %s to %s", configurationId, file ) );

        final Properties p = new Properties ();
        p.putAll ( properties );
        p.put ( "id", configurationId );

        final FileOutputStream stream = new FileOutputStream ( file );
        try
        {
            logger.debug ( "Storing {}/{} -> {}", new Object[] { factoryId, configurationId, properties } );
            p.store ( stream, "" );
        }
        finally
        {
            stream.close ();
        }

        // notify the abstract service from our content change
        // changeConfiguration ( factoryId, configurationId, properties, future );
        storage.changeConfiguration ( new ConfigurationData[] { new ConfigurationDataImpl ( configurationId, properties ) }, null );
    }

    private File getFactoryPath ( final String factoryId )
    {
        final File path = new File ( this.root, getPath ( factoryId ) );
        if ( !path.exists () )
        {
            logger.info ( String.format ( "Store for factory (%s) does not exist", factoryId ) );
            createStore ( path, factoryId );
        }
        return path;
    }

    protected void performDeleteConfiguration ( final InternalStorage storage, final String factoryId, final String configurationId, final ConfigurationFuture future )
    {
        final File path = getFactoryPath ( factoryId );

        final File file = new File ( path, encode ( configurationId ) );

        logger.info ( "Deleting {}", configurationId );

        if ( !file.delete () )
        {
            logger.info ( "Failed to delete: {}", file );
        }

        // notify the abstract service from our content change
        changeConfiguration ( factoryId, configurationId, null, future );

    }

    @Override
    protected Storage performCreateStorage ( final String factoryId ) throws Exception
    {
        return new InternalStorage ( this.executor, factoryId, new ConfigurationImpl[0] );
    }
}
