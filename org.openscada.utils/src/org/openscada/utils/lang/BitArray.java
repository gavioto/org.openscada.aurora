/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.lang;

public class BitArray
{
    private byte[] data;

    public BitArray ()
    {
        this.data = new byte[0];
    }

    public BitArray ( final int initialSize )
    {
        this.data = new byte[initialSize / 8 + 1];
    }

    public BitArray ( final byte[] data )
    {
        this.data = data;
    }

    public boolean get ( final int bit )
    {
        if ( bit < 0 )
        {
            return false;
        }

        final int pos = bit >> 8;
        if ( pos >= this.data.length )
        {
            return false;
        }

        return ( this.data[pos] & 1 << bit % 8 ) > 0;
    }

    public void set ( final int bit, final boolean value )
    {
        if ( bit < 0 )
        {
            return;
        }

        final int pos = bit / 8;
        ensureSize ( pos + 1 );

        if ( value )
        {
            this.data[pos] |= 1 << bit % 8;
        }
        else
        {
            this.data[pos] &= ~ ( 1 << bit % 8 );
        }
    }

    private void ensureSize ( final int size )
    {
        if ( this.data.length < size )
        {
            final byte[] newData = new byte[size];
            System.arraycopy ( this.data, 0, newData, 0, this.data.length );
            this.data = newData;
        }
    }

    public byte[] toArray ()
    {
        return this.data;
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ( "[" );

        for ( final byte b : this.data )
        {
            sb.append ( String.format ( "%02x|", b ) );
        }

        sb.append ( "]" );

        return sb.toString ();
    }
}
