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

package org.openscada.sec.osgi.plain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openscada.sec.AuthenticationException;
import org.openscada.sec.StatusCodes;
import org.openscada.sec.UserInformation;
import org.openscada.utils.lang.Immutable;

public abstract class AbstractPlainAuthenticationService
{

    @Immutable
    protected static class UserEntry
    {
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
            // user is unknown .. but don't tell that to the client ;-)
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