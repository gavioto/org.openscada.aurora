package org.openscada.ca.common;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationFactory;
import org.openscada.ca.Factory;
import org.openscada.ca.FactoryState;

public class FactoryImpl implements Factory
{

    private final String id;

    private String description;

    private FactoryState state;

    private final Map<String, ConfigurationImpl> configurations = new HashMap<String, ConfigurationImpl> ();

    private ConfigurationFactory service;

    private final Storage storage;

    public FactoryImpl ( final String id, final Storage storage )
    {
        this.id = id;
        this.storage = storage;
    }

    public Storage getStorage ()
    {
        return this.storage;
    }

    public void setDescription ( final String description )
    {
        this.description = description;
    }

    public String getDescription ()
    {
        return this.description;
    }

    public String getId ()
    {
        return this.id;
    }

    public void setState ( final FactoryState state )
    {
        this.state = state;
    }

    public FactoryState getState ()
    {
        return this.state;
    }

    public ConfigurationImpl getConfiguration ( final String configurationId )
    {
        return this.configurations.get ( configurationId );
    }

    public ConfigurationImpl[] getConfigurations ()
    {
        return this.configurations.values ().toArray ( new ConfigurationImpl[0] );
    }

    public void setConfigurations ( final ConfigurationImpl[] configurations )
    {
        this.configurations.clear ();
        for ( final ConfigurationImpl configuration : configurations )
        {
            this.configurations.put ( configuration.getId (), configuration );
        }
    }

    public void setService ( final ConfigurationFactory service )
    {
        this.service = service;
    }

    public ConfigurationFactory getService ()
    {
        return this.service;
    }

    public void addConfiguration ( final ConfigurationImpl configuration )
    {
        this.configurations.put ( configuration.getId (), configuration );
    }

    public void removeConfigration ( final String configurationId )
    {
        this.configurations.remove ( configurationId );
    }
}
