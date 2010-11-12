/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.filter.internal;

import org.openscada.utils.filter.internal.Tokenizer.TokenizeException;

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
                if ( b < 32 || b > 127 )
                {
                    sb.append ( "\\" + String.format ( "%02x", new Object[] { b & 0xff } ) );
                }
                else
                {
                    sb.append ( new Character ( b ) );
                }
            }
        }
        return sb.toString ();
    }

    public static String decode ( final String toDecode ) throws TokenizeException
    {
        final StringBuilder sb = new StringBuilder ();
        for ( int i = 0; i < toDecode.length (); i++ )
        {
            final char c = toDecode.charAt ( i );
            if ( c == '\\' )
            {
                if ( i + 2 >= toDecode.length () )
                {
                    throw new TokenizeException ( "valid escape sequence expected" );
                }
                final String ec = "" + toDecode.charAt ( i + 1 ) + toDecode.charAt ( i + 2 );
                try
                {
                    sb.append ( String.valueOf ( (char)Integer.parseInt ( ec, 16 ) ) );
                }
                catch ( final NumberFormatException e )
                {
                    throw new TokenizeException ( "valid escape sequence expected" );
                }
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
