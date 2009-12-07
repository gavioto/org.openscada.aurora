package org.openscada.utils.filter.internal;

import java.util.Iterator;

import org.openscada.utils.filter.internal.Tokens.Token;
import org.openscada.utils.filter.internal.Tokens.TokenAssertion;
import org.openscada.utils.filter.internal.Tokens.TokenAttribute;
import org.openscada.utils.filter.internal.Tokens.TokenOperator;
import org.openscada.utils.filter.internal.Tokens.TokenValue;

public class Tokenizer implements Iterable<Token>, Iterator<Token>
{

    public static class TokenizeException extends RuntimeException
    {

        private static final long serialVersionUID = 2256865968011732768L;

        public TokenizeException ()
        {
            super ();
        }

        public TokenizeException ( String message, Throwable cause )
        {
            super ( message, cause );
        }

        public TokenizeException ( String message )
        {
            super ( message );
        }

        public TokenizeException ( Throwable cause )
        {
            super ( cause );
        }
    }

    private final char[] filter;

    private int pos = 0;

    private boolean isAttribute = false;

    private boolean isValue = false;

    private boolean isAssertion = false;

    private StringBuilder buffer;

    public Tokenizer ( String toParse )
    {
        filter = toParse.toCharArray ();
    }

    public Iterator<Token> iterator ()
    {
        return this;
    }

    public boolean hasNext ()
    {
        return filter != null && pos < filter.length;
    }

    public Token next ()
    {
        int overflow = 0;
        for ( ;; )
        {
            overflow += 1;
            if ( pos >= filter.length || overflow > filter.length )
            {
                throw new TokenizeException ( "incorrect Syntax at pos " + pos );
            }
            final String currentChar = String.valueOf ( filter[pos] );

            if ( currentChar.equals ( Tokens.tokenLeftParen.getValue () ) ) // left paren
            {
                pos += 1;
                buffer = new StringBuilder ();
                return Tokens.tokenLeftParen;

            }
            else if ( currentChar.equals ( Tokens.tokenRightParen.getValue () ) ) // right paren
            {
                if ( isValue )
                {
                    isValue = false;
                    return new TokenValue ( buffer.toString () );
                }
                pos += 1;
                return Tokens.tokenRightParen;

            }
            else if ( TokenOperator.isOperator ( currentChar ) ) // operator
            {
                pos += 1;
                return TokenOperator.getByValue ( currentChar );

            }
            else if ( !isAttribute && !isAssertion && !isValue ) // attribute started
            {
                isAttribute = true;
                continue;
            }
            else if ( isAttribute && !isAssertion && !isValue ) // continue or end attribute
            {
                if ( pos + 1 >= filter.length )
                {
                    throw new TokenizeException ( "incorrect Syntax at pos " + pos );
                }
                String lookahead = currentChar + filter[pos + 1] + ( filter.length > pos + 2 ? filter[pos + 2] : "" );
                if ( TokenAssertion.isAssertion ( lookahead ) )
                {
                    isAssertion = true;
                    isAttribute = false;
                    final TokenAttribute token = new TokenAttribute ( buffer.toString () );
                    buffer = new StringBuilder ();
                    return token;
                }
                else
                {
                    if ( buffer == null )
                    {
                        throw new TokenizeException ( "incorrect Syntax at pos " + pos );
                    }
                    buffer.append ( currentChar );
                    pos += 1;
                    continue;
                }
            }
            else if ( isAssertion ) // handle assertion
            {
                isAttribute = false;
                isAssertion = false;
                isValue = true;
                String lookahead2 = currentChar + filter[pos + 1];
                String lookahead3 = currentChar + filter[pos + 1] + ( filter.length > pos + 2 ? filter[pos + 2] : "" );
                // special case for presence
                if ( "=*)".equals ( lookahead3 ) )
                {
                    pos += 2;
                    return TokenAssertion.getByValue ( "=*" );
                }
                else if ( "=*".equals ( lookahead2 ) )
                {
                    pos += 1;
                    return TokenAssertion.getByValue ( "=" );
                }
                // all others are handled regularly
                if ( TokenAssertion.getByValue ( lookahead2 ) != null )
                {
                    pos += 2;
                    return TokenAssertion.getByValue ( lookahead2 );
                }
                pos += 1;
                return TokenAssertion.getByValue ( currentChar );

            }
            else if ( !isAttribute && !isAssertion && isValue ) // in any other case it is an value
            {
                buffer.append ( currentChar );
                pos += 1;
                continue;
            }
            throw new TokenizeException ( "incorrect Syntax at pos " + pos );
        }
    }

    public void remove ()
    {
        throw new UnsupportedOperationException ();
    }
}