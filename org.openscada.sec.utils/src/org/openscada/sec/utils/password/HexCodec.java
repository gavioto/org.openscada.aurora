/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec.utils.password;

import java.nio.CharBuffer;

public class HexCodec implements PasswordDigestCodec
{

    private final boolean uppercase;

    public HexCodec ()
    {
        this ( true );
    }

    public HexCodec ( final boolean uppercase )
    {
        this.uppercase = uppercase;
    }

    @Override
    public byte[] decode ( final String data )
    {
        final int len = data.length ();
        final byte[] result = new byte[len / 2 + len % 2];

        final CharBuffer cb = CharBuffer.wrap ( data );

        int i = 0;

        while ( cb.hasRemaining () )
        {
            if ( cb.remaining () > 1 )
            {
                result[i] = (byte) ( 0xFF & Integer.parseInt ( new String ( new char[] { cb.get (), cb.get () } ), 16 ) );
                i++;
            }
            else
            {
                result[i] = (byte) ( 0xFF & Integer.parseInt ( new String ( new char[] { cb.get () } ), 16 ) );
            }
        }

        return result;
    }

    @Override
    public String encode ( final byte[] data )
    {
        final StringBuffer sb = new StringBuffer ();

        for ( final byte b : data )
        {
            sb.append ( String.format ( "%02x", b ) );
        }

        if ( this.uppercase )
        {
            return sb.toString ().toUpperCase ();
        }
        else
        {
            return sb.toString ();
        }
    }

}
