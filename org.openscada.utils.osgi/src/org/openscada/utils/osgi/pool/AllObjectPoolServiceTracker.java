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

import org.openscada.utils.osgi.pool.ObjectPoolTracker.ObjectPoolServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllObjectPoolServiceTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( AllObjectPoolServiceTracker.class );

    private final ObjectPoolTracker poolTracker;

    private final ObjectPoolServiceListener poolListener;

    private final Map<ObjectPool, PoolHandler> poolMap = new HashMap<ObjectPool, PoolHandler> ();

    private final ObjectPoolListener serviceListener;

    protected class PoolHandler implements ObjectPoolListener
    {
        private final ObjectPool pool;

        private final Map<Object, Dictionary<?, ?>> services = new HashMap<Object, Dictionary<?, ?>> ();

        public PoolHandler ( final ObjectPool pool )
        {
            this.pool = pool;

            synchronized ( this )
            {
                this.pool.addListener ( this );
            }
        }

        public synchronized void dispose ()
        {
            this.pool.removeListener ( this );

            for ( final Map.Entry<Object, Dictionary<?, ?>> entry : this.services.entrySet () )
            {
                fireServiceRemoved ( entry.getKey (), entry.getValue () );
            }
            this.services.clear ();
        }

        public synchronized void serviceAdded ( final Object service, final Dictionary<?, ?> properties )
        {
            this.services.put ( service, properties );
            fireServiceAdded ( service, properties );
        }

        private void fireServiceAdded ( final Object service, final Dictionary<?, ?> properties )
        {
            AllObjectPoolServiceTracker.this.handleServiceAdded ( service, properties );
        }

        public synchronized void serviceModified ( final Object service, final Dictionary<?, ?> properties )
        {
            this.services.put ( service, properties );
            fireServiceModified ( service, properties );
        }

        private void fireServiceModified ( final Object service, final Dictionary<?, ?> properties )
        {
            AllObjectPoolServiceTracker.this.handleServiceModified ( service, properties );
        }

        public synchronized void serviceRemoved ( final Object service, final Dictionary<?, ?> properties )
        {
            final Dictionary<?, ?> oldProperties = this.services.remove ( service );
            if ( oldProperties != null )
            {
                fireServiceRemoved ( service, properties );
            }
        }

        private void fireServiceRemoved ( final Object service, final Dictionary<?, ?> properties )
        {
            AllObjectPoolServiceTracker.this.handleServiceRemoved ( service, properties );
        }
    }

    public AllObjectPoolServiceTracker ( final ObjectPoolTracker poolTracker, final ObjectPoolListener listener )
    {
        this.serviceListener = listener;
        this.poolTracker = poolTracker;

        this.poolListener = new ObjectPoolServiceListener () {

            public void poolRemoved ( final ObjectPool objectPool )
            {
                AllObjectPoolServiceTracker.this.handlePoolRemove ( objectPool );
            }

            public void poolModified ( final ObjectPool objectPool, final int newPriority )
            {
                AllObjectPoolServiceTracker.this.handlePoolModified ( objectPool, newPriority );
            }

            public void poolAdded ( final ObjectPool objectPool, final int priority )
            {
                AllObjectPoolServiceTracker.this.handlePoolAdd ( objectPool, priority );
            }
        };
    }

    protected synchronized void handleServiceAdded ( final Object service, final Dictionary<?, ?> properties )
    {
        logger.debug ( "Service added {} -> {}", new Object[] { service, properties } );
        this.serviceListener.serviceAdded ( service, properties );
    }

    protected synchronized void handleServiceModified ( final Object service, final Dictionary<?, ?> properties )
    {
        this.serviceListener.serviceModified ( service, properties );
    }

    protected synchronized void handleServiceRemoved ( final Object service, final Dictionary<?, ?> properties )
    {
        this.serviceListener.serviceRemoved ( service, properties );
    }

    protected synchronized void handlePoolAdd ( final ObjectPool objectPool, final int priority )
    {
        this.poolMap.put ( objectPool, new PoolHandler ( objectPool ) );
    }

    protected synchronized void handlePoolModified ( final ObjectPool objectPool, final int newPriority )
    {
        // we don't care
    }

    protected synchronized void handlePoolRemove ( final ObjectPool objectPool )
    {
        final PoolHandler handler = this.poolMap.get ( objectPool );
        if ( handler != null )
        {
            handler.dispose ();
        }
    }

    /**
     * Start the tracker
     */
    public synchronized void open ()
    {
        this.poolTracker.addListener ( this.poolListener );
    }

    /**
     * Stop the tracker
     */
    public synchronized void close ()
    {
        this.poolTracker.removeListener ( this.poolListener );
    }
}
