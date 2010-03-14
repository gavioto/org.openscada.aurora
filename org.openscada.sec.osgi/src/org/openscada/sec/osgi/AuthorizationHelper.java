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

package org.openscada.sec.osgi;

import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.AuthorizationService;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class AuthorizationHelper
{
    private final ServiceTracker tracker;

    public AuthorizationHelper ( final BundleContext context )
    {
        this.tracker = new ServiceTracker ( context, AuthorizationService.class.getName (), null );
    }

    public void open ()
    {
        this.tracker.open ();
    }

    public void close ()
    {
        this.tracker.close ();
    }

    public AuthorizationResult authorize ( final String objectId, final String objectType, final String action, final UserInformation userInformation )
    {
        return authorize ( objectId, objectType, action, userInformation, AuthorizationResult.create ( StatusCodes.AUTHORIZATION_FAILED, "No authentication provider voted. Rejecting request!" ) );
    }

    public AuthorizationResult authorize ( final String objectId, final String objectType, final String action, final UserInformation userInformation, final AuthorizationResult defaultResult )
    {
        final Object[] s = this.tracker.getServices ();

        if ( s == null )
        {
            return defaultResult;
        }

        for ( final Object service : s )
        {
            if ( ! ( service instanceof AuthorizationService ) )
            {
                continue;
            }
            final AuthorizationResult result = ( (AuthorizationService)service ).authorize ( objectId, objectType, action, userInformation );
            if ( result != null )
            {
                return result;
            }
        }

        return defaultResult;
    }
}
