package org.openscada.ca.servelt.axis;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@WebService
public class ConfigurationAdministratorExportImpl implements ConfigurationAdministratorExport
{
    private final SingleServiceTracker tracker;

    private volatile ConfigurationAdministrator service;

    public ConfigurationAdministratorExportImpl ( final BundleContext context )
    {
        this.tracker = new SingleServiceTracker ( context, ConfigurationAdministrator.class.getName (), new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                ConfigurationAdministratorExportImpl.this.setService ( (ConfigurationAdministrator)service );
            }
        } );
        this.tracker.open ();
    }

    protected void setService ( final ConfigurationAdministrator service )
    {
        this.service = service;
    }

    @WebMethod ( exclude = true )
    public void dispose ()
    {
        this.tracker.close ();
    }

    public boolean hasService ()
    {
        return this.service != null;
    }
}
