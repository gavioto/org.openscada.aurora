/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.ca.file.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.openscada.ca.common.AbstractConfigurationAdministrator;
import org.openscada.ca.common.ConfigurationImpl;
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

    protected File getRootFile ()
    {
        final String rootDir = System.getProperty ( "org.openscada.ca.file.root", null );

        if ( rootDir == null || rootDir.length () == 0 )
        {
            return this.context.getDataFile ( STORE );
        }
        else
        {
            return new File ( rootDir );
        }
    }

    private File initRoot ()
    {
        final File file = getRootFile ();
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
        logger.info ( "Loading from: {}", configurationRoot.getName () );

        final List<ConfigurationImpl> configurations = new LinkedList<ConfigurationImpl> ();

        for ( final File file : configurationRoot.listFiles ( new DataFilenameFilter () ) )
        {
            logger.info ( "Loading file: {}", file.getName () );
            final ConfigurationImpl cfg = loadConfiguration ( factoryId, file );

            if ( cfg != null )
            {
                configurations.add ( cfg );
            }
        }

        addStoredFactory ( factoryId, configurations.toArray ( new ConfigurationImpl[configurations.size ()] ) );
    }

    private ConfigurationImpl loadConfiguration ( final String factoryId, final File file )
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
            return new ConfigurationImpl ( id, factoryId, result );
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

    @Override
    protected void performPurge ( final String factoryId, final PurgeFuture future )
    {
        logger.info ( "Request to delete: {}", factoryId );

        if ( this.root == null )
        {
            logger.warn ( "Unable to store : no root" );
            return;
        }

        final File path = getFactoryPath ( factoryId );

        for ( final File file : path.listFiles ( new DataFilenameFilter () ) )
        {
            logger.info ( "Delete file: " + file.getName () );
            final String id = idFromFile ( factoryId, file );

            final ConfigurationFuture subFuture = new ConfigurationFuture ();
            changeConfiguration ( factoryId, id, null, subFuture );

            future.addChild ( subFuture );

            file.delete ();
        }

        final File metaFile = new File ( path, ".meta" );
        metaFile.delete ();

        future.setComplete ();

        logger.debug ( "Delete factory root: {}", path );
        path.delete ();
    }

    private String idFromFile ( final String factoryId, final File file )
    {
        return loadConfiguration ( factoryId, file ).getData ().get ( "id" );
    }

    @Override
    protected void performStoreConfiguration ( final String factoryId, final String configurationId, final Map<String, String> properties, final boolean fullSet, final ConfigurationFuture future ) throws FileNotFoundException, IOException
    {
        if ( this.root == null )
        {
            future.setError ( new RuntimeException ( "No root to store" ).fillInStackTrace () );
            logger.warn ( "Unable to store : no root" );
            return;
        }

        final File path = getFactoryPath ( factoryId );
        final File file = new File ( path, encode ( configurationId ) );

        logger.info ( String.format ( "Storing %s to %s", configurationId, file ) );

        final Map<String, String> newProperties = new HashMap<String, String> ();

        // if this is differential, load in old data first
        if ( !fullSet )
        {
            final ConfigurationImpl oldConfig = loadConfiguration ( factoryId, file );
            if ( oldConfig != null )
            {
                newProperties.putAll ( oldConfig.getData () );
            }
        }

        // merge in changes
        for ( final Map.Entry<String, String> entry : properties.entrySet () )
        {
            final String key = entry.getKey ();
            final String value = entry.getValue ();
            if ( value != null )
            {
                newProperties.put ( key, value );
            }
            else
            {
                newProperties.remove ( key );
            }
        }

        // convert to properties and store
        final Properties p = new Properties ();
        p.putAll ( newProperties );
        p.put ( "id", configurationId );

        final FileOutputStream stream = new FileOutputStream ( file );
        try
        {
            logger.debug ( "Storing {}/{} -> {}", new Object[] { factoryId, configurationId, newProperties } );
            p.store ( stream, "" );
        }
        finally
        {
            stream.close ();
        }

        // notify the abstract service from our content change
        changeConfiguration ( factoryId, configurationId, newProperties, future );
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

    @Override
    protected void performDeleteConfiguration ( final String factoryId, final String configurationId, final ConfigurationFuture future )
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
}
