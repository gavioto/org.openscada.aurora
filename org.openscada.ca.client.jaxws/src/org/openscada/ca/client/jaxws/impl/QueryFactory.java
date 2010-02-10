package org.openscada.ca.client.jaxws.impl;

import java.util.concurrent.Callable;

import org.openscada.ca.FactoryInformation;
import org.openscada.ca.client.jaxws.RemoteConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryFactory implements Callable<FactoryInformation>
{

    private final static Logger logger = LoggerFactory.getLogger ( QueryFactory.class );

    private final RemoteConfigurationClient port;

    private final String factoryId;

    public QueryFactory ( final RemoteConfigurationClient port, final String factoryId )
    {
        this.factoryId = factoryId;
        this.port = port;
    }

    @Override
    public FactoryInformation call () throws Exception
    {
        logger.debug ( "Query data : {}", this.factoryId );
        final FactoryInformation result = this.port.getPort ().getFactory ( this.factoryId );
        logger.debug ( "Query data : {} -> {}", new Object[] { this.factoryId, result } );
        return result;
    }

}
