package org.openscada.ca.testing;

import java.util.Map;

import org.openscada.ca.ConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationFactoryImpl implements ConfigurationFactory
{
    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationFactoryImpl.class );

    public void update ( final String configurationId, final Map<String, String> properties ) throws NumberFormatException, InterruptedException
    {
        logger.info ( String.format ( "Updating configuration: %s (%s)", configurationId, properties ) );

        if ( properties.containsKey ( "error" ) )
        {
            throw new RuntimeException ( "Error flag set" );
        }
        if ( properties.containsKey ( "sleep" ) )
        {
            Thread.sleep ( Integer.parseInt ( properties.get ( "sleep" ) ) );
        }
    }

    public void delete ( final String configurationId )
    {
        logger.info ( "Deleting: " + configurationId );
    }

}
