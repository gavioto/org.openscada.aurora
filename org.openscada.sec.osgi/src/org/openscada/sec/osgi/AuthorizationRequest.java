/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec.osgi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.sec.UserInformation;
import org.openscada.utils.lang.Immutable;

@Immutable
public class AuthorizationRequest
{
    private final String objectType;

    private final String objectId;

    private final String action;

    private final UserInformation userInformation;

    private final Map<String, Object> context;

    public AuthorizationRequest ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context )
    {
        this.objectType = objectType;
        this.objectId = objectId;
        this.action = action;
        this.userInformation = userInformation;
        this.context = context == null ? null : Collections.unmodifiableMap ( new HashMap<String, Object> ( context ) );
    }

    public String getAction ()
    {
        return this.action;
    }

    public Map<String, Object> getContext ()
    {
        return this.context;
    }

    public String getObjectId ()
    {
        return this.objectId;
    }

    public String getObjectType ()
    {
        return this.objectType;
    }

    public UserInformation getUserInformation ()
    {
        return this.userInformation;
    }

    @Override
    public String toString ()
    {
        return String.format ( "[type: %s, id: %s, action: %s, userInformation: %s, context: %s]", this.objectType, this.objectId, this.action, this.userInformation, this.context );
    }
}
