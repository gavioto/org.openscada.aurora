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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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

    /**
     * Create a new data node and fill with byte buffer
     * <p>
     * The byte buffer is copied by the constructor
     * </p>
     * @param id The node id
     * @param data the data
     */
    public DataNode ( final String id, final byte[] data )
    {
        this.id = id;
        if ( data != null )
        {
            this.data = data.clone ();
        }
        else
        {
            this.data = null;
        }
    }

    /**
     * Create a new data node and fill with the input stream
     * <p>
     * The input stream is read into the data buffer of the node
     * </p>
     * <p>
     * The stream is not closed by the constructor.
     * </p>
     * @param id the node id
     * @param stream the stream to read from
     * @throws IOException if stream reading fails
     */
    public DataNode ( final String id, final InputStream stream ) throws IOException
    {
        this.id = id;

        if ( stream == null )
        {
            this.data = null;
        }
        else
        {
            this.data = loadFromFile ( stream );
        }
    }

    private byte[] loadFromFile ( final InputStream stream ) throws IOException
    {
        int bytes = 0;
        final List<byte[]> buffers = new LinkedList<byte[]> ();

        byte[] buffer = new byte[4096];
        int i;
        while ( ( i = stream.read ( buffer ) ) > 0 )
        {
            if ( buffer.length != i )
            {
                // we have to add a smaller buffer
                final byte[] newBuffer = new byte[i];
                System.arraycopy ( buffer, 0, newBuffer, 0, i );
                buffers.add ( newBuffer );
            }
            else
            {
                // we can directly add the buffer
                buffers.add ( buffer );
                buffer = new byte[4096];
            }

            // record the number of bytes
            bytes += i;
        }

        // if we only have one buffer
        if ( buffers.size () == 1 )
        {
            // we can simply use it
            return buffers.get ( 0 );
        }
        else
        {
            // we have to merge data 
            final byte[] data = new byte[bytes];
            int currentBytes = 0;
            for ( final byte[] cb : buffers )
            {
                System.arraycopy ( cb, 0, this.data, currentBytes, cb.length );
                currentBytes += cb.length;
            }
            return data;
        }
    }

    /**
     * Create a new data node and fill it with the serialized
     * representation of the data object provided.
     * @param id the node id
     * @param data the data to serialize
     */
    public DataNode ( final String id, final Serializable data )
    {
        this.id = id;

        if ( data == null )
        {
            this.data = null;
        }
        else
        {
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
    }

    public String getId ()
    {
        return this.id;
    }

    /**
     * Get a copy of the data
     * @return the binary node data
     */
    public byte[] getData ()
    {
        return this.data.clone ();
    }

    public Object getDataAsObject () throws IOException, ClassNotFoundException
    {
        if ( this.data == null )
        {
            return null;
        }
        else
        {
            final ByteArrayInputStream bin = new ByteArrayInputStream ( this.data );
            final ObjectInputStream ois = new ObjectInputStream ( bin );
            ois.close ();
            return ois.readObject ();
        }
    }

    public Object getDataAsObject ( final Object defaultValue )
    {
        try
        {
            final Object result = getDataAsObject ();
            if ( result == null )
            {
                return defaultValue;
            }
            else
            {
                return result;
            }
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