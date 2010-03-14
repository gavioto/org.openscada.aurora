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

package org.openscada.sec.provider.dummy;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.sec.AuthenticationService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private DummyAuthenticationProviderImpl authenticationService;

    private final int priority = Integer.getInteger ( "org.openscada.sec.provider.dummy.priority", 0 );

    private final int authenticationPriority = Integer.getInteger ( "org.openscada.sec.provider.dummy.authentication.priority", this.priority );

    private final int authorizationPriority = Integer.getInteger ( "org.openscada.sec.provider.dummy.authorization.priority", this.priority );

    private DummyAuthorizationProviderImpl authorizationService;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.authenticationService = new DummyAuthenticationProviderImpl ();

        {
            final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "A dummy authentication service" );
            properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
            properties.put ( Constants.SERVICE_RANKING, this.authenticationPriority );

            context.registerService ( AuthenticationService.class.getName (), this.authenticationService, properties );
        }

        this.authorizationService = new DummyAuthorizationProviderImpl ();

        {
            final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "A dummy authorization service" );
            properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
            properties.put ( Constants.SERVICE_RANKING, this.authorizationPriority );

            context.registerService ( AuthenticationService.class.getName (), this.authorizationService, properties );
        }

    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
    }

}
