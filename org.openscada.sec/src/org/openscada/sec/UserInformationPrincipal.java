/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.security.Principal;

import org.openscada.utils.lang.Immutable;

@Immutable
public class UserInformationPrincipal implements Principal
{
    private final UserInformation userInformation;

    /**
     * Create an new principal object from a user information
     * @param userInformation the user information or <code>null</code> if none is available
     * @return a new {@link UserInformationPrincipal} instance of <code>null</code> if the
     * parameter userInformation was null
     */
    public static UserInformationPrincipal create ( final UserInformation userInformation )
    {
        if ( userInformation == null )
        {
            return null;
        }
        else
        {
            return new UserInformationPrincipal ( userInformation );
        }
    }

    /**
     * Create a new principal object
     * <p>
     * For creating a new object with easier <code>null</code>
     * handling see {@link #create(UserInformation)}
     * </p>
     * @param userInformation the user information that is the source of the information.
     * This parameter must not be <code>null</code>
     * @throws NullPointerException in the case the userInformation parameter is <code>null</code>
     * @see #create(UserInformation)
     */
    public UserInformationPrincipal ( final UserInformation userInformation )
    {
        if ( userInformation == null )
        {
            throw new NullPointerException ();
        }
        this.userInformation = userInformation;
    }

    @Override
    public String getName ()
    {
        return this.userInformation.getName ();
    }

    @Override
    public String toString ()
    {
        return this.userInformation.getName ();
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.userInformation == null ? 0 : this.userInformation.hashCode () );
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
        final UserInformationPrincipal other = (UserInformationPrincipal)obj;
        if ( this.userInformation == null )
        {
            if ( other.userInformation != null )
            {
                return false;
            }
        }
        else if ( !this.userInformation.equals ( other.userInformation ) )
        {
            return false;
        }
        return true;
    }

}
