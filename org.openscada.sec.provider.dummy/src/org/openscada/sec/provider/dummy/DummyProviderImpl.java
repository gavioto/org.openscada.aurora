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

package org.openscada.sec.provider.dummy;

import org.openscada.sec.AuthenticationService;
import org.openscada.sec.UserInformation;

/**
 * A dummy provider which returns an anonymous 
 * user information (<code>null</code>) if the username
 * is not set and an authenticated user with no roles
 * if the username is set
 *  
 * @author Jens Reimann
 *
 */
public class DummyProviderImpl implements AuthenticationService
{

    public UserInformation authenticate ( final String username, final String password )
    {
        if ( username != null )
        {
            return new UserInformation ( username, new String[0] );
        }
        else
        {
            return null;
        }
    }

}
