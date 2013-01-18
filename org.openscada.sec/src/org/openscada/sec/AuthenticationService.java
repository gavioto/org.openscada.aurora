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

package org.openscada.sec;

/**
 * An interface for a service that authenticates a user.
 * <p>
 * The service needs to ensure that the information provided identifies a valid
 * user.
 * </p>
 * 
 * @author Jens Reimann
 * @since 0.15.0
 */
public interface AuthenticationService
{
    /**
     * Authenticate a user based on username and password.
     * 
     * @param username
     *            the username
     * @param password
     *            the password
     * @return a valid user information instance if the user is a known user and
     *         is allowed to log on using the provided credentials. Or
     *         <code>null</code> if it is a valid anonymous login.
     * @throws AuthenticationException
     *             in the case the login is invalid (e.g. username and
     *             password don't match)
     */
    public UserInformation authenticate ( String username, String password ) throws AuthenticationException;
}
