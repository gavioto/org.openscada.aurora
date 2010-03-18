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

package org.openscada.utils.lang;

public class Pair<T1, T2>
{
    public T1 first = null;

    public T2 second = null;

    public Pair ( final T1 first, final T2 second )
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.first == null ? 0 : this.first.hashCode () );
        result = prime * result + ( this.second == null ? 0 : this.second.hashCode () );
        return result;
    }

    @SuppressWarnings ( "unchecked" )
    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass () != obj.getClass () )
        {
            return false;
        }
        final Pair other = (Pair)obj;
        if ( this.first == null )
        {
            if ( other.first != null )
            {
                return false;
            }
        }
        else if ( !this.first.equals ( other.first ) )
        {
            return false;
        }
        if ( this.second == null )
        {
            if ( other.second != null )
            {
                return false;
            }
        }
        else if ( !this.second.equals ( other.second ) )
        {
            return false;
        }
        return true;
    }
}
