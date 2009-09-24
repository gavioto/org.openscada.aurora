package org.openscada.ca.testing;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.ca.ConfigurationFactory;

public class ConfigurationFactoryImpl implements ConfigurationFactory
{

    private final static Logger logger = Logger.getLogger ( ConfigurationFactoryImpl.class );

    public void update ( final String configurationId, final Map<String, String> properties )
    {
        logger.info ( String.format ( "Updating configuration: %s (%s)", configurationId, properties ) );

        if ( properties.containsKey ( "error" ) )
        {
            throw new RuntimeException ( "Error flag set" );
        }
    }

    public void delete ( final String configurationId )
    {
        logger.info ( "Deleting: " + configurationId );
    }

    public void purge ()
    {
        logger.info ( "Purging" );
    }

}
