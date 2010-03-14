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

/**
 * An interface for a service that authenticates a user.
 * <p>
 * The service needs to ensure that the information provided identifies a valid user.
 * </p>
 * @author Jens Reimann
 * @since 0.15.0
 *
 */
public interface AuthenticationService
{
    /**
     * Authenticate a user based on username and password.
     * @param username the username
     * @param password the password
     * @return a valid user information instance of the user is a known user and is allowed
     * to log on using the provided credentials or <code>null</code> if it a valid
     * anonymous login.
     * @throws AuthenticationException in the case the login is invalid (e.g. username and
     * password don't match)
     */
    public UserInformation authenticate ( String username, String password ) throws AuthenticationException;
}
