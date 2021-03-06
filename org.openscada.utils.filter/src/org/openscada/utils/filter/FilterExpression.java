/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
            result.append ( "(" ); //$NON-NLS-1$
            result.append ( this.operator.toString () );
            for ( final Filter filter : this.filterSet )
            {
                result.append ( filter.toString () );
            }
            result.append ( ")" ); //$NON-NLS-1$
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

    @Override
    public boolean isAssertion ()
    {
        return false;
    }

    @Override
    public boolean isExpression ()
    {
        return true;
    }

    @Override
    public boolean isEmpty ()
    {
        return this.operator == null;
    }

    public static FilterExpression negate ( final Filter expression )
    {
        final FilterExpression negation = new FilterExpression ();
        negation.setOperator ( Operator.NOT );
        negation.getFilterSet ().add ( expression );
        return negation;
    }
}
