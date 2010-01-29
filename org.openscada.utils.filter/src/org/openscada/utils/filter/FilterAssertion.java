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

package org.openscada.utils.filter;

import java.util.List;

import org.openscada.utils.filter.internal.Encoder;

public class FilterAssertion implements Filter
{

    private String attribute;

    private Assertion assertion;

    private Object value;

    public FilterAssertion ( final String attribute, final Assertion assertion, final Object value )
    {
        this.attribute = attribute;
        this.assertion = assertion;
        this.value = value;
    }

    public String getAttribute ()
    {
        return this.attribute;
    }

    public void setAttribute ( final String attribute )
    {
        this.attribute = attribute;
    }

    public Assertion getAssertion ()
    {
        return this.assertion;
    }

    public void setAssertion ( final Assertion assertion )
    {
        this.assertion = assertion;
    }

    public Object getValue ()
    {
        return this.value;
    }

    public void setValue ( final Object value )
    {
        this.value = value;
    }

    @Override
    public String toString ()
    {
        return "(" + this.attribute + this.assertion.toString () + nullSafeToString ( this.value ) + ")";
    }

    public boolean isAssertion ()
    {
        return true;
    }

    public boolean isExpression ()
    {
        return false;
    }

    public boolean isEmpty ()
    {
        return this.assertion == null;
    }

    @SuppressWarnings ( "unchecked" )
    private String nullSafeToString ( final Object value )
    {
        if ( value == null )
        {
            return "";
        }
        if ( value instanceof List )
        {
            final List valueList = (List)value;
            final StringBuilder sb = new StringBuilder ();
            int i = 0;
            for ( final Object part : valueList )
            {
                if ( i > 0 && i < valueList.size () )
                {
                    sb.append ( "*" );
                }
                sb.append ( part == null ? "" : Encoder.encode ( part.toString () ) );
                i += 1;
            }
            return sb.toString ();
        }
        else
        {
            return Encoder.encode ( value.toString () );
        }
    }
}
