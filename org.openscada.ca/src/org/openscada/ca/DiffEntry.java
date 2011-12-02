/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ca;

import java.io.Serializable;
import java.util.Map;

public class DiffEntry implements Serializable, Comparable<DiffEntry>
{
    private static final long serialVersionUID = 9030999002792898317L;

    public static enum Operation
    {
        ADD,
        DELETE,
        UPDATE_SET,
        UPDATE_DIFF
    }

    private String factoryId;

    private String configurationId;

    private Operation operation;

    private Map<String, String> oldData;

    private Map<String, String> newData;

    public DiffEntry ()
    {
    }

    public DiffEntry ( final String factoryId, final String configurationId, final Operation operation, final Map<String, String> newData )
    {
        this ( factoryId, configurationId, operation, null, newData );
    }

    public DiffEntry ( final String factoryId, final String configurationId, final Operation operation, final Map<String, String> oldData, final Map<String, String> newData )
    {
        this.factoryId = factoryId;
        this.configurationId = configurationId;
        this.operation = operation;
        this.oldData = oldData;
        this.newData = newData;
    }

    public String getConfigurationId ()
    {
        return this.configurationId;
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public Operation getOperation ()
    {
        return this.operation;
    }

    public void setConfigurationId ( final String configurationId )
    {
        this.configurationId = configurationId;
    }

    public void setFactoryId ( final String factoryId )
    {
        this.factoryId = factoryId;
    }

    public void setOperation ( final Operation operation )
    {
        this.operation = operation;
    }

    public Map<String, String> getOldData ()
    {
        return this.oldData;
    }

    public void setOldData ( final Map<String, String> oldData )
    {
        this.oldData = oldData;
    }

    public Map<String, String> getNewData ()
    {
        return this.newData;
    }

    public void setNewData ( final Map<String, String> newData )
    {
        this.newData = newData;
    }

    @Override
    public String toString ()
    {
        return String.format ( "%s/%s/%s", this.factoryId, this.configurationId, this.operation );
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.configurationId == null ? 0 : this.configurationId.hashCode () );
        result = prime * result + ( this.factoryId == null ? 0 : this.factoryId.hashCode () );
        result = prime * result + ( this.operation == null ? 0 : this.operation.hashCode () );
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
        if ( ! ( obj instanceof DiffEntry ) )
        {
            return false;
        }
        final DiffEntry other = (DiffEntry)obj;
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
        if ( this.operation == null )
        {
            if ( other.operation != null )
            {
                return false;
            }
        }
        else if ( !this.operation.equals ( other.operation ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo ( final DiffEntry o )
    {
        final int rc = this.factoryId.compareTo ( o.factoryId );
        if ( rc != 0 )
        {
            return rc;
        }
        return this.configurationId.compareTo ( o.configurationId );
    }

}
