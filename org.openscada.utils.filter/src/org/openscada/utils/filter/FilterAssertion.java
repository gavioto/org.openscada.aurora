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

package org.openscada.utils.filter;

import java.util.Collection;

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

    private String nullSafeToString ( final Object value )
    {
        if ( value == null )
        {
            return "";
        }
        else if ( value instanceof Collection<?> )
        {
            final Object[] valueList = ( (Collection<?>)value ).toArray ();
            return stringFromArray ( valueList );
        }
        else if ( value instanceof String[] )
        {
            // I am not sure if we really need that, multi values should
            // be provided in collections
            final Object[] valueList = (String[])value;
            return stringFromArray ( valueList );
        }
        else
        {
            return Encoder.encode ( value.toString () );
        }
    }

    private String stringFromArray ( final Object[] valueList )
    {
        final StringBuilder sb = new StringBuilder ();
        int i = 0;
        for ( final Object part : valueList )
        {
            if ( i > 0 && i < valueList.length )
            {
                sb.append ( "*" );
            }
            sb.append ( part == null ? "" : Encoder.encode ( part.toString () ) );
            i += 1;
        }
        return sb.toString ();
    }
}
