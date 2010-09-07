/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.sec.AuthenticationService;
import org.openscada.sec.AuthorizationService;
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
            properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
            properties.put ( Constants.SERVICE_RANKING, this.authenticationPriority );

            context.registerService ( AuthenticationService.class.getName (), this.authenticationService, properties );
        }

        this.authorizationService = new DummyAuthorizationProviderImpl ();

        {
            final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
            properties.put ( Constants.SERVICE_DESCRIPTION, "A dummy authorization service" );
            properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
            properties.put ( Constants.SERVICE_RANKING, this.authorizationPriority );

            context.registerService ( AuthorizationService.class.getName (), this.authorizationService, properties );
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
