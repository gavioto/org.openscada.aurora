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

package org.openscada.sec.authz;

import java.util.HashMap;
import java.util.Map;

import org.openscada.sec.AuthorizationRequest;
import org.openscada.sec.UserInformation;
import org.openscada.sec.callback.CallbackHandler;

/**
 * @since 1.1
 */
public class AuthorizationContext
{
    private AuthorizationRequest request;

    private final Map<String, Object> context = new HashMap<String, Object> ();

    private CallbackHandler callbackHandler;

    public void setCallbackHandler ( final CallbackHandler callbackHandler )
    {
        this.callbackHandler = callbackHandler;
    }

    public CallbackHandler getCallbackHandler ()
    {
        return this.callbackHandler;
    }

    public Map<String, Object> getContext ()
    {
        return this.context;
    }

    public void setRequest ( final AuthorizationRequest request )
    {
        this.request = request;
    }

    public AuthorizationRequest getRequest ()
    {
        return this.request;
    }

    /**
     * Change the user information to the provided user information
     * 
     * @param userInformation
     *            the new user information
     */
    public void changeUserInformation ( final UserInformation userInformation )
    {
        this.request = AuthorizationRequest.changeUser ( this.request, userInformation );
    }
}
