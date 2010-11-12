/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hsdb;

/**
 * This interface extends the StorageChannel interface with methods for managing StorageChannel objects.
 * This can be useful when complex storage channel sturctures have to be created and handled.
 * @author Ludwig Straub
 */
public interface StorageChannelManager extends ExtendedStorageChannel
{
    /**
     * This method adds a sub storage channel to the current channel.
     * All data the current channel receives will also be propagated to the sub channel
     * @param storageChannel storage channel that has to be added to the current channel
     */
    public abstract void registerStorageChannel ( ExtendedStorageChannel storageChannel );

    /**
     * This method removes a sub storage channel from the current channel.
     * All data the current channel receives will also be propagated to the sub channel
     * @param storageChannel storage channel that has to be added to the current channel
     */
    public abstract void unregisterStorageChannel ( ExtendedStorageChannel storageChannel );
}
