/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2009-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.ds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.openscada.utils.lang.Immutable;

/**
 * A data node used for storing data in a {@link DataStore}.
 * @author Jens Reimann
 * @since 0.15.0
 *
 */
@Immutable
public class DataNode
{
    private final String id;

    private final byte[] data;

    public DataNode ( final String id, final byte[] data )
    {
        this.id = id;
        this.data = data.clone ();
    }

    public DataNode ( final String id, final Serializable data )
    {
        this.id = id;

        final ByteArrayOutputStream bos = new ByteArrayOutputStream ();
        try
        {
            final ObjectOutputStream os = new ObjectOutputStream ( bos );
            os.writeObject ( data );
            os.close ();
            this.data = bos.toByteArray ();
        }
        catch ( final IOException e )
        {
            throw new RuntimeException ( "Failed to store data node", e );
        }
    }

    public String getId ()
    {
        return this.id;
    }

    public byte[] getData ()
    {
        return this.data.clone ();
    }

    public Object getDataAsObject () throws IOException, ClassNotFoundException
    {
        final ByteArrayInputStream bin = new ByteArrayInputStream ( this.data );
        final ObjectInputStream ois = new ObjectInputStream ( bin );
        ois.close ();
        return ois.readObject ();
    }

    public Object getDataAsObject ( final Object defaultValue )
    {
        try
        {
            return getDataAsObject ();
        }
        catch ( final Exception e )
        {
            return defaultValue;
        }
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.id == null ? 0 : this.id.hashCode () );
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
        if ( ! ( obj instanceof DataNode ) )
        {
            return false;
        }
        final DataNode other = (DataNode)obj;
        if ( this.id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.id ) )
        {
            return false;
        }
        return true;
    }

}
