/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.sec;

import java.io.Serializable;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openscada.utils.lang.Immutable;
import org.openscada.utils.str.StringHelper;

/**
 * A user information object
 * 
 * @author Jens Reimann
 * @since 0.3.0
 */
@Immutable
public class UserInformation implements Serializable
{
    private static final long serialVersionUID = 1L;

    public final static UserInformation ANONYMOUS = new UserInformation ( null, null, Collections.<String> emptySet () );

    /**
     * The name of the user or <code>null</code> if it is an anonymous user
     * information.
     */
    private final String name;

    private final String password;

    private final Set<String> roles;

    /**
     * @since 1.1
     */
    public UserInformation ( final String name )
    {
        this ( name, null );
    }

    public UserInformation ( final String name, final String password )
    {
        this.name = name;
        this.password = password;
        this.roles = Collections.emptySet ();
    }

    public UserInformation ( final String name, final String password, final Collection<String> roles )
    {
        this.name = name;
        this.password = password;
        if ( roles != null )
        {
            this.roles = Collections.unmodifiableSet ( new HashSet<String> ( roles ) );
        }
        else
        {
            this.roles = Collections.emptySet ();
        }
    }

    public UserInformation ( final String name, final String password, final String[] roles )
    {
        this ( name, password, Arrays.asList ( roles ) );
    }

    /**
     * Create a user information object
     * 
     * @param principal
     * @return Returns the converted user information object or
     *         {@link #ANONYMOUS} if principal was <code>null</code>. Never
     *         returns <code>null</code>.
     */
    public static UserInformation fromPrincipal ( final Principal principal )
    {
        if ( principal == null )
        {
            return ANONYMOUS;
        }
        return new UserInformation ( principal.getName (), null );
    }

    public boolean isAnonymous ()
    {
        return this.name == null;
    }

    /**
     * Get the name of the user
     * 
     * @return the name of the user or <code>null</code> if it an anonymous user
     *         information
     */
    public String getName ()
    {
        return this.name;
    }

    public String getPassword ()
    {
        return this.password;
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

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.name == null ? 0 : this.name.hashCode () );
        result = prime * result + ( this.roles == null ? 0 : this.roles.hashCode () );
        return result;
    }

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
        final UserInformation other = (UserInformation)obj;
        if ( this.name == null )
        {
            if ( other.name != null )
            {
                return false;
            }
        }
        else if ( !this.name.equals ( other.name ) )
        {
            return false;
        }
        if ( this.roles == null )
        {
            if ( other.roles != null )
            {
                return false;
            }
        }
        else if ( !this.roles.equals ( other.roles ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ();
        sb.append ( "[UserInformation - name=" );
        if ( this.name != null )
        {
            sb.append ( this.name );
        }
        else
        {
            sb.append ( "<anonymous>" );
        }
        sb.append ( ", roles=[" );
        sb.append ( StringHelper.join ( this.roles, ", " ) );
        sb.append ( "]]" );
        return sb.toString ();
    }
}
