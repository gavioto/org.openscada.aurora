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
public enum Assertion
{

    EQUALITY ( 0 ),
    PRESENCE ( 1 ),
    SUBSTRING ( 2 ),
    GREATEREQ ( 3 ),
    LESSEQ ( 4 ),
    APPROXIMATE ( 6 ),

    LESSTHAN ( 7 ),
    GREATERTHAN ( 8 );

    private final int op;

    Assertion ( final int operator )
    {
        this.op = operator;
    }

    public static Assertion fromValue ( final int op )
    {
        switch ( op )
        {
        case 0:
            return Assertion.EQUALITY;
        case 1:
            return Assertion.PRESENCE;
        case 2:
            return Assertion.SUBSTRING;
        case 3:
            return Assertion.GREATEREQ;
        case 4:
            return Assertion.LESSEQ;
        case 6:
            return Assertion.APPROXIMATE;
        case 7:
            return Assertion.LESSTHAN;
        case 8:
            return Assertion.GREATERTHAN;
        }
        return null;
    }

    @Override
    public String toString ()
    {
        switch ( this.op )
        {
        case 0:
            return "=";
        case 1:
            return "=*";
        case 2:
            return "=";
        case 3:
            return ">=";
        case 4:
            return "<=";
        case 6:
            return "~=";
        case 7:
            return "<";
        case 8:
            return ">";
        }
        return null;
    }

    public int toValue ()
    {
        return this.op;
    }
}