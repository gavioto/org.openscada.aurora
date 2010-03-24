/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.sec.osgi;

import java.util.Collection;
import java.util.LinkedList;

import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthenticationService;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class AuthenticationHelper
{
    private final ServiceTracker tracker;

    public AuthenticationHelper ( final BundleContext context )
    {
        this.tracker = new ServiceTracker ( context, AuthenticationService.class.getName (), null );
    }

    public void open ()
    {
        this.tracker.open ();
    }

    public void close ()
    {
        this.tracker.close ();
    }

    public UserInformation authenticate ( final String username, final String password ) throws AuthenticationException
    {
        final Collection<AuthenticationException> causes = new LinkedList<AuthenticationException> ();

        int services = 0;

        final Object[] s = this.tracker.getServices ();
        if ( s != null )
        {
            for ( final Object o : s )
            {
                if ( ! ( o instanceof AuthenticationService ) )
                {
                    continue;
                }

                try
                {
                    services++;
                    return ( (AuthenticationService)o ).authenticate ( username, password );
                }
                catch ( final AuthenticationException e )
                {
                    causes.add ( e );
                }
            }
        }

        throw new MultiAuthenticationException ( String.format ( "All of the %s authentication services rejected", services ), causes.toArray ( new AuthenticationException[causes.size ()] ) );
    }
}
