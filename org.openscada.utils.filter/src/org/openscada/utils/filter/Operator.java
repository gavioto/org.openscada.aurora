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
