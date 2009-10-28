package org.openscada.utils.filter.internal;

import java.nio.charset.Charset;

public class Encoder
{

    public static final byte CHAR_ASTERISK = '*';

    public static final byte CHAR_PAREN_LEFT = '(';

    public static final byte CHAR_PAREN_RIGHT = ')';

    public static final byte CHAR_BACKSPACE = 0x5c;

    public static String encode ( String toEncode )
    {
        StringBuilder sb = new StringBuilder ();
        for ( byte b : toEncode.getBytes ( Charset.forName ( "UTF-8" ) ) )
        {
            switch ( b )
            {
            case CHAR_ASTERISK:
            case CHAR_PAREN_LEFT:
            case CHAR_PAREN_RIGHT:
            case CHAR_BACKSPACE:
                sb.append ( "\\" + String.format ( "%02x", new Object[] { b & 0xff } ) );
                break;
            default:
                if ( b < 20 || b > 127 )
                {
                    sb.append ( "\\" + String.format ( "%02x", new Object[] { b & 0xff } ) );
                }
                else
                {
                    sb.append ( new Character ( (char)b ) );
                }
            }
        }
        return sb.toString ();
    }
}
