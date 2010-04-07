package org.openscada.hsdb.backend.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.openscada.hsdb.backend.BackEndManagerFactory;
import org.openscada.hsdb.configuration.Configuration;
import org.openscada.hsdb.datatypes.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the BackEndManagerFactory interface for FileBackEnd objects.
 * @author Ludwig Straub
 */
public class FileBackEndManagerFactory implements BackEndManagerFactory
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( FileBackEndManagerFactory.class );

    /** Suffix of files containing meta information of back end files. */
    private final static String CONTROL_FILE_SUFFIX = ".va_ctrl";

    /** Empty array of file back end manager objects. */
    private final static FileBackEndManager[] EMPTY_FILE_BACKEND_MANAGER_ARRAY = new FileBackEndManager[0];

    /** Factory that will be used to create new back end objects. */
    private final FileBackEndFactory fileBackEndFactory;

    /**
     * Constructor.
     * @param fileBackEndFactory factory that will be used to create new back end objects
     */
    public FileBackEndManagerFactory ( final FileBackEndFactory fileBackEndFactory )
    {
        this.fileBackEndFactory = fileBackEndFactory;
    }

    /**
     * This method returns the name of the configuration file with the specified encoded id.
     * @param encodedConfigurationId encoded configuration id
     * @return name of the configuration file with the specified encoded id
     */
    public String getConfigurationFileName ( final String encodedConfigurationId )
    {
        return new File ( new File ( this.fileBackEndFactory.getFileRoot (), encodedConfigurationId ), encodedConfigurationId + CONTROL_FILE_SUFFIX ).getPath ();
    }

    /**
     * This method loads the configuration data from the configuration file.
     * @param encodedConfigurationId folder name and prefix of file name for configuration control files
     * @return loaded configuration object or null if configuration file does not exist
     * @throws Exception in case of problems
     */
    public Configuration loadConfiguration ( final String encodedConfigurationId ) throws Exception
    {
        final String configurationFileName = getConfigurationFileName ( encodedConfigurationId );
        if ( !new File ( configurationFileName ).exists () )
        {
            return null;
        }
        final Properties properties = new Properties ();
        try
        {
            properties.load ( new FileInputStream ( configurationFileName ) );
        }
        catch ( final IOException e )
        {
            final String message = String.format ( "could not load configuration from file '%s'", configurationFileName );
            logger.error ( message, e );
            throw new Exception ( message, e );
        }
        final Map<String, String> data = new HashMap<String, String> ();
        for ( final Entry<Object, Object> entry : properties.entrySet () )
        {
            data.put ( entry.getKey ().toString (), entry.getValue ().toString () );
        }
        final Configuration configuration = new Configuration ();
        configuration.setId ( properties.getProperty ( Configuration.MANAGER_CONFIGURATION_ID ) );
        if ( configuration.getId () == null )
        {
            final String configurationId = FileBackEndFactory.decodeFileNamePart ( encodedConfigurationId );
            logger.error ( String.format ( "could not retrieve configuration for '%s' from control file '%s'. please check file!", configurationId, configurationFileName ) );
            configuration.setId ( configurationId );
            data.put ( Configuration.MANAGER_CONFIGURATION_ID, configurationId );
        }
        if ( !data.containsKey ( Configuration.DATA_TYPE_KEY ) )
        {
            final String configurationId = FileBackEndFactory.decodeFileNamePart ( encodedConfigurationId );
            logger.error ( String.format ( "could not retrieve data type information for configuration '%s' from control file '%s'. please check file!", configurationId, configurationFileName ) );
            data.put ( Configuration.DATA_TYPE_KEY, DataType.convertDataTypeToShortString ( DataType.DOUBLE_VALUE ) );
        }
        configuration.setData ( data );
        return configuration;
    }

    /**
     * This method saves the configuration data to the configuration file.
     * @param configuration configuration object that has to be saved
     * @throws Exception in case of problems
     */
    public void saveConfiguration ( final Configuration configuration ) throws Exception
    {
        if ( configuration == null )
        {
            return;
        }
        final Properties properties = new Properties ();
        properties.put ( Configuration.MANAGER_CONFIGURATION_ID, configuration.getId () );
        properties.putAll ( configuration.getData () );
        final String fileName = getConfigurationFileName ( FileBackEndFactory.encodeFileNamePart ( configuration.getId () ) );
        try
        {
            final File file = new File ( fileName );
            final File parent = file.getParentFile ();
            if ( parent != null && !parent.exists () )
            {
                parent.mkdirs ();
            }
            properties.store ( new FileOutputStream ( file ), null );
        }
        catch ( final IOException e )
        {
            final String message = String.format ( "could not save configuration to file '%s'", fileName );
            logger.error ( message, e );
            throw new Exception ( message, e );
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerFactory#getBackEndManagers()
     */
    public FileBackEndManager[] getBackEndManagers ()
    {
        final File root = new File ( this.fileBackEndFactory.getFileRoot () );
        if ( !root.exists () )
        {
            return EMPTY_FILE_BACKEND_MANAGER_ARRAY;
        }
        final List<FileBackEndManager> managers = new ArrayList<FileBackEndManager> ();
        for ( final File file : root.listFiles () )
        {
            if ( file.exists () && file.isDirectory () )
            {
                try
                {
                    final Configuration configuration = loadConfiguration ( file.getName () );
                    if ( configuration != null )
                    {
                        final FileBackEndManager manager = new FileBackEndManager ( configuration, this, this.fileBackEndFactory );
                        if ( manager != null )
                        {
                            managers.add ( manager );
                        }
                    }
                    else
                    {
                        logger.error ( "Failed to load archive from: " + file );
                    }
                }
                catch ( final Exception e )
                {
                    logger.error ( String.format ( "could not create file backend manager for folder '%s'", file.getPath () ), e );
                }
            }
        }
        return managers.toArray ( EMPTY_FILE_BACKEND_MANAGER_ARRAY );
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerFactory#getBackEndManager(Configuration,boolean)
     */
    public FileBackEndManager getBackEndManager ( final Configuration configuration, final boolean createIfNotExists )
    {
        if ( configuration == null )
        {
            throw new IllegalArgumentException ( "'configuration' must not be null" );
        }
        try
        {
            Configuration loadedConfiguration = loadConfiguration ( FileBackEndFactory.encodeFileNamePart ( configuration.getId () ) );
            if ( loadedConfiguration == null )
            {
                if ( createIfNotExists )
                {
                    saveConfiguration ( configuration );
                    loadedConfiguration = configuration;
                }
            }
            if ( loadedConfiguration != null )
            {
                return new FileBackEndManager ( loadedConfiguration, this, this.fileBackEndFactory );
            }
        }
        catch ( final Exception e )
        {
            logger.error ( String.format ( "could not create file backend manager for configuration '%s'", configuration.getId () ), e );
        }
        return null;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerFactory#delete(org.openscada.hsdb.configuration.Configuration)
     */
    public void delete ( final Configuration configuration )
    {
        new File ( getConfigurationFileName ( FileBackEndFactory.encodeFileNamePart ( configuration.getId () ) ) ).delete ();
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerFactory#save(org.openscada.hsdb.configuration.Configuration)
     */
    public void save ( final Configuration configuration )
    {
        try
        {
            saveConfiguration ( configuration );
        }
        catch ( final Exception e )
        {
            logger.error ( "could not save configuration", e );
        }
    }
}
