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
 * An interface for a service authorizing an operation 
 * <p>
 * The authorization service only acts on already authenticated
 * user information objects.
 * </p>
 * @author Jens Reimann
 * @since 0.15.0
 *
 */
public interface AuthorizationService
{
    /**
     * Authorizes a requsted operation
     * @param objectId The object id on which the request should be performed
     * @param objectType The type of object
     * @param action The action to be performed
     * @param userInformation The user information or <code>null</code> if
     * there is no user information the user is anonymous.
     * @return Returns a authorization result and never returns <code>null</code>.
     */
    public AuthorizationResult authorize ( String objectId, String objectType, String action, UserInformation userInformation );
}
