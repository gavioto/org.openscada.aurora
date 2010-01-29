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

import java.util.ArrayList;
import java.util.List;

public class FilterExpression implements Filter
{

    private Operator operator;

    private final List<Filter> filterSet = new ArrayList<Filter> ();

    @Override
    public String toString ()
    {
        final StringBuilder result = new StringBuilder ();
        if ( this.operator == null )
        {
            for ( final Filter filter : this.filterSet )
            {
                result.append ( filter.toString () );
            }
        }
        else
        {
            result.append ( "(" );
            result.append ( this.operator.toString () );
            for ( final Filter filter : this.filterSet )
            {
                result.append ( filter.toString () );
            }
            result.append ( ")" );
        }
        return result.toString ();
    }

    public List<Filter> getFilterSet ()
    {
        return this.filterSet;
    }

    public Operator getOperator ()
    {
        return this.operator;
    }

    public void setOperator ( final Operator operator )
    {
        this.operator = operator;
    }

    public boolean isAssertion ()
    {
        return false;
    }

    public boolean isExpression ()
    {
        return true;
    }

    public boolean isEmpty ()
    {
        return this.operator == null;
    }
}
