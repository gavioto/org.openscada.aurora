/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.filter.internal;

public class Encoder
{

    public static final byte CHAR_NUL = 0x00;

    public static final byte CHAR_ASTERISK = '*';

    public static final byte CHAR_PAREN_LEFT = '(';

    public static final byte CHAR_PAREN_RIGHT = ')';

    public static final byte CHAR_BACKSPACE = '\\';

    public static String encode ( final String toEncode )
    {
        final StringBuilder sb = new StringBuilder ();

        for ( int i = 0; i < toEncode.length (); i++ )
        {
            final char b = toEncode.charAt ( i );
            switch ( b )
            {
            case CHAR_NUL:
            case CHAR_ASTERISK:
            case CHAR_PAREN_LEFT:
            case CHAR_PAREN_RIGHT:
            case CHAR_BACKSPACE:
                sb.append ( "\\" + String.format ( "%02x", new Object[] { b & 0xff } ) );
                break;
            default:
                sb.append ( new Character ( b ) );
            }
        }
        return sb.toString ();
    }

    public static String decode ( final String toDecode )
    {
        final StringBuilder sb = new StringBuilder ();
        for ( int i = 0; i < toDecode.length (); i++ )
        {
            final char c = toDecode.charAt ( i );
            if ( c == '\\' )
            {
                final String ec = "" + toDecode.charAt ( i + 1 ) + toDecode.charAt ( i + 2 );
                sb.append ( String.valueOf ( (char)Integer.parseInt ( ec, 16 ) ) );
                i += 2;
            }
            else
            {
                sb.append ( c );
            }
        }
        return sb.toString ();
    }
}
