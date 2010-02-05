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
