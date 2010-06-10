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

package org.openscada.ds.storage;

import java.util.concurrent.Executor;

import org.openscada.ds.DataListener;
import org.openscada.ds.DataNode;
import org.openscada.ds.DataStore;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public abstract class AbstractStorage implements DataStore
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractStorage.class );

    protected final Executor executor;

    private final Multimap<String, DataListener> listeners = HashMultimap.create ();

    public AbstractStorage ( final Executor executor )
    {
        this.executor = executor;
    }

    public abstract NotifyFuture<DataNode> readNode ( final String nodeId );

    public synchronized void dispose ()
    {
        for ( final DataListener listener : this.listeners.values () )
        {
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    listener.nodeChanged ( null );
                }
            } );
        }
        this.listeners.clear ();
    }

    public synchronized void attachListener ( final String nodeId, final DataListener listener )
    {
        if ( this.listeners.put ( nodeId, listener ) )
        {
            final NotifyFuture<DataNode> task = readNode ( nodeId );

            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    try
                    {
                        listener.nodeChanged ( task.get () );
                    }
                    catch ( final Exception e )
                    {
                        listener.nodeChanged ( null );
                        logger.info ( "Failed to initially load data node", e );
                    }
                }
            } );

        }
    }

    public synchronized void detachListener ( final String nodeId, final DataListener listener )
    {
        this.listeners.remove ( nodeId, listener );
    }

    protected synchronized void fireUpdate ( final DataNode node )
    {
        for ( final DataListener listener : this.listeners.get ( node.getId () ) )
        {
            this.executor.execute ( new Runnable () {

                public void run ()
                {
                    listener.nodeChanged ( node );
                }
            } );
        }
    }

}