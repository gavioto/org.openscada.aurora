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

import java.util.Map;

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
     * Authorizes a requested operation
     * @param objectId The object id on which the request should be performed
     * @param objectType The type of object
     * @param action The action to be performed
     * @param userInformation The user information or <code>null</code> if
     * there is no user information the user is anonymous.
     * @param context Additional information that can be used by the implementations.
     * The content must not be modified. The context may be <code>null</code> if no data
     * would be present.
     * @return Returns an authorization result if the implementation known something
     * about the requested authorization and <code>null</code> if the service can neither
     * approve or reject the request.
     */
    public AuthorizationResult authorize ( String objectType, String objectId, String action, UserInformation userInformation, Map<String, Object> context );
}
