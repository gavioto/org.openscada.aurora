/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2009-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.sec;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openscada.utils.lang.Immutable;

@Immutable
public class UserInformation
{
    private final String name;

    private final Set<String> roles;

    public UserInformation ( final String name, final Set<String> roles )
    {
        this.name = name;
        this.roles = new HashSet<String> ( roles );
    }

    public UserInformation ( final String name, final String[] roles )
    {
        this.name = name;
        this.roles = Collections.unmodifiableSet ( new HashSet<String> ( Arrays.asList ( roles ) ) );
    }

    public String getName ()
    {
        return this.name;
    }

    public Set<String> getRoles ()
    {
        // is unmodifiable
        return this.roles;
    }

    public boolean hasRole ( final String role )
    {
        return this.roles.contains ( role );
    }

    public boolean hasAllRoles ( final String[] roles )
    {
        return this.roles.containsAll ( Arrays.asList ( roles ) );
    }

    public boolean hasAllRoles ( final Collection<String> roles )
    {
        return this.roles.containsAll ( roles );
    }

    public boolean hasAnyRole ( final String[] roles )
    {
        for ( final String role : roles )
        {
            if ( this.roles.contains ( role ) )
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyRole ( final Collection<String> roles )
    {
        for ( final String role : roles )
        {
            if ( this.roles.contains ( role ) )
            {
                return true;
            }
        }
        return false;
    }

}
