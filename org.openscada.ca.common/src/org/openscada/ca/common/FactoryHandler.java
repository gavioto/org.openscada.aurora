package org.openscada.ca.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.openscada.ca.Configuration;
import org.openscada.ca.Factory;
import org.openscada.ca.FactoryState;
import org.openscada.ca.SelfManagedConfigurationFactory;
import org.openscada.ca.StorageListener;
import org.openscada.utils.concurrent.InstantErrorFuture;

public class FactoryHandler implements Factory
{
    private SelfManagedConfigurationFactory service;

    private final String id;

    private StorageListener listener;

    private final Map<String, ConfigurationImpl> configurations;

    private FactoryState state;

    private String description;

    public FactoryHandler ( final String id )
    {
        this.id = id;
        this.configurations = new HashMap<String, ConfigurationImpl> ();
    }

    public synchronized void addService ( final SelfManagedConfigurationFactory service )
    {
        if ( this.service != null )
        {
            return;
        }

        this.service = service;
        service.addConfigurationListener ( this.listener = new StorageListener () {

            public void configurationUpdate ( final Configuration[] addedOrChanged, final String[] deleted )
            {
                FactoryHandler.this.configurationUpdate ( addedOrChanged, deleted );
            }
        } );
    }

    protected synchronized void configurationUpdate ( final Configuration[] addedOrChanged, final String[] deleted )
    {
        if ( addedOrChanged != null )
        {
            for ( final Configuration cfg : addedOrChanged )
            {
                final ConfigurationImpl impl = new ConfigurationImpl ( cfg );
                this.configurations.put ( cfg.getId (), impl );
            }
        }
        if ( deleted != null )
        {
            for ( final String cfg : deleted )
            {
                this.configurations.remove ( cfg );
            }
        }
    }

    public synchronized void removeService ( final SelfManagedConfigurationFactory service )
    {
        if ( service != this.service )
        {
            return;
        }
        this.service.removeConfigurationListener ( this.listener );
        this.service = null;

        this.configurations.clear ();
    }

    public String getDescription ()
    {
        return this.description;
    }

    public String getId ()
    {
        return this.id;
    }

    public FactoryState getState ()
    {
        return this.state;
    }

    public synchronized Configuration[] getConfigurations ()
    {
        return this.configurations.values ().toArray ( new Configuration[0] );
    }

    public synchronized Configuration getConfiguration ( final String configurationId )
    {
        return this.configurations.get ( configurationId );
    }

    public synchronized Future<Configuration> deleteConfiguration ( final String configurationId )
    {
        if ( this.service == null )
        {
            return new InstantErrorFuture<Configuration> ( new IllegalStateException ( "Factory not bound" ) );
        }
        return this.service.delete ( configurationId );
    }

    public synchronized Future<Configuration> updateConfiguration ( final String configurationId, final Map<String, String> properties )
    {
        if ( this.service == null )
        {
            return new InstantErrorFuture<Configuration> ( new IllegalStateException ( "Factory not bound" ) );
        }

        return this.service.update ( configurationId, properties );
    }

    public synchronized Future<Configuration> createConfiguration ( final String configurationId, final Map<String, String> properties )
    {
        if ( this.service == null )
        {
            return new InstantErrorFuture<Configuration> ( new IllegalStateException ( "Factory not bound" ) );
        }

        return this.service.create ( configurationId, properties );
    }

}
