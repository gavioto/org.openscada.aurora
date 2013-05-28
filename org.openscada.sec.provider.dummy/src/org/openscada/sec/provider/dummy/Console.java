/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.sec.provider.dummy;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.sec.AuthenticationService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Console
{

    private final DummyAuthenticationProviderImpl authenticationService;

    private final int priority = Integer.getInteger ( "org.openscada.sec.provider.dummy.priority", Integer.MIN_VALUE );

    private final int authenticationPriority = Integer.getInteger ( "org.openscada.sec.provider.dummy.authentication.priority", this.priority );

    private BundleContext context;

    private ServiceRegistration<?> authnHandle;

    public Console ()
    {
        this.authenticationService = new DummyAuthenticationProviderImpl ();
    }

    public void activate ( final BundleContext context )
    {
        this.context = context;
    }

    public synchronized void enableDummyAuthentication ()
    {
        if ( this.authnHandle != null )
        {
            return;
        }

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();
        properties.put ( Constants.SERVICE_DESCRIPTION, "A dummy authentication service" );
        properties.put ( Constants.SERVICE_VENDOR, "openSCADA.org" );
        properties.put ( Constants.SERVICE_RANKING, this.authenticationPriority );

        System.out.println ( String.format ( "Injecting dummy authentication service with priority: %s", this.authenticationPriority ) );
        this.authnHandle = this.context.registerService ( AuthenticationService.class.getName (), this.authenticationService, properties );
    }

    public synchronized void disableDummyAuthentication ()
    {
        if ( this.authnHandle != null )
        {
            System.out.println ( "Removing dummy authentication service" );
            this.authnHandle.unregister ();
            this.authnHandle = null;
        }
    }

    public void dispose ()
    {
        disableDummyAuthentication ();
    }

}
