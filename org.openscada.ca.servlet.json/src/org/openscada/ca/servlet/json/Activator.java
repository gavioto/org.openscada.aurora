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
    public void start ( BundleContext context ) throws Exception
    {
        this.context = context;

        ServiceTrackerCustomizer configurationAdminCustomizer = createConfigurationAdminCustomizer ();
        configurationAdminTracker = new ServiceTracker ( context, ConfigurationAdministrator.class.getName (), configurationAdminCustomizer );
        ServiceTrackerCustomizer httpServiceCustomizer = createHttpServiceCustomizer ();
        httpServiceTracker = new ServiceTracker ( context, HttpService.class.getName (), httpServiceCustomizer );

        configurationAdminTracker.open ();
        httpServiceTracker.open ();
    }

    private ServiceTrackerCustomizer createConfigurationAdminCustomizer ()
    {
        return new ServiceTrackerCustomizer () {
            public Object addingService ( ServiceReference reference )
            {
                Object service = context.getService ( reference );
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

            public void modifiedService ( ServiceReference reference, Object service )
            {
                // pass
            }

            public void removedService ( ServiceReference reference, Object service )
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
            public Object addingService ( ServiceReference reference )
            {
                Object service = context.getService ( reference );
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

            public void modifiedService ( ServiceReference reference, Object service )
            {
                // pass
            }

            public void removedService ( ServiceReference reference, Object service )
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
    public void stop ( BundleContext context ) throws Exception
    {
        httpServiceTracker.close ();
        configurationAdminTracker.close ();
    }
}
