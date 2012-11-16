/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.osgi.jaxws;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractExporter implements ServiceListener
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractExporter.class );

    private final BundleContext context;

    public AbstractExporter ( final BundleContext context ) throws InvalidSyntaxException
    {
        this.context = context;

        final String filter = String.format ( "(%s=%s)", JaxWsExporter.EXPORT_ENABLED, true );
        synchronized ( this )
        {
            context.addServiceListener ( this, filter );
            final ServiceReference<?>[] refs = context.getServiceReferences ( (String)null, filter );
            if ( refs != null )
            {
                for ( final ServiceReference<?> ref : refs )
                {
                    addService ( ref );
                }
            }
        }
    }

    public void dispose ()
    {
        this.context.removeServiceListener ( this );
    }

    protected abstract void exportService ( final ServiceReference<?> reference, final Object service );

    @Override
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

    private void removeService ( final ServiceReference<?> serviceReference )
    {
        this.context.ungetService ( serviceReference );
        unexportService ( serviceReference );
    }

    protected abstract void unexportService ( ServiceReference<?> serviceReference );

    private void addService ( final ServiceReference<?> reference )
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

}
