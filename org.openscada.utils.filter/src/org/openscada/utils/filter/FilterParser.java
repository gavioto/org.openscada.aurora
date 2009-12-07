package org.openscada.utils.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.openscada.utils.filter.internal.Encoder;
import org.openscada.utils.filter.internal.Tokenizer;
import org.openscada.utils.filter.internal.Tokenizer.TokenizeException;
import org.openscada.utils.filter.internal.Tokens.Token;
import org.openscada.utils.filter.internal.Tokens.TokenAssertion;
import org.openscada.utils.filter.internal.Tokens.TokenAttribute;
import org.openscada.utils.filter.internal.Tokens.TokenLeftParen;
import org.openscada.utils.filter.internal.Tokens.TokenOperator;
import org.openscada.utils.filter.internal.Tokens.TokenRightParen;
import org.openscada.utils.filter.internal.Tokens.TokenValue;

/**
 * @author jrose
 *
 */
public class FilterParser
{

    private final Filter filter;

    public FilterParser ( String filter ) throws FilterParseException
    {
        boolean expressionExpected = true;
        FilterAssertion currentAssertion = null;
        Stack<FilterExpression> filterExpressions = new Stack<FilterExpression> ();
        Filter result = null;
        try
        {
            for ( Token token : new Tokenizer ( filter ) )
            {
                if ( token instanceof TokenLeftParen )
                {
                    expressionExpected = false;
                    continue;
                }
                if ( token instanceof TokenOperator )
                {
                    expressionExpected = true;
                    FilterExpression expression = new FilterExpression ();
                    if ( "|".equals ( token.getValue () ) )
                    {
                        expression.setOperator ( Operator.OR );
                    }
                    else if ( "&".equals ( token.getValue () ) )
                    {
                        expression.setOperator ( Operator.AND );
                    }
                    else if ( "!".equals ( token.getValue () ) )
                    {
                        expression.setOperator ( Operator.NOT );
                    }
                    if ( filterExpressions.isEmpty () )
                    {
                        result = expression;
                    }
                    else
                    {
                        filterExpressions.peek ().getFilterSet ().add ( expression );
                    }
                    filterExpressions.push ( expression );
                    continue;
                }
                if ( expressionExpected )
                {
                    throw new FilterParseException ( "expression expected" );
                }
                if ( token instanceof TokenAttribute )
                {
                    currentAssertion = new FilterAssertion ( token.getValue (), null, "" );
                    continue;
                }
                if ( token instanceof TokenAssertion )
                {
                    if ( "=".equals ( token.getValue () ) )
                    {
                        currentAssertion.setAssertion ( Assertion.EQUALITY );
                    }
                    else if ( ">=".equals ( token.getValue () ) )
                    {
                        currentAssertion.setAssertion ( Assertion.GREATEREQ );
                    }
                    else if ( ">".equals ( token.getValue () ) )
                    {
                        currentAssertion.setAssertion ( Assertion.GREATERTHAN );
                    }
                    else if ( "<=".equals ( token.getValue () ) )
                    {
                        currentAssertion.setAssertion ( Assertion.LESSEQ );
                    }
                    else if ( "<".equals ( token.getValue () ) )
                    {
                        currentAssertion.setAssertion ( Assertion.LESSTHAN );
                    }
                    else if ( "~=".equals ( token.getValue () ) )
                    {
                        currentAssertion.setAssertion ( Assertion.APPROXIMATE );
                    }
                    else if ( "=*".equals ( token.getValue () ) )
                    {
                        currentAssertion.setAssertion ( Assertion.PRESENCE );
                    }
                    continue;
                }
                if ( token instanceof TokenValue )
                {
                    currentAssertion.setValue ( toValue ( currentAssertion, token.getValue () ) );
                    continue;
                }
                if ( token instanceof TokenRightParen )
                {
                    if ( result == null )
                    {
                        validate ( currentAssertion );
                        result = currentAssertion;
                        break;
                    }
                    if ( currentAssertion != null )
                    {
                        validate ( currentAssertion );
                        filterExpressions.peek ().getFilterSet ().add ( currentAssertion );
                        currentAssertion = null;
                    }
                    else
                    {
                        validate ( filterExpressions.pop () );
                    }
                    continue;
                }
            }
        }
        catch ( TokenizeException e )
        {
            throw new FilterParseException ( e.getMessage () );
        }
        validate ( result );
        this.filter = result;
    }

    private void validate ( Filter toValidate )
    {
        if ( toValidate == null )
        {
            throw new FilterParseException ();
        }
        if ( toValidate.isAssertion () )
        {
            FilterAssertion assertion = (FilterAssertion)toValidate;
            if ( assertion.getAttribute () == null )
            {
                throw new FilterParseException ();
            }
            if ( assertion.getAssertion () == null )
            {
                throw new FilterParseException ();
            }
            if ( assertion.getValue () == null )
            {
                throw new FilterParseException ();
            }
        }
        else if ( toValidate.isExpression () )
        {
            FilterExpression expression = (FilterExpression)toValidate;
            if ( expression.getOperator () == null )
            {
                throw new FilterParseException ();
            }
            if ( expression.getFilterSet ().size () == 0 )
            {
                throw new FilterParseException ();
            }
        }
    }

    public Filter getFilter ()
    {
        return filter;
    }

    private Object toValue ( FilterAssertion currentAssertion, String value )
    {
        if ( currentAssertion.getAssertion () == Assertion.EQUALITY && value.contains ( "*" ) )
        {
            currentAssertion.setAssertion ( Assertion.SUBSTRING );
            List<String> result = new ArrayList<String> ();
            for ( String part : value.split ( "\\*" ) )
            {
                result.add ( Encoder.decode ( part ) );
            }
            // split doesn't include trailing whitespace, so add it here manually
            if ( value.charAt ( value.length () - 1 ) == '*' )
            {
                result.add ( "" );
            }
            return result;
        }
        return Encoder.decode ( value );
    }
}
