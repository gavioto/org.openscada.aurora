package org.openscada.utils.osgi.jaxws;

import java.util.HashMap;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndpointExporter implements ServiceListener
{

    private final static Logger logger = LoggerFactory.getLogger ( EndpointExporter.class );

    private final BundleContext context;

    private final String baseAddress;

    private final Map<ServiceReference, Endpoint> endpoints = new HashMap<ServiceReference, Endpoint> ();

    public EndpointExporter ( final BundleContext context, final String baseAddress ) throws InvalidSyntaxException
    {
        this.context = context;
        this.baseAddress = baseAddress;

        final String filter = String.format ( "(%s=%s)", JaxWsExporter.EXPORT_ENABLED, true );
        synchronized ( this )
        {
            context.addServiceListener ( this, filter );
            final ServiceReference[] refs = context.getServiceReferences ( null, filter );
            if ( refs != null )
            {
                for ( final ServiceReference ref : refs )
                {
                    addService ( ref );
                }
            }
        }
    }

    public void dispose ()
    {
        this.context.removeServiceListener ( this );
        for ( final Endpoint e : this.endpoints.values () )
        {
            e.stop ();
        }
    }

    public synchronized void serviceChanged ( final ServiceEvent event )
    {
        switch ( event.getType () )
        {
        case ServiceEvent.REGISTERED:
            addService ( event.getServiceReference () );
            break;
        case ServiceEvent.UNREGISTERING:
            removeService ( event.getServiceReference () );
            break;
        }

    }

    private void removeService ( final ServiceReference serviceReference )
    {
        final Endpoint e = this.endpoints.remove ( serviceReference );
        if ( e != null )
        {
            e.stop ();
            this.context.ungetService ( serviceReference );
        }
    }

    private void addService ( final ServiceReference reference )
    {
        logger.debug ( "Found new service: {}", reference );

        Object service = this.context.getService ( reference );
        try
        {
            exportService ( reference, service );
            service = null;
        }
        finally
        {
            if ( service != null )
            {
                this.context.ungetService ( reference );
            }
        }
    }

    private void exportService ( final ServiceReference reference, final Object service )
    {
        logger.info ( "Exporting service: {} -> {}", new Object[] { reference } );
        Endpoint e = null;
        final ClassLoader currentClassLoader = Thread.currentThread ().getContextClassLoader ();

        try
        {

            final WebService webService = service.getClass ().getAnnotation ( WebService.class );

            Thread.currentThread ().setContextClassLoader ( service.getClass ().getClassLoader () );

            if ( webService != null )
            {
                e = Endpoint.create ( service );

                final String address = makeAddress ( reference, service, webService );
                e.publish ( address );
                this.endpoints.put ( reference, e );
                e = null;
            }
        }
        catch ( final Exception ex )
        {
            logger.warn ( "Failed to export", ex );
        }
        finally
        {
            Thread.currentThread ().setContextClassLoader ( currentClassLoader );
            if ( e != null )
            {
                e.stop ();
            }
        }
    }

    private String makeAddress ( final ServiceReference reference, final Object service, final WebService webService )
    {
        String serviceName = webService.serviceName ();
        if ( serviceName == null )
        {
            if ( reference.getProperty ( Constants.SERVICE_PID ) != null )
            {
                serviceName = reference.getProperty ( Constants.SERVICE_PID ).toString ();
            }
        }

        return this.baseAddress + "/" + serviceName;
    }
}
