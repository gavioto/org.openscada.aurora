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

package org.openscada.hsdb;

import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter class for storage channel implementations class that provides the support of additional datatypes.
 * No checks are performed whether the stored data is compatible with the specified datatype.
 * For instance if data is stored as double, the data has to be retrieved as double in the future.
 * Each storage channel should therefore be fixed to exactly one datatype it supports.
 * That datatype setting should not be changed during the whole storage channel's lifespan.
 * @author Ludwig Straub
 */
public class ExtendedStorageChannelAdapter implements ExtendedStorageChannel
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( ExtendedStorageChannelAdapter.class );

    /** Storage channel that will be used to store and retrieve data. */
    private StorageChannel storageChannel;

    /**
     * Constructor.
     * @param storageChannel storage channel that will be used to store and retrieve data
     */
    public ExtendedStorageChannelAdapter ( final StorageChannel storageChannel )
    {
        this.storageChannel = storageChannel;
    }

    /**
     * This method returns the storage channel that will be used to store and retrieve data.
     * @return storage channel that will be used to store and retrieve data
     */
    public synchronized StorageChannel getStorageChannel ()
    {
        return this.storageChannel;
    }

    /**
     * This method sets the storage channel that will be used to store and retrieve data.
     * @param storageChannel storage channel that will be used to store and retrieve data
     */
    public synchronized void setStorageChannel ( final StorageChannel storageChannel )
    {
        this.storageChannel = storageChannel;
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getMetaData
     */
    public StorageChannelMetaData getMetaData () throws Exception
    {
        if ( this.storageChannel == null )
        {
            final String message = "no storage channel available for extended storage channel adapter! unable to retrieve meta data";
            logger.error ( message );
            throw new Exception ( message );
        }
        return this.storageChannel.getMetaData ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDouble
     */
    public synchronized void updateDouble ( final DoubleValue doubleValue ) throws Exception
    {
        if ( this.storageChannel != null && doubleValue != null )
        {
            this.storageChannel.updateLong ( new LongValue ( doubleValue.getTime (), doubleValue.getQualityIndicator (), doubleValue.getManualIndicator (), doubleValue.getBaseValueCount (), Double.doubleToLongBits ( doubleValue.getValue () ) ) );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDoubles
     */
    public synchronized void updateDoubles ( final DoubleValue[] doubleValues ) throws Exception
    {
        if ( this.storageChannel != null && doubleValues != null )
        {
            final LongValue[] longValues = new LongValue[doubleValues.length];
            for ( int i = 0; i < doubleValues.length; i++ )
            {
                final DoubleValue doubleValue = doubleValues[i];
                longValues[i] = new LongValue ( doubleValue.getTime (), doubleValue.getQualityIndicator (), doubleValue.getManualIndicator (), doubleValue.getBaseValueCount (), Double.doubleToLongBits ( doubleValue.getValue () ) );
            }
            this.storageChannel.updateLongs ( longValues );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getDoubleValues
     */
    public synchronized DoubleValue[] getDoubleValues ( final long startTime, final long endTime ) throws Exception
    {
        final LongValue[] longValues = this.storageChannel != null ? this.storageChannel.getLongValues ( startTime, endTime ) : EMPTY_LONGVALUE_ARRAY;
        final DoubleValue[] doubleValues = new DoubleValue[longValues.length];
        for ( int i = 0; i < longValues.length; i++ )
        {
            final LongValue longValue = longValues[i];
            doubleValues[i] = new DoubleValue ( longValue.getTime (), longValue.getQualityIndicator (), longValue.getManualIndicator (), longValue.getBaseValueCount (), Double.longBitsToDouble ( longValue.getValue () ) );
        }
        return doubleValues;
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        return this.storageChannel != null ? this.storageChannel.getLongValues ( startTime, endTime ) : EMPTY_LONGVALUE_ARRAY;
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#updateLong
     */
    public synchronized void updateLong ( final LongValue longValue ) throws Exception
    {
        if ( this.storageChannel != null )
        {
            this.storageChannel.updateLong ( longValue );
        }
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#updateLongs
     */
    public synchronized void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        if ( this.storageChannel != null )
        {
            this.storageChannel.updateLongs ( longValues );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#cleanupRelicts
     */
    public synchronized void cleanupRelicts () throws Exception
    {
        if ( this.storageChannel != null )
        {
            this.storageChannel.cleanupRelicts ();
        }
    }
}
