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

package org.openscada.utils.osgi.pool;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleObjectPoolServiceTracker extends AbstractObjectPoolServiceTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( SingleObjectPoolServiceTracker.class );

    private static final int DEFAULT_PRIORITY = 0;

    public interface ServiceListener
    {
        public void serviceChange ( Object service, Dictionary<?, ?> properties );
    }

    private final ServiceListener listener;

    public SingleObjectPoolServiceTracker ( final ObjectPoolTracker poolTracker, final String serviceId, final ServiceListener listener )
    {
        super ( poolTracker, serviceId );
        this.listener = listener;
    }

    protected int getPriority ( final Dictionary<?, ?> properties )
    {
        return getPriority ( properties, DEFAULT_PRIORITY );
    }

    protected int getPriority ( final Dictionary<?, ?> properties, final int defaultPriority )
    {
        final Object o = properties.get ( Constants.SERVICE_RANKING );
        if ( o instanceof Number )
        {
            return ( (Number)o ).intValue ();
        }
        else
        {
            return defaultPriority;
        }
    }

    private final Map<Object, Dictionary<?, ?>> services = new HashMap<Object, Dictionary<?, ?>> ();

    private Object currentService;

    @Override
    protected synchronized void handleServiceAdded ( final Object service, final Dictionary<?, ?> properties )
    {
        this.services.put ( service, properties );
        update ();
    }

    @Override
    protected synchronized void handleServiceModified ( final Object service, final Dictionary<?, ?> properties )
    {
        this.services.put ( service, properties );
        update ();
    }

    @Override
    protected synchronized void handleServiceRemoved ( final Object service, final Dictionary<?, ?> properties )
    {
        this.services.remove ( service );
        update ();
    }

    protected void update ()
    {
        Object bestService = null;
        Dictionary<?, ?> bestProperties = null;
        final int bestPriority = Integer.MIN_VALUE;

        for ( final Map.Entry<Object, Dictionary<?, ?>> entry : this.services.entrySet () )
        {
            final int priority = getPriority ( entry.getValue () );
            if ( priority > bestPriority )
            {
                bestService = entry.getKey ();
                bestProperties = entry.getValue ();
            }
        }

        setService ( bestService, bestProperties );
    }

    private synchronized void setService ( final Object bestService, final Dictionary<?, ?> bestProperties )
    {
        if ( this.currentService != bestService )
        {
            logger.debug ( "Change service: {} -> {}", new Object[] { this.currentService, bestService } );
            this.currentService = bestService;
            fireServiceChange ( bestService, bestProperties );
        }
    }

    private void fireServiceChange ( final Object bestService, final Dictionary<?, ?> bestProperties )
    {
        this.listener.serviceChange ( bestService, bestProperties );
    }

}
