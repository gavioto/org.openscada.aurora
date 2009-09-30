package org.openscada.ca.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.ca.ConfigurationState;
import org.openscada.ca.Factory;
import org.openscada.ca.FactoryState;

public class FactoryImpl implements Factory
{

    private volatile FactoryState state;

    private volatile ConfigurationFactory service;

    private final String id;

    private String description;

    private final AbstractConfigurationAdminImpl admin;

    private Map<String, ConfigurationImpl> configurations = new HashMap<String, ConfigurationImpl> ();

    public FactoryImpl ( final AbstractConfigurationAdminImpl admin, final FactoryState initialState, final String id, final String description, final ConfigurationFactory configurationFactory )
    {
        this.admin = admin;
        this.id = id;
        this.description = description;
        this.state = initialState;
        this.service = configurationFactory;
    }

    public synchronized void setConfigurations ( final Map<String, Map<String, String>> configurations )
    {
        final Map<String, ConfigurationImpl> cfgs = new HashMap<String, ConfigurationImpl> ();

        for ( final Map.Entry<String, Map<String, String>> cfg : configurations.entrySet () )
        {
            final String id = cfg.getKey ();
            final ConfigurationImpl cfgImpl = new ConfigurationImpl ( id, this.admin, this, ConfigurationState.AVAILABLE, cfg.getValue () );
            cfgs.put ( cfgImpl.getId (), cfgImpl );

            this.admin.performApplyConfiguration ( cfgImpl );
        }

        this.configurations = cfgs;
    }

    public Configuration[] getConfigurations ()
    {
        Collection<ConfigurationImpl> result = new LinkedList<ConfigurationImpl> ();

        synchronized ( this )
        {
            if ( this.configurations == null )
            {
                return new Configuration[0];
            }
            result = new LinkedList<ConfigurationImpl> ( this.configurations.values () );
        }

        // kick out all deleted
        final Iterator<ConfigurationImpl> i = result.iterator ();
        while ( i.hasNext () )
        {
            final ConfigurationImpl cfg = i.next ();
            if ( cfg.isDeleted () )
            {
                i.remove ();
            }
        }

        return result.toArray ( new Configuration[result.size ()] );
    }

    public String getId ()
    {
        return this.id;
    }

    public String getDescription ()
    {
        return this.description;
    }

    public FactoryState getState ()
    {
        return this.state;
    }

    public synchronized void purge ()
    {
        for ( final ConfigurationImpl cfg : this.configurations.values () )
        {
            cfg.delete ();
        }
        this.admin.performPurge ( this, this.service );
    }

    public ConfigurationFactory getService ()
    {
        return this.service;
    }

    public void setState ( final FactoryState state )
    {
        this.state = state;
    }

    public synchronized void setService ( final ConfigurationFactory service, final String description )
    {
        this.service = service;
        this.description = description;
        if ( this.service != null && this.configurations != null )
        {
            for ( final ConfigurationImpl cfg : this.configurations.values () )
            {
                this.admin.performApplyConfiguration ( cfg );
            }
        }
        if ( this.service == null )
        {
            for ( final ConfigurationImpl cfg : this.configurations.values () )
            {
                cfg.setAvailable ();
            }
        }
    }

    public synchronized Configuration createConfiguration ( final String configurationId, final Map<String, String> initialProperties )
    {
        ConfigurationImpl cfg = this.configurations.get ( configurationId );
        if ( cfg == null )
        {
            cfg = new ConfigurationImpl ( configurationId, this.admin, this, ConfigurationState.AVAILABLE, null );
            this.configurations.put ( configurationId, cfg );
            if ( initialProperties != null )
            {
                cfg.update ( initialProperties );
            }
        }
        else
        {
            cfg.update ( initialProperties );
        }
        return cfg;
    }

    public void deleteConfiguration ( final String id )
    {
        synchronized ( this.configurations )
        {
            this.configurations.remove ( id );
        }
    }

    public Configuration getConfiguration ( final String configurationId )
    {
        synchronized ( this )
        {
            final ConfigurationImpl cfg = this.configurations.get ( configurationId );
            if ( cfg.isDeleted () )
            {
                return null;
            }
            return cfg;
        }
    }
}
