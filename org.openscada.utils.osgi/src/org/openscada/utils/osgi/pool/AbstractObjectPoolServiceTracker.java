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

package org.openscada.utils.osgi.pool;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.openscada.utils.osgi.pool.ObjectPoolTracker.ObjectPoolServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractObjectPoolServiceTracker<S>
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractObjectPoolServiceTracker.class );

    private final ObjectPoolTracker<S> poolTracker;

    private final ObjectPoolServiceListener<S> poolListener;

    protected final String serviceId;

    private final Map<ObjectPool<S>, PoolHandler> poolMap = new HashMap<ObjectPool<S>, PoolHandler> ( 1 );

    protected class PoolHandler implements ObjectPoolListener<S>
    {
        private final ObjectPool<S> pool;

        private final String serviceId;

        private final Map<S, Dictionary<?, ?>> services = new HashMap<S, Dictionary<?, ?>> ( 1 );

        public PoolHandler ( final ObjectPool<S> pool, final String serviceId )
        {
            this.pool = pool;
            this.serviceId = serviceId;

            synchronized ( this )
            {
                this.pool.addListener ( this.serviceId, this );
            }
        }

        public synchronized void dispose ()
        {
            this.pool.removeListener ( this.serviceId, this );

            for ( final Map.Entry<S, Dictionary<?, ?>> entry : this.services.entrySet () )
            {
                fireServiceRemoved ( entry.getKey (), entry.getValue () );
            }
            this.services.clear ();
        }

        @Override
        public synchronized void serviceAdded ( final S service, final Dictionary<?, ?> properties )
        {
            this.services.put ( service, properties );
            fireServiceAdded ( service, properties );
        }

        private void fireServiceAdded ( final S service, final Dictionary<?, ?> properties )
        {
            logger.debug ( "Service added to pool: {} -> {}", new Object[] { this.serviceId, service } );
            handleServiceAdded ( service, properties );
        }

        @Override
        public synchronized void serviceModified ( final S service, final Dictionary<?, ?> properties )
        {
            this.services.put ( service, properties );
            fireServiceModified ( service, properties );
        }

        private void fireServiceModified ( final S service, final Dictionary<?, ?> properties )
        {
            handleServiceModified ( service, properties );
        }

        @Override
        public synchronized void serviceRemoved ( final S service, final Dictionary<?, ?> properties )
        {
            final Dictionary<?, ?> oldProperties = this.services.remove ( service );
            if ( oldProperties != null )
            {
                fireServiceRemoved ( service, properties );
            }
        }

        private void fireServiceRemoved ( final S service, final Dictionary<?, ?> properties )
        {
            handleServiceRemoved ( service, properties );
        }
    }

    public AbstractObjectPoolServiceTracker ( final ObjectPoolTracker<S> poolTracker, final String serviceId )
    {
        this.serviceId = serviceId;
        this.poolTracker = poolTracker;

        this.poolListener = new ObjectPoolServiceListener<S> () {

            @Override
            public void poolRemoved ( final ObjectPool<S> objectPool )
            {
                AbstractObjectPoolServiceTracker.this.handlePoolRemove ( objectPool );
            }

            @Override
            public void poolModified ( final ObjectPool<S> objectPool, final int newPriority )
            {
                AbstractObjectPoolServiceTracker.this.handlePoolModified ( objectPool, newPriority );
            }

            @Override
            public void poolAdded ( final ObjectPool<S> objectPool, final int priority )
            {
                AbstractObjectPoolServiceTracker.this.handlePoolAdd ( objectPool, priority );
            }
        };
    }

    protected abstract void handleServiceAdded ( final S service, final Dictionary<?, ?> properties );

    protected abstract void handleServiceModified ( final S service, final Dictionary<?, ?> properties );

    protected abstract void handleServiceRemoved ( final S service, final Dictionary<?, ?> properties );

    protected synchronized void handlePoolAdd ( final ObjectPool<S> objectPool, final int priority )
    {
        logger.debug ( "Pool added: {}/{}", objectPool, priority );
        this.poolMap.put ( objectPool, new PoolHandler ( objectPool, this.serviceId ) );
    }

    protected synchronized void handlePoolModified ( final ObjectPool<S> objectPool, final int newPriority )
    {
        // we don't care
    }

    protected synchronized void handlePoolRemove ( final ObjectPool<S> objectPool )
    {
        logger.debug ( "Pool removed: {}", objectPool );

        final PoolHandler handler = this.poolMap.get ( objectPool );
        if ( handler != null )
        {
            handler.dispose ();
        }
    }

    public void open ()
    {
        this.poolTracker.addListener ( this.poolListener );
    }

    public void close ()
    {
        this.poolTracker.removeListener ( this.poolListener );
    }
}
