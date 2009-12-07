package org.openscada.utils.filter.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Tokens
{
    public static abstract class Token
    {
        public abstract String getType ();

        public abstract String getValue ();

        @Override
        public String toString ()
        {
            return getType () + " " + getValue ();
        }

        @Override
        public boolean equals ( Object obj )
        {
            if ( obj == this )
            {
                return true;
            }
            if ( obj == null )
            {
                return false;
            }
            return this.toString ().equals ( obj.toString () );
        }

        @Override
        public int hashCode ()
        {
            return this.toString ().hashCode ();
        }
    }

    public static class TokenLeftParen extends Token
    {
        @Override
        public String getType ()
        {
            return "LEFT_PAREN";
        }

        @Override
        public String getValue ()
        {
            return "(";
        }
    }

    public static class TokenRightParen extends Token
    {
        @Override
        public String getType ()
        {
            return "RIGHT_PAREN";
        }

        @Override
        public String getValue ()
        {
            return ")";
        }
    }

    public static class TokenOperator extends Token
    {

        private final String operator;

        private final static Map<String, TokenOperator> instances = new HashMap<String, TokenOperator> ();

        static
        {
            instances.put ( "&", new TokenOperator ( "&" ) );
            instances.put ( "|", new TokenOperator ( "|" ) );
            instances.put ( "!", new TokenOperator ( "!" ) );
        }

        public TokenOperator ( String operator )
        {
            this.operator = operator;
        }

        public static boolean isOperator ( String operator )
        {
            return instances.keySet ().contains ( operator );
        }

        public static TokenOperator getByValue ( String operator )
        {
            return instances.get ( operator );
        }

        @Override
        public String getType ()
        {
            return "OPERATOR";
        }

        @Override
        public String getValue ()
        {
            return this.operator;
        }
    }

    public static class TokenAssertion extends Token
    {

        private final String assertion;

        private final static Map<String, TokenAssertion> instances = new HashMap<String, TokenAssertion> ();

        static
        {
            instances.put ( "=", new TokenAssertion ( "=" ) );
            instances.put ( ">", new TokenAssertion ( ">" ) );
            instances.put ( ">=", new TokenAssertion ( ">=" ) );
            instances.put ( "<", new TokenAssertion ( "<" ) );
            instances.put ( "<=", new TokenAssertion ( "<=" ) );
            instances.put ( "~=", new TokenAssertion ( "~=" ) );
            instances.put ( "=*", new TokenAssertion ( "=*" ) );
        }

        public TokenAssertion ( String assertion )
        {
            this.assertion = assertion;
        }

        public static boolean isAssertion ( String assertion )
        {
            boolean result = false;
            for ( Entry<String, TokenAssertion> entry : instances.entrySet () )
            {
                result = result || assertion.startsWith ( entry.getKey () );
            }
            return result;
        }

        public static TokenAssertion getByValue ( String assertion )
        {
            return instances.get ( assertion );
        }

        @Override
        public String getType ()
        {
            return "ASSERTION";
        }

        @Override
        public String getValue ()
        {
            return this.assertion;
        }
    }

    public static class TokenAttribute extends Token
    {

        private final String value;

        public TokenAttribute ( String value )
        {
            this.value = value;
        }

        @Override
        public String getType ()
        {
            return "ATTRIBUTE";
        }

        @Override
        public String getValue ()
        {
            return this.value;
        }
    }

    public static class TokenValue extends Token
    {

        private final String value;

        public TokenValue ( String value )
        {
            this.value = value;
        }

        @Override
        public String getType ()
        {
            return "VALUE";
        }

        @Override
        public String getValue ()
        {
            return this.value;
        }
    }

    public static TokenLeftParen tokenLeftParen = new TokenLeftParen ();

    public static TokenRightParen tokenRightParen = new TokenRightParen ();
}
