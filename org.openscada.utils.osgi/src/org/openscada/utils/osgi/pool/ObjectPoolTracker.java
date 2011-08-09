/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.utils.osgi.FilterUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A tracker which tracks object pools
 * @author Jens Reimann
 *
 */
public class ObjectPoolTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( ObjectPoolTracker.class );

    private final int DEFAULT_PRIORITY = Integer.getInteger ( "org.openscada.osgi.objectPool.defaultPriority", 0 );

    private final ServiceTracker<ObjectPool, ObjectPool> poolTracker;

    private final Set<ObjectPoolServiceListener> listeners = new HashSet<ObjectPoolServiceListener> ();

    public interface ObjectPoolServiceListener
    {
        public void poolAdded ( ObjectPool objectPool, int priority );

        public void poolRemoved ( ObjectPool objectPool );

        public void poolModified ( ObjectPool objectPool, int newPriority );
    }

    private final Map<ObjectPool, Integer> poolMap = new HashMap<ObjectPool, Integer> ();

    public ObjectPoolTracker ( final BundleContext context, final String poolClass ) throws InvalidSyntaxException
    {
        final Map<String, String> parameters = new HashMap<String, String> ();
        parameters.put ( ObjectPool.OBJECT_POOL_CLASS, poolClass );
        final Filter filter = FilterUtil.createAndFilter ( ObjectPool.class.getName (), parameters );

        this.poolTracker = new ServiceTracker<ObjectPool, ObjectPool> ( context, filter, new ServiceTrackerCustomizer<ObjectPool, ObjectPool> () {

            @Override
            public void removedService ( final ServiceReference<ObjectPool> reference, final ObjectPool service )
            {
                context.ungetService ( reference );
                ObjectPoolTracker.this.removePool ( service );
            }

            @Override
            public void modifiedService ( final ServiceReference<ObjectPool> reference, final ObjectPool service )
            {
                ObjectPoolTracker.this.modifyPool ( service, reference );
            }

            @Override
            public ObjectPool addingService ( final ServiceReference<ObjectPool> reference )
            {
                final ObjectPool o = context.getService ( reference );
                ObjectPoolTracker.this.addPool ( o, reference );
                return o;
            }
        } );
    }

    protected int getPriority ( final ServiceReference<ObjectPool> reference )
    {
        return getPriority ( reference, this.DEFAULT_PRIORITY );
    }

    protected int getPriority ( final ServiceReference<ObjectPool> reference, final int defaultPriority )
    {
        final Object o = reference.getProperty ( Constants.SERVICE_RANKING );
        if ( o instanceof Number )
        {
            return ( (Number)o ).intValue ();
        }
        else
        {
            return defaultPriority;
        }
    }

    protected synchronized void addPool ( final ObjectPool objectPool, final ServiceReference<ObjectPool> reference )
    {
        logger.debug ( "Found new pool: {} -> {}", new Object[] { objectPool, reference } );

        final int priority = getPriority ( reference );
        this.poolMap.put ( objectPool, priority );
        fireAdded ( objectPool, priority );
    }

    private void fireAdded ( final ObjectPool objectPool, final int priority )
    {
        for ( final ObjectPoolServiceListener listener : this.listeners )
        {
            listener.poolAdded ( objectPool, priority );
        }
    }

    protected synchronized void modifyPool ( final ObjectPool objectPool, final ServiceReference<ObjectPool> reference )
    {
        logger.debug ( "Pool modified: {} -> {}", new Object[] { objectPool, reference } );

        final int newPriority = getPriority ( reference );
        this.poolMap.put ( objectPool, newPriority );
        fireModified ( objectPool, newPriority );
    }

    private void fireModified ( final ObjectPool objectPool, final int newPriority )
    {
        for ( final ObjectPoolServiceListener listener : this.listeners )
        {
            listener.poolModified ( objectPool, newPriority );
        }
    }

    protected synchronized void removePool ( final ObjectPool objectPool )
    {
        logger.debug ( "Pool removed: {}", new Object[] { objectPool } );
        final Integer priority = this.poolMap.remove ( objectPool );
        if ( priority != null )
        {
            fireRemoved ( objectPool );
        }
    }

    private void fireRemoved ( final ObjectPool objectPool )
    {
        for ( final ObjectPoolServiceListener listener : this.listeners )
        {
            listener.poolRemoved ( objectPool );
        }
    }

    public synchronized void open ()
    {
        this.poolTracker.open ();
    }

    public synchronized void close ()
    {
        this.poolTracker.close ();
    }

    public synchronized void addListener ( final ObjectPoolServiceListener listener )
    {
        logger.debug ( "Adding pool service listener: {}", listener );
        if ( this.listeners.add ( listener ) )
        {
            for ( final Map.Entry<ObjectPool, Integer> entry : this.poolMap.entrySet () )
            {
                logger.debug ( "Add Announce pool: {}/{}", new Object[] { entry.getKey (), entry.getValue () } );
                listener.poolAdded ( entry.getKey (), entry.getValue () );
            }
        }
    }

    public void removeListener ( final ObjectPoolServiceListener listener )
    {
        final Set<ObjectPool> pools;

        synchronized ( this )
        {
            if ( !this.listeners.remove ( listener ) )
            {
                return;
            }
            pools = new HashSet<ObjectPool> ( this.poolMap.keySet () );
        }

        for ( final ObjectPool pool : pools )
        {
            listener.poolRemoved ( pool );
        }
    }
}
