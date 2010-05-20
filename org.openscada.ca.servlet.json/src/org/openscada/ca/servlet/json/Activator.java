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

package org.openscada.ca.servlet.json;

import javax.servlet.ServletException;

import org.openscada.ca.ConfigurationAdministrator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator
{

    private BundleContext context;

    private ServiceTracker configurationAdminTracker;

    private ServiceTracker httpServiceTracker;

    private ConfigurationAdministrator configurationAdmin;

    private HttpService httpService;

    private HttpContext httpContext;

    private JsonServlet servlet;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.context = context;

        final ServiceTrackerCustomizer configurationAdminCustomizer = createConfigurationAdminCustomizer ();
        this.configurationAdminTracker = new ServiceTracker ( context, ConfigurationAdministrator.class.getName (), configurationAdminCustomizer );
        final ServiceTrackerCustomizer httpServiceCustomizer = createHttpServiceCustomizer ();
        this.httpServiceTracker = new ServiceTracker ( context, HttpService.class.getName (), httpServiceCustomizer );

        this.configurationAdminTracker.open ();
        this.httpServiceTracker.open ();
    }

    private ServiceTrackerCustomizer createConfigurationAdminCustomizer ()
    {
        return new ServiceTrackerCustomizer () {
            public Object addingService ( final ServiceReference reference )
            {
                final Object service = Activator.this.context.getService ( reference );
                synchronized ( Activator.this )
                {
                    if ( Activator.this.configurationAdmin == null )
                    {
                        Activator.this.configurationAdmin = (ConfigurationAdministrator)service;
                        Activator.this.bind ();
                    }
                }
                return service;
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
                // pass
            }

            public void removedService ( final ServiceReference reference, final Object service )
            {
                synchronized ( Activator.this )
                {
                    if ( Activator.this.configurationAdmin != service )
                    {
                        return;
                    }
                    Activator.this.unbind ();
                    Activator.this.configurationAdmin = null;
                    Activator.this.bind ();
                }
            }
        };
    }

    private ServiceTrackerCustomizer createHttpServiceCustomizer ()
    {
        return new ServiceTrackerCustomizer () {
            public Object addingService ( final ServiceReference reference )
            {
                final Object service = Activator.this.context.getService ( reference );
                synchronized ( Activator.this )
                {
                    if ( Activator.this.httpService == null )
                    {
                        Activator.this.httpService = (HttpService)service;
                        Activator.this.bind ();
                    }
                }
                return service;
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
                // pass
            }

            public void removedService ( final ServiceReference reference, final Object service )
            {
                synchronized ( Activator.this )
                {
                    if ( Activator.this.httpService != service )
                    {
                        return;
                    }
                    Activator.this.unbind ();
                    Activator.this.httpService = null;
                    Activator.this.bind ();
                }
            }
        };
    }

    private void bind ()
    {
        if ( this.httpService != null && this.configurationAdmin != null )
        {
            this.httpContext = this.httpService.createDefaultHttpContext ();
            try
            {
                this.httpService.registerServlet ( "/org.openscada.ca", this.servlet = new JsonServlet ( this.configurationAdmin ), null, this.httpContext );
            }
            catch ( final NamespaceException e )
            {
                e.printStackTrace ();
            }
            catch ( final ServletException e )
            {
                e.printStackTrace ();
            }
        }
    }

    private void unbind ()
    {
        if ( this.httpService != null )
        {
            this.httpService.unregister ( "/org.openscada.ca" );

            this.servlet.destroy ();
            this.servlet = null;

            this.httpContext = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
        this.httpServiceTracker.close ();
        this.configurationAdminTracker.close ();
    }
}
