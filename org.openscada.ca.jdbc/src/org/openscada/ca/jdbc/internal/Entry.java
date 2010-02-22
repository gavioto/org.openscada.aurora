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

package org.openscada.ca.jdbc.internal;

import java.io.Serializable;

public class Entry implements Serializable
{
    private static final long serialVersionUID = -4409482881200779138L;

    private String factoryId;

    private String configurationId;

    private String key;

    private java.sql.Clob value;

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public void setFactoryId ( final String factoryId )
    {
        this.factoryId = factoryId;
    }

    public String getConfigurationId ()
    {
        return this.configurationId;
    }

    public void setConfigurationId ( final String configurationId )
    {
        this.configurationId = configurationId;
    }

    public String getKey ()
    {
        return this.key;
    }

    public void setKey ( final String entry )
    {
        this.key = entry;
    }

    public java.sql.Clob getValue ()
    {
        return this.value;
    }

    public void setValue ( final java.sql.Clob data )
    {
        this.value = data;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.configurationId == null ? 0 : this.configurationId.hashCode () );
        result = prime * result + ( this.key == null ? 0 : this.key.hashCode () );
        result = prime * result + ( this.factoryId == null ? 0 : this.factoryId.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( ! ( obj instanceof Entry ) )
        {
            return false;
        }
        final Entry other = (Entry)obj;
        if ( this.configurationId == null )
        {
            if ( other.configurationId != null )
            {
                return false;
            }
        }
        else if ( !this.configurationId.equals ( other.configurationId ) )
        {
            return false;
        }
        if ( this.key == null )
        {
            if ( other.key != null )
            {
                return false;
            }
        }
        else if ( !this.key.equals ( other.key ) )
        {
            return false;
        }
        if ( this.factoryId == null )
        {
            if ( other.factoryId != null )
            {
                return false;
            }
        }
        else if ( !this.factoryId.equals ( other.factoryId ) )
        {
            return false;
        }
        return true;
    }

}
