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

package org.openscada.hsdb.datatypes;

/**
 * Base class for all values that can be handled via the storage channel api.
 * Two instances of this class are equal if they have the identical timestamp.
 * @author Ludwig Straub
 */
public abstract class BaseValue implements Comparable<BaseValue>
{
    /** Time stamp of the data. */
    private long time;

    /** Quality information of the data. The value lies within the interval 0..100. */
    private double qualityIndicator;

    /** Percentage of manually set values during the value time. */
    private double manualIndicator;

    /** Count of values that have been combined to get the current value. */
    private long baseValueCount;

    /**
     * Fully initializing constructor.
     * @param time time stamp of the data
     * @param qualityIndicator quality information of the data
     * @param manualIndicator percentage of manually set values during the value time
     * @param baseValueCount count of values that have been combined to get the current value
     */
    public BaseValue ( final long time, final double qualityIndicator, final double manualIndicator, final long baseValueCount )
    {
        this.time = time;
        this.qualityIndicator = qualityIndicator;
        this.manualIndicator = manualIndicator;
        this.baseValueCount = baseValueCount;
    }

    /**
     * This method returns the time stamp of the data.
     * @return time stamp of the data
     */
    public long getTime ()
    {
        return this.time;
    }

    /**
     * This method sets the time stamp of the data.
     * @param time time stamp of the data
     */
    public void setTime ( final long time )
    {
        this.time = time;
    }

    /**
     * This method returns the quality information of the data.
     * @return quality information of the data
     */
    public double getQualityIndicator ()
    {
        return this.qualityIndicator;
    }

    /**
     * This method sets the quality information of the data.
     * @param qualityIndicator quality information of the data
     */
    public void setQualityIndicator ( final double qualityIndicator )
    {
        this.qualityIndicator = qualityIndicator;
    }

    /**
     * This method returns the percentage of manually set values during the value time.
     * @return percentage of manually set values during the value time
     */
    public double getManualIndicator ()
    {
        return this.manualIndicator;
    }

    /**
     * This method sets the percentage of manually set values during the value time.
     * @param manualIndicator percentage of manually set values during the value time
     */
    public void setManualIndicator ( final double manualIndicator )
    {
        this.manualIndicator = manualIndicator;
    }

    /**
     * This method returns the count of values that have been combined to get the current value.
     * @return count of values that have been combined to get the current value
     */
    public long getBaseValueCount ()
    {
        return this.baseValueCount;
    }

    /**
     * This method sets the count of values that have been combined to get the current value.
     * @param baseValueCount count of values that have been combined to get the current value
     */
    public void setBaseValueCount ( final long baseValueCount )
    {
        this.baseValueCount = baseValueCount;
    }

    /**
     * @see java.lang.Object#hashCode
     */
    public int hashCode ()
    {
        return (int) ( this.time ^ this.time >>> 32 );
    }

    /**
     * @see java.lang.Object#equals
     */
    public boolean equals ( final Object obj )
    {
        if ( obj instanceof BaseValue )
        {
            final BaseValue baseValue = (BaseValue)obj;
            return this.time == baseValue.getTime () && this.qualityIndicator == baseValue.getQualityIndicator () && this.baseValueCount == baseValue.getBaseValueCount ();
        }
        return false;
    }

    /**
     * @see java.lang.Comparable#compareTo
     */
    public int compareTo ( final BaseValue o2 )
    {
        if ( o2 == null )
        {
            return 1;
        }
        final long t2 = o2.getTime ();
        if ( this.time < t2 )
        {
            return -1;
        }
        if ( this.time > t2 )
        {
            return 1;
        }
        return 0;
    }
}
