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

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationState;

public class ConfigurationImpl implements Configuration
{
    private final String id;

    private Map<String, String> data;

    private final String factoryId;

    private ConfigurationState state;

    private Throwable error;

    public ConfigurationImpl ( final String id, final String factoryId, final Map<String, String> data )
    {
        this.id = id;
        this.factoryId = factoryId;
        this.data = new HashMap<String, String> ( data );
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public Map<String, String> getData ()
    {
        return this.data;
    }

    public Throwable getErrorInformation ()
    {
        return this.error;
    }

    public String getId ()
    {
        return this.id;
    }

    public ConfigurationState getState ()
    {
        return this.state;
    }

    public void setData ( final Map<String, String> data )
    {
        this.data = data;
    }

    public void setState ( final ConfigurationState state, final Throwable e )
    {
        this.state = state;
        this.error = e;
    }

}
