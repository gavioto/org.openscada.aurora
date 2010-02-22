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

package org.openscada.ca.common;

import java.util.Map;
import java.util.TreeMap;

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

    private final Map<String, ConfigurationImpl> configurations = new TreeMap<String, ConfigurationImpl> ();

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
