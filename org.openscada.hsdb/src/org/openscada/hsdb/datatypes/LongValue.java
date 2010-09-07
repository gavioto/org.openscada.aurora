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

package org.openscada.hsdb.datatypes;

/**
 * This class handles a long value for being storaged in a storage channel.
 * @author Ludwig Straub
 */
public class LongValue extends BaseValue
{
    /** Value to be handled. */
    private long value;

    /**
     * Fully initializing constructor.
     * @param time time stamp of the data
     * @param qualityIndicator quality information of the data
     * @param manualIndicator percentage of manually set values during the value time
     * @param baseValueCount count of values that have been combined to get the current value
     * @param value value to be handled
     */
    public LongValue ( final long time, final double qualityIndicator, final double manualIndicator, final long baseValueCount, final long value )
    {
        super ( time, qualityIndicator, manualIndicator, baseValueCount );
        this.value = value;
    }

    /**
     * This method returns the value to be handled.
     * @return value to be handled
     */
    public long getValue ()
    {
        return this.value;
    }

    /**
     * This method sets the value to be handled.
     * @param value value to be handled
     */
    public void setValue ( final long value )
    {
        this.value = value;
    }

    /**
     * @see java.lang.Object#equals
     */
    public boolean equals ( final Object baseValue )
    {
        return baseValue instanceof LongValue && super.equals ( baseValue ) && this.value == ( (LongValue)baseValue ).getValue ();
    }
}
