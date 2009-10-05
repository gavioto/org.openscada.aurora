package org.openscada.ca.file.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openscada.ca.common.ConfigurationFuture;
import org.openscada.ca.common.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStorage extends AbstractStorage
{

    private final static Logger logger = LoggerFactory.getLogger ( FileStorage.class );

    private final File base;

    public FileStorage ( final String factoryId, final File root )
    {
        super ( factoryId );
        this.base = new File ( root, fileName ( factoryId ) );

        if ( !this.base.exists () )
        {
            logger.info ( "Creating base: {}", this.base );
            this.base.mkdirs ();
        }
    }

    @Override
    public void setStorageManager ( final StorageManager manager )
    {
        super.setStorageManager ( manager );

        load ();
    }

    private void load ()
    {
        logger.info ( "Loading all: {}", this.base );

        for ( final File file : this.base.listFiles () )
        {
            if ( file.canRead () && file.isFile () )
            {
                logger.info ( "Loading: {}", file );
                loadFile ( file );
            }
            else
            {
                logger.info ( "Ignoring: {}", file );
            }
        }
    }

    private void loadFile ( final File file )
    {
        FileInputStream stream = null;
        try
        {
            final String configurationId = URLDecoder.decode ( file.getName (), "UTF-8" );
            final Properties p = new Properties ();
            stream = new FileInputStream ( file );
            p.load ( stream );

            this.manager.changeConfiguration ( configurationId, convert ( p ), null );
        }
        catch ( final UnsupportedEncodingException e )
        {
            logger.warn ( "Failed to load", e );
            return;
        }
        catch ( final IOException e )
        {
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
                    logger.warn ( "Failed failing", e );
                }
            }
        }
    }

    private Map<String, String> convert ( final Properties p )
    {
        final Map<String, String> result = new HashMap<String, String> ();

        for ( final Map.Entry<Object, Object> entry : p.entrySet () )
        {
            result.put ( entry.getKey ().toString (), entry.getValue ().toString () );
        }

        return result;
    }

    private String fileName ( final String configurationId )
    {
        try
        {
            return URLEncoder.encode ( configurationId, "UTF-8" );
        }
        catch ( final UnsupportedEncodingException e )
        {
            return configurationId;
        }
    }

    @Override
    protected void performDelete ( final String configurationId, final ConfigurationFuture future )
    {
        final File file = new File ( this.base, fileName ( configurationId ) );
        if ( file.exists () )
        {
            if ( !file.delete () )
            {
                logger.warn ( "Failed to delete: {}", file );
            }
            else
            {
                this.manager.changeConfiguration ( configurationId, null, future );
            }
        }
    }

    @Override
    protected void performStore ( final String configurationId, final Map<String, String> properties, final ConfigurationFuture future ) throws IOException
    {
        final File file = new File ( this.base, fileName ( configurationId ) );
        final Properties prop = new Properties ();
        prop.putAll ( properties );

        final FileOutputStream out = new FileOutputStream ( file );
        try
        {
            prop.store ( out, "" );

            this.manager.changeConfiguration ( configurationId, properties, future );
        }
        finally
        {
            if ( out != null )
            {
                out.close ();
            }
        }
    }

}
