package org.openscada.ca.file.internal;

import java.util.Map;

import org.openscada.ca.common.ConfigurationFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryStorage extends AbstractStorage
{

    public MemoryStorage ( final String factoryId )
    {
        super ( factoryId );
    }

    private final static Logger logger = LoggerFactory.getLogger ( MemoryStorage.class );

    @Override
    protected void performDelete ( final String configurationId, final ConfigurationFuture future ) throws Exception
    {
        logger.info ( "Deleting: {}", configurationId );

        // delete
        this.manager.changeConfiguration ( configurationId, null, future );
    }

    @Override
    protected void performStore ( final String configurationId, final Map<String, String> properties, final ConfigurationFuture future ) throws Exception
    {
        logger.info ( "Storing: {} -> {}", new Object[] { configurationId, properties } );

        // store
        this.manager.changeConfiguration ( configurationId, properties, future );
    }

}
