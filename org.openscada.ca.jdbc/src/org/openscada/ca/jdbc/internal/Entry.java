/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ca.jdbc.internal;

import java.io.Serializable;

public class Entry implements Serializable
{
    private static final long serialVersionUID = -4409482881200779138L;

    private String factoryId;

    private String configurationId;

    private String key;

    private String instance;

    private String value;

    public String getInstance ()
    {
        return this.instance;
    }

    public void setInstance ( final String instance )
    {
        this.instance = instance;
    }

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

    public String getValue ()
    {
        return this.value;
    }

    public void setValue ( final String data )
    {
        this.value = data;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.configurationId == null ? 0 : this.configurationId.hashCode () );
        result = prime * result + ( this.factoryId == null ? 0 : this.factoryId.hashCode () );
        result = prime * result + ( this.instance == null ? 0 : this.instance.hashCode () );
        result = prime * result + ( this.key == null ? 0 : this.key.hashCode () );
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
        if ( this.instance == null )
        {
            if ( other.instance != null )
            {
                return false;
            }
        }
        else if ( !this.instance.equals ( other.instance ) )
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
        return true;
    }

}
