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

import org.openscada.hsdb.datatypes.LongValue;

/**
 * This interface provides methods for storing and retrieving values of type long.
 * @author Ludwig Straub
 */
public interface StorageChannel
{
    /** Empty array of long values that is used when transforming a list to an array. */
    public final static LongValue[] EMPTY_LONGVALUE_ARRAY = new LongValue[0];

    /**
     * This method returns the metadata that is currently valid by the storage channel backend.
     * Note: The time related data that is returned via this method has to be treated as snapshot.
     * To be reliable concerning that data, the storage channel object has to be synchronized.
     * The data then is reliable as long as the synchronization consists.
     * @return storageChannelMetaData metadata that is currently valid by the storage channel backend, never null
     * @throws Exception if no meta data object can be retrieved
     */
    public abstract StorageChannelMetaData getMetaData () throws Exception;

    /**
     * This method updates the passed long value.
     * If a value with the same time stamp already exists, the previous value will be replaced.
     * The implementation decides whether the data is processed or not.
     * See the documentation of the different implementations for more details.
     * @param longValue value that has to be updated
     * @throws Exception in case of read/write problems or file corruption
     */
    public abstract void updateLong ( LongValue longValue ) throws Exception;

    /**
     * This method updates the passed long values.
     * If a value with the same time stamp already exists, the previous value will be replaced.
     * The implementation decides whether the data is processed or not.
     * See the documentation of the different implementations for more details.
     * @param longValues values that have to be updated
     * @throws Exception in case of read/write problems or file corruption
     */
    public abstract void updateLongs ( final LongValue[] longValues ) throws Exception;

    /**
     * This method retrieves all long values that match the specified time span and returns them as array sorted by time.
     * If the lower bound cannot be satisfied by an exact value, the previous value that lies outside the time span will also be returned.
     * @param startTime start of the time span for which the values have to be retrieved
     * @param endTime end of the time span for which the values have to be retrieved
     * @return long values that match the specified time span
     * @throws Exception in case of read/write problems or file corruption
     */
    public abstract LongValue[] getLongValues ( long startTime, long endTime ) throws Exception;

    /**
     * This method deletes old data.
     * This method can only be called after the initialize method.
     * @throws Exception in case of any problem
     */
    public abstract void cleanupRelicts () throws Exception;
}
