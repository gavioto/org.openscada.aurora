/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2009-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.ds;

import org.openscada.utils.concurrent.NotifyFuture;

/**
 * A data store service interface
 * @author Jens Reimann
 * @since 0.15.0
 *
 */
public interface DataStore
{
    /**
     * Attach a listener to a data node
     * 
     * <p>
     * The listener is called with the current status of the node when
     * attaching.
     * </p>
     * 
     * @param nodeId the node id to attach to
     * @param listener the listener that will receive events
     *  
     */
    public void attachListener ( String nodeId, DataListener listener );

    public void detachListener ( String nodeId, DataListener listener );

    public NotifyFuture<Void> deleteNode ( String nodeId );

    public NotifyFuture<Void> writeNode ( DataNode node );

    /**
     * Get the data node
     * @param nodeId the id of the node to get
     * @return the future to the read request. The future will
     * return <code>null</code> if the node does not exists.
     */
    public NotifyFuture<DataNode> readNode ( String nodeId );
}
