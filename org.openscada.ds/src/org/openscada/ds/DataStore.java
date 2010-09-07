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
