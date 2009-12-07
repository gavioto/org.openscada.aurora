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

    public static String decode ( String toDecode )
    {
        StringBuilder sb = new StringBuilder ();
        for ( int i = 0; i < toDecode.length (); i++ )
        {
            char c = toDecode.charAt ( i );
            if ( c == '\\' )
            {
                String ec = "" + toDecode.charAt ( i + 1 ) + toDecode.charAt ( i + 2 );
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
