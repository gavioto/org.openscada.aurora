/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.ca;

import org.openscada.utils.lang.Immutable;

@Immutable
public class FactoryEvent
{

    public static enum Type
    {
        STATE,
        ADDED,
        REMOVED,
    }

    private final Factory factory;

    private final Type type;

    private final FactoryState state;

    public FactoryEvent ( final Type type, final Factory factory, final FactoryState state )
    {
        this.type = type;
        this.factory = factory;
        this.state = state;
    }

    public FactoryState getState ()
    {
        return this.state;
    }

    public Factory getFactory ()
    {
        return this.factory;
    }

    public Type getType ()
    {
        return this.type;
    }

    @Override
    public String toString ()
    {
        switch ( this.type )
        {
        case STATE:
            return String.format ( "%s -> %s : %s", this.factory.getId (), this.type, this.state );
        default:
            return String.format ( "%s -> %s", this.factory.getId (), this.type );
        }

    }
}
