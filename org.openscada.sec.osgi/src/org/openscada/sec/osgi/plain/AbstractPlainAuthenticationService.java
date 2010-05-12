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

package org.openscada.sec.osgi.plain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthenticationService;
import org.openscada.sec.StatusCodes;
import org.openscada.sec.UserInformation;
import org.openscada.utils.lang.Immutable;

public abstract class AbstractPlainAuthenticationService implements AuthenticationService
{

    @Immutable
    protected static class UserEntry implements Serializable
    {
        private static final long serialVersionUID = -6899786759766310861L;

        private final String password;

        private final Set<String> roles;

        public UserEntry ( final String password, final Collection<String> roles )
        {
            this.password = password;
            this.roles = new HashSet<String> ( roles );
        }

        public String getPassword ()
        {
            return this.password;
        }

        public Set<String> getRoles ()
        {
            return Collections.unmodifiableSet ( this.roles );
        }

        @Override
        public int hashCode ()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ( this.password == null ? 0 : this.password.hashCode () );
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
            final UserEntry other = (UserEntry)obj;
            if ( this.password == null )
            {
                if ( other.password != null )
                {
                    return false;
                }
            }
            else if ( !this.password.equals ( other.password ) )
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
            sb.append ( "UserEntry [password=" );
            sb.append ( this.password );
            sb.append ( ", roles=" );
            for ( final String role : this.roles )
            {
                sb.append ( "," );
                sb.append ( role );
            }
            sb.append ( "]" );
            return sb.toString ();
        }
    }

    public AbstractPlainAuthenticationService ()
    {
        super ();
    }

    protected abstract UserEntry getUserEntry ( final String name ) throws Exception;

    public UserInformation authenticate ( final String username, final String password ) throws AuthenticationException
    {
        UserEntry user;
        try
        {
            user = getUserEntry ( username );
        }
        catch ( final Exception e )
        {
            throw new AuthenticationException ( StatusCodes.UNKNOWN_STATUS_CODE, "Failed to retrieve user data", e );
        }

        if ( user == null )
        {
            // user is unknown
            throw new AuthenticationException ( StatusCodes.INVALID_USER_OR_PASSWORD );
        }

        if ( user.getPassword () == null )
        {
            return makeInfo ( username, user );
        }
        if ( !user.getPassword ().equals ( password ) )
        {
            throw new AuthenticationException ( StatusCodes.INVALID_USER_OR_PASSWORD );
        }

        return makeInfo ( username, user );
    }

    protected UserInformation makeInfo ( final String name, final UserEntry user )
    {
        return new UserInformation ( name, user.getRoles () );
    }

}