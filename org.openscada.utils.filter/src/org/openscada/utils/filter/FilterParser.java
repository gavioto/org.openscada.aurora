/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.filter;

import java.util.ArrayList;
import java.util.EmptyStackException;
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

    public FilterParser ( final String filter ) throws FilterParseException
    {
        if ( filter == null || "".equals ( filter.trim () ) )
        {
            this.filter = new FilterEmpty ();
            return;
        }
        boolean expressionExpected = true;
        FilterAssertion currentAssertion = null;
        final Stack<FilterExpression> filterExpressions = new Stack<FilterExpression> ();
        Filter result = null;
        try
        {
            for ( final Token token : new Tokenizer ( filter ) )
            {
                if ( token instanceof TokenLeftParen )
                {
                    expressionExpected = false;
                    continue;
                }
                if ( token instanceof TokenOperator )
                {
                    expressionExpected = true;
                    final FilterExpression expression = new FilterExpression ();
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
                        try
                        {
                            validate ( filterExpressions.pop () );
                        }
                        catch ( final EmptyStackException e )
                        {
                            throw new FilterParseException ( "expression expected" );
                        }
                    }
                    continue;
                }
            }
        }
        catch ( final TokenizeException e )
        {
            throw new FilterParseException ( e.getMessage () );
        }
        validate ( result );
        if ( !result.toString ().equals ( filter ) )
        {
            throw new FilterParseException ( "expression ambiguos; expected " + result.toString () );
        }
        this.filter = result;
    }

    private void validate ( final Filter toValidate )
    {
        if ( toValidate == null )
        {
            throw new FilterParseException ( "no filter given" );
        }
        if ( toValidate.isAssertion () )
        {
            final FilterAssertion assertion = (FilterAssertion)toValidate;
            if ( assertion.getAttribute () == null )
            {
                throw new FilterParseException ( "no attribute given" );
            }
            if ( assertion.getAssertion () == null )
            {
                throw new FilterParseException ( "no assertion given" );
            }
            if ( assertion.getValue () == null )
            {
                throw new FilterParseException ( "no value given" );
            }
        }
        else if ( toValidate.isExpression () )
        {
            final FilterExpression expression = (FilterExpression)toValidate;
            if ( expression.getOperator () == null )
            {
                throw new FilterParseException ( "no operator given" );
            }
            if ( expression.getFilterSet ().size () == 0 )
            {
                throw new FilterParseException ( "missing sub expression" );
            }
        }
    }

    public Filter getFilter ()
    {
        return this.filter;
    }

    private Object toValue ( final FilterAssertion currentAssertion, final String value ) throws TokenizeException
    {
        if ( currentAssertion.getAssertion () == Assertion.EQUALITY && value.contains ( "*" ) )
        {
            currentAssertion.setAssertion ( Assertion.SUBSTRING );
            final List<String> result = new ArrayList<String> ();
            for ( final String part : value.split ( "\\*" ) )
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
