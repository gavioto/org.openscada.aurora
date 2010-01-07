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

package org.openscada.ca.servelt.axis;

import org.openscada.ca.FactoryState;

public class Factory
{
    private String id;

    private String description;

    private FactoryState state;

    private Configuration[] configurations;

    public Factory ( final org.openscada.ca.Factory factory )
    {
        this.id = factory.getId ();
        this.description = factory.getDescription ();
        this.state = factory.getState ();
    }

    public Factory ()
    {
    }

    public void setDescription ( final String description )
    {
        this.description = description;
    }

    public String getDescription ()
    {
        return this.description;
    }

    public void setId ( final String id )
    {
        this.id = id;
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

    public void setConfigurations ( final Configuration[] configurations )
    {
        this.configurations = configurations;
    }

    public Configuration[] getConfigurations ()
    {
        return this.configurations;
    }

}
