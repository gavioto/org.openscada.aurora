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

package org.openscada.ds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class DataNodeTracker
{
    private final SingleServiceTracker tracker;

    private DataStore service;

    private final Map<String, Set<DataListener>> listeners = new HashMap<String, Set<DataListener>> ();

    public DataNodeTracker ( final BundleContext context )
    {
        this.tracker = new SingleServiceTracker ( context, DataStore.class.getName (), new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                handleService ( (DataStore)service );
            }
        } );
    }

    protected synchronized void handleService ( final DataStore service )
    {
        if ( this.service != null )
        {
            // Detach from all
            for ( final Map.Entry<String, Set<DataListener>> listeners : this.listeners.entrySet () )
            {
                for ( final DataListener listener : listeners.getValue () )
                {
                    this.service.detachListener ( listeners.getKey (), listener );
                }
            }
        }

        this.service = service;

        if ( this.service != null )
        {
            // attach to all
            for ( final Map.Entry<String, Set<DataListener>> listeners : this.listeners.entrySet () )
            {
                for ( final DataListener listener : listeners.getValue () )
                {
                    this.service.attachListener ( listeners.getKey (), listener );
                }
            }
        }
    }

    public void open ()
    {
        this.tracker.open ();
    }

    public void close ()
    {
        this.tracker.close ();
    }

    /**
     * Add a listener for a data node
     * @param nodeId the node to listen to
     * @param listener the listener to add
     */
    public synchronized void addListener ( final String nodeId, final DataListener listener )
    {
        Set<DataListener> set = this.listeners.get ( nodeId );
        if ( set == null )
        {
            set = new HashSet<DataListener> ( 1 );
            this.listeners.put ( nodeId, set );
        }

        if ( set.add ( listener ) && this.service != null )
        {
            this.service.attachListener ( nodeId, listener );
        }
    }

    /**
     * Remove a listener for a data node
     * @param nodeId the node to remove the listener from
     * @param listener the listener to remove
     */
    public synchronized void removeListener ( final String nodeId, final DataListener listener )
    {
        final Set<DataListener> set = this.listeners.get ( nodeId );
        if ( set == null )
        {
            return;
        }

        if ( set.remove ( listener ) && this.service != null )
        {
            this.service.detachListener ( nodeId, listener );
        }

        if ( set.isEmpty () )
        {
            this.listeners.remove ( nodeId );
        }
    }

    /**
     * Write to the data node if there currently is a service attached
     * @param node the node to write
     * @return <code>true</code> if the service was attached an the request was
     * passed on to the service, <code>false</code> otherwise.
     */
    public boolean write ( final DataNode node )
    {
        final DataStore store = this.service;
        if ( store != null )
        {
            store.writeNode ( node );
            return true;
        }
        else
        {
            return false;
        }
    }
}
