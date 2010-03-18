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

/**
 * @author jrose
 *
 */
public enum Operator
{
    OR ( 9 ),
    AND ( 10 ),
    NOT ( 11 );

    private final int op;

    Operator ( final int operator )
    {
        this.op = operator;
    }

    public static Operator fromValue ( final int op )
    {
        switch ( op )
        {
        case 9:
            return Operator.OR;
        case 10:
            return Operator.AND;
        case 11:
            return Operator.NOT;
        }
        return null;
    }

    @Override
    public String toString ()
    {
        switch ( this.op )
        {
        case 9:
            return "|";
        case 10:
            return "&";
        case 11:
            return "!";
        }
        return null;
    }

    public int toValue ()
    {
        return this.op;
    }
}
