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

package org.openscada.ca.servelt.jaxws;

import java.util.Map;

import org.openscada.ca.ConfigurationState;

public class Configuration
{
    private String factoryId;

    private String id;

    private ConfigurationState state;

    private Map<String, String> data;

    public Configuration ()
    {
    }

    public Configuration ( final Factory factory, final org.openscada.ca.Configuration configuration )
    {
        this.factoryId = factory.getId ();
        this.id = configuration.getId ();
        this.state = configuration.getState ();
        this.data = configuration.getData ();
    }

    public void setData ( final Map<String, String> data )
    {
        this.data = data;
    }

    public Map<String, String> getData ()
    {
        return this.data;
    }

    public void setState ( final ConfigurationState state )
    {
        this.state = state;
    }

    public ConfigurationState getState ()
    {
        return this.state;
    }

    public void setFactoryId ( final String factoryId )
    {
        this.factoryId = factoryId;
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public void setId ( final String id )
    {
        this.id = id;
    }

    public String getId ()
    {
        return this.id;
    }
}
