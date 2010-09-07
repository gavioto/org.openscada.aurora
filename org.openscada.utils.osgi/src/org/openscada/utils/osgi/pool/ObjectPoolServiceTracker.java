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

package org.openscada.utils.osgi.pool;

import java.util.Dictionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPoolServiceTracker extends AbstractObjectPoolServiceTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( ObjectPoolServiceTracker.class );

    private final ObjectPoolListener clientListener;

    public ObjectPoolServiceTracker ( final ObjectPoolTracker poolTracker, final String serviceId, final ObjectPoolListener listener )
    {
        super ( poolTracker, serviceId );
        this.clientListener = listener;
        logger.debug ( "new pool service tracker for {}", serviceId );
    }

    @Override
    protected void handleServiceAdded ( final Object service, final Dictionary<?, ?> properties )
    {
        fireServiceAdded ( service, properties );
    }

    private void fireServiceAdded ( final Object service, final Dictionary<?, ?> properties )
    {
        this.clientListener.serviceAdded ( service, properties );
    }

    @Override
    protected void handleServiceModified ( final Object service, final Dictionary<?, ?> properties )
    {
        fireServiceModified ( service, properties );
    }

    private void fireServiceModified ( final Object service, final Dictionary<?, ?> properties )
    {
        this.clientListener.serviceModified ( service, properties );
    }

    @Override
    protected void handleServiceRemoved ( final Object service, final Dictionary<?, ?> properties )
    {
        fireServiceRemoved ( service, properties );
    }

    private void fireServiceRemoved ( final Object service, final Dictionary<?, ?> properties )
    {
        this.clientListener.serviceRemoved ( service, properties );
    }

}
