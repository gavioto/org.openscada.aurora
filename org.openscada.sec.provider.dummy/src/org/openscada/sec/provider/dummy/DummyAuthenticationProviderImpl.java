/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.sec.provider.dummy;

import org.openscada.sec.AuthenticationService;
import org.openscada.sec.UserInformation;
import org.openscada.sec.authn.CredentialsRequest;

/**
 * A dummy provider which returns an anonymous
 * user information (<code>null</code>) if the username
 * is not set and an authenticated user with no roles
 * if the username is set
 * 
 * @author Jens Reimann
 */
public class DummyAuthenticationProviderImpl implements AuthenticationService
{

    @Override
    public UserInformation authenticate ( final CredentialsRequest request )
    {
        return new UserInformation ( request.getUserName (), new String[0] );
    }

    @Override
    public void joinRequest ( final CredentialsRequest request )
    {
        request.askUsername ();
    }

}
