package org.openscada.ca.common;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationFactory;
import org.openscada.ca.ConfigurationListener;
import org.openscada.ca.Factory;
import org.openscada.ca.FactoryState;
import org.openscada.ca.SelfManagedConfigurationFactory;

public class FactoryImpl implements Factory
{

    private final String id;

    private String description;

    private FactoryState state;

    private final Map<String, ConfigurationImpl> configurations = new HashMap<String, ConfigurationImpl> ();

    private Object service;

    private ConfigurationListener listener;

    public FactoryImpl ( final String id )
    {
        this.id = id;
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

    public void setService ( final Object service )
    {
        this.service = service;
    }

    public Object getService ()
    {
        return this.service;
    }

    public ConfigurationFactory getConfigurationFactoryService ()
    {
        final Object service = this.service;
        if ( service instanceof ConfigurationFactory )
        {
            return (ConfigurationFactory)service;
        }
        else
        {
            return null;
        }
    }

    public void addConfiguration ( final ConfigurationImpl configuration )
    {
        this.configurations.put ( configuration.getId (), configuration );
    }

    public void removeConfigration ( final String configurationId )
    {
        this.configurations.remove ( configurationId );
    }

    public void setListener ( final ConfigurationListener listener )
    {
        this.listener = listener;
    }

    public ConfigurationListener getListener ()
    {
        return this.listener;
    }

    public SelfManagedConfigurationFactory getSelfService ()
    {
        final Object service = this.service;
        if ( service instanceof SelfManagedConfigurationFactory )
        {
            return (SelfManagedConfigurationFactory)service;
        }
        else
        {
            return null;
        }
    }

    public boolean isSelfManaged ()
    {
        return this.service instanceof SelfManagedConfigurationFactory;
    }
}
