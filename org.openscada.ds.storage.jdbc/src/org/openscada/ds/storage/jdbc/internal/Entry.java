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

package org.openscada.ds.storage.jdbc.internal;

import java.io.Serializable;

public class Entry implements Serializable
{

    private static final long serialVersionUID = -5115602881866753356L;

    private String nodeId;

    private String instance;

    private byte[] data;

    public void setData ( final byte[] data )
    {
        this.data = data;
    }

    public void setNodeId ( final String nodeId )
    {
        this.nodeId = nodeId;
    }

    public byte[] getData ()
    {
        return this.data;
    }

    public String getNodeId ()
    {
        return this.nodeId;
    }

    public String getInstance ()
    {
        return this.instance;
    }

    public void setInstance ( final String instance )
    {
        this.instance = instance;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.instance == null ? 0 : this.instance.hashCode () );
        result = prime * result + ( this.nodeId == null ? 0 : this.nodeId.hashCode () );
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
        if ( this.nodeId == null )
        {
            if ( other.nodeId != null )
            {
                return false;
            }
        }
        else if ( !this.nodeId.equals ( other.nodeId ) )
        {
            return false;
        }
        return true;
    }

}
