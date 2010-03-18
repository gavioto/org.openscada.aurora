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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.osgi.pool.internal.UnmodifyableDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ObjectPoolImpl implements ObjectPool
{

    private final static Logger logger = LoggerFactory.getLogger ( ObjectPoolImpl.class );

    private final Multimap<String, ObjectPoolListener> idListeners = HashMultimap.create ();

    private final Set<ObjectPoolListener> anyListener = new HashSet<ObjectPoolListener> ();

    private final Map<String, Map<Object, Dictionary<?, ?>>> services = new HashMap<String, Map<Object, Dictionary<?, ?>>> ();

    private final ExecutorService executor;

    private static final Dictionary<?, ?> emptyHashtable = new UnmodifyableDictionary<Object, Object> ( new Hashtable<Object, Object> () );

    private boolean disposed;

    public ObjectPoolImpl ()
    {
        this.executor = Executors.newSingleThreadExecutor ( new NamedThreadFactory ( toString () ) );
    }

    @SuppressWarnings ( "unchecked" )
    public synchronized void addService ( final String id, final Object service, Dictionary<?, ?> properties )
    {
        if ( this.disposed )
        {
            return;
        }

        if ( properties == null || properties.isEmpty () )
        {
            properties = emptyHashtable;
        }
        else
        {
            properties = new UnmodifyableDictionary<Object, Object> ( (Dictionary<Object, Object>)properties );
        }

        logger.debug ( "Adding service: {} -> {} -> {}", new Object[] { id, service, properties } );

        Map<Object, Dictionary<?, ?>> serviceMap = this.services.get ( id );
        if ( serviceMap == null )
        {
            serviceMap = new HashMap<Object, Dictionary<?, ?>> ( 1 );
            this.services.put ( id, serviceMap );
        }

        final Dictionary<?, ?> oldService = serviceMap.put ( service, properties );
        if ( oldService != null )
        {
            logger.warn ( "Replaced service: {}", new Object[] { id } );
        }

        fireAddedService ( id, service, properties );
    }

    public synchronized void modifyService ( final String id, final Object service, final Dictionary<?, ?> properties )
    {
        if ( this.disposed )
        {
            return;
        }

        logger.debug ( "Modifing service: {} -> {} -> {}", new Object[] { id, service, properties } );

        final Map<Object, Dictionary<?, ?>> serviceMap = this.services.get ( id );
        if ( serviceMap != null )
        {
            serviceMap.put ( service, properties );
            fireModifiedService ( id, service, properties );
        }
    }

    public synchronized void removeService ( final String id, final Object service )
    {
        if ( this.disposed )
        {
            return;
        }

        logger.debug ( "Removing service: {} -> {}", new Object[] { id, service } );

        final Map<Object, Dictionary<?, ?>> serviceMap = this.services.get ( id );
        if ( serviceMap == null )
        {
            return;
        }
        final Dictionary<?, ?> properties = serviceMap.remove ( service );
        if ( properties != null )
        {
            if ( serviceMap.isEmpty () )
            {
                this.services.remove ( id );
            }
            fireRemoveService ( id, service, properties );
        }
    }

    private void fireAddedService ( final String id, final Object service, final Dictionary<?, ?> properties )
    {
        final Collection<ObjectPoolListener> listeners = cloneListeners ( id );

        logger.debug ( "Fire add service: {} ({} listeners)", new Object[] { id, listeners.size () } );

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final ObjectPoolListener listener : listeners )
                {
                    listener.serviceAdded ( service, properties );
                }
            }
        } );
    }

    protected Collection<ObjectPoolListener> cloneListeners ( final String id )
    {
        final List<ObjectPoolListener> listeners = new ArrayList<ObjectPoolListener> ();
        listeners.addAll ( this.idListeners.get ( id ) );
        listeners.addAll ( this.anyListener );
        return listeners;
    }

    private void fireModifiedService ( final String id, final Object service, final Dictionary<?, ?> properties )
    {
        final Collection<ObjectPoolListener> listeners = cloneListeners ( id );

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final ObjectPoolListener listener : listeners )
                {
                    listener.serviceModified ( service, properties );
                }
            }
        } );
    }

    private void fireRemoveService ( final String id, final Object service, final Dictionary<?, ?> properties )
    {
        final Collection<ObjectPoolListener> listeners = cloneListeners ( id );

        this.executor.execute ( new Runnable () {

            public void run ()
            {
                for ( final ObjectPoolListener listener : listeners )
                {
                    listener.serviceRemoved ( service, properties );
                }
            }
        } );
    }

    public void dispose ()
    {
        synchronized ( this )
        {

            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    for ( final ObjectPoolListener listener : ObjectPoolImpl.this.anyListener )
                    {
                        for ( final Map<Object, Dictionary<?, ?>> map : ObjectPoolImpl.this.services.values () )
                        {
                            for ( final Map.Entry<Object, Dictionary<?, ?>> serviceEntry : map.entrySet () )
                            {
                                listener.serviceRemoved ( serviceEntry.getKey (), serviceEntry.getValue () );
                            }
                        }
                    }
                    for ( final Map.Entry<String, ObjectPoolListener> entry : ObjectPoolImpl.this.idListeners.entries () )
                    {
                        final Map<Object, Dictionary<?, ?>> serviceMap = ObjectPoolImpl.this.services.get ( entry.getKey () );
                        for ( final Map.Entry<Object, Dictionary<?, ?>> serviceEntry : serviceMap.entrySet () )
                        {
                            entry.getValue ().serviceRemoved ( serviceEntry.getKey (), serviceEntry.getValue () );
                        }
                    }

                    ObjectPoolImpl.this.anyListener.clear ();
                    ObjectPoolImpl.this.idListeners.clear ();
                    ObjectPoolImpl.this.services.clear ();

                }
            } );

            this.disposed = true;
        }

        // wait for termination outside of sync
        this.executor.shutdown ();
    }

    /* (non-Javadoc)
     * @see org.openscada.da.datasource.ObjectPool#addListener(java.lang.String, org.openscada.da.datasource.ObjectPoolListener)
     */
    public synchronized void addListener ( final String id, final ObjectPoolListener listener )
    {
        if ( this.disposed )
        {
            return;
        }

        logger.debug ( "Adding listener for {}", new Object[] { id } );

        if ( this.idListeners.put ( id, listener ) )
        {
            logger.debug ( "Added listener {} for {}", new Object[] { listener, id } );

            final Map<Object, Dictionary<?, ?>> serviceMap = this.services.get ( id );
            if ( serviceMap != null )
            {
                final Map<Object, Dictionary<?, ?>> serviceMapClone = new HashMap<Object, Dictionary<?, ?>> ( serviceMap );
                this.executor.execute ( new Runnable () {

                    public void run ()
                    {
                        for ( final Map.Entry<Object, Dictionary<?, ?>> entry : serviceMapClone.entrySet () )
                        {
                            listener.serviceAdded ( entry.getKey (), entry.getValue () );
                        }

                    }
                } );
            }
        }
    }

    /* (non-Javadoc)
     * @see org.openscada.da.datasource.ObjectPool#removeListener(java.lang.String, org.openscada.da.datasource.ObjectPoolListener)
     */
    public synchronized void removeListener ( final String id, final ObjectPoolListener listener )
    {
        if ( this.disposed )
        {
            return;
        }

        this.idListeners.remove ( id, listener );
    }

    public synchronized void addListener ( final ObjectPoolListener listener )
    {
        if ( this.disposed )
        {
            return;
        }

        logger.debug ( "Adding listener {}", listener );

        if ( this.anyListener.add ( listener ) )
        {
            final Collection<Map<Object, Dictionary<?, ?>>> servicesClone = new ArrayList<Map<Object, Dictionary<?, ?>>> ( this.services.values () );

            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    for ( final Map<Object, Dictionary<?, ?>> serviceMap : servicesClone )
                    {
                        for ( final Map.Entry<Object, Dictionary<?, ?>> entry : serviceMap.entrySet () )
                        {
                            listener.serviceAdded ( entry.getKey (), entry.getValue () );
                        }
                    }

                }
            } );
        }
    }

    public synchronized void removeListener ( final ObjectPoolListener listener )
    {
        if ( this.disposed )
        {
            return;
        }

        this.anyListener.remove ( listener );
    }
}
