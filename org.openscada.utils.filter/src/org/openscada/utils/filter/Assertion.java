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

    public static Assertion fromString ( final String op )
    {
        if ( "=".equals ( op ) ) //$NON-NLS-1$
        {
            return EQUALITY;
        }
        else if ( "=*".equals ( op ) ) //$NON-NLS-1$
        {
            return PRESENCE;
        }
        else if ( ">=".equals ( op ) ) //$NON-NLS-1$
        {
            return GREATEREQ;
        }
        else if ( ">".equals ( op ) ) //$NON-NLS-1$
        {
            return GREATERTHAN;
        }
        else if ( "<=".equals ( op ) ) //$NON-NLS-1$
        {
            return LESSEQ;
        }
        else if ( "<".equals ( op ) ) //$NON-NLS-1$
        {
            return LESSTHAN;
        }
        else if ( "~=".equals ( op ) ) //$NON-NLS-1$
        {
            return APPROXIMATE;
        }
        return null;
    }

    @Override
    public String toString ()
    {
        switch ( this.op )
        {
        case 0:
            return "="; //$NON-NLS-1$
        case 1:
            return "=*"; //$NON-NLS-1$
        case 2:
            return "="; //$NON-NLS-1$
        case 3:
            return ">="; //$NON-NLS-1$
        case 4:
            return "<="; //$NON-NLS-1$
        case 6:
            return "~="; //$NON-NLS-1$
        case 7:
            return "<"; //$NON-NLS-1$
        case 8:
            return ">"; //$NON-NLS-1$
        }
        return null;
    }

    public int toValue ()
    {
        return this.op;
    }
}