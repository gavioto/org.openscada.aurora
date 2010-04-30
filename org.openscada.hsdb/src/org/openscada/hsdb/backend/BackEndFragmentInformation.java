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

package org.openscada.hsdb.backend;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.hsdb.calculation.CalculationMethod;

/**
 * This class provides data for back end fragment objects.
 * @author Ludwig Straub
 */
public class BackEndFragmentInformation implements Comparable<BackEndFragmentInformation>
{
    /** Id of the configuration of the back end fragment. */
    private String configurationId;

    /** Id of the detail level of the back end fragment. */
    private long detailLevelId;

    /** Calculation method that is applied for the back end fragment. */
    private CalculationMethod calculationMethod;

    /** Start time of the back end fragment. */
    private long startTime;

    /** End time of the back end fragment. */
    private long endTime;

    /** Flag indicating whether the back end fragment is marked as corrupt or not. */
    private boolean isCorrupt;

    /** Name of the back end fragment. */
    private String fragmentName;

    /** Lock provider that is used to restrict the access to the back end fragment object. */
    private ReentrantReadWriteLock lock;

    /** Flag indicating whether the back end fragment is empty or not. */
    private Boolean isEmpty;

    /** Time of the first value that can be found within the back end fragment. */
    private Long earliestValueTime;

    /**
     * This method returns the id of the configuration of the back end fragment.
     * @return id of the configuration of the back end fragment
     */
    public String getConfigurationId ()
    {
        return this.configurationId;
    }

    /**
     * This method sets the id of the configuration of the back end fragment.
     * @param configurationId id of the configuration of the back end fragment
     */
    public void setConfigurationId ( final String configurationId )
    {
        this.configurationId = configurationId;
    }

    /**
     * This method returns the id of the detail level of the back end fragment.
     * @return id of the detail level of the back end fragment
     */
    public long getDetailLevelId ()
    {
        return this.detailLevelId;
    }

    /**
     * This method sets the id of the detail level of the back end fragment.
     * @param detailLevelId id of the detail level of the back end fragment
     */
    public void setDetailLevelId ( final long detailLevelId )
    {
        this.detailLevelId = detailLevelId;
    }

    /**
     * This method returns the calculation method that is applied for the back end fragment.
     * @return calculation method that is applied for the back end fragment
     */
    public CalculationMethod getCalculationMethod ()
    {
        return this.calculationMethod;
    }

    /**
     * This method sets the calculation method that is applied for the back end fragment.
     * @param calculationMethod calculation method that is applied for the back end fragment
     */
    public void setCalculationMethod ( final CalculationMethod calculationMethod )
    {
        this.calculationMethod = calculationMethod;
    }

    /**
     * This method returns the start time of the back end fragment.
     * @return start time of the back end fragment
     */
    public long getStartTime ()
    {
        return this.startTime;
    }

    /**
     * This method sets the start time of the back end fragment.
     * @param startTime start time of the back end fragment
     */
    public void setStartTime ( final long startTime )
    {
        this.startTime = startTime;
    }

    /**
     * This method returns the end time of the back end fragment.
     * @return end time of the back end fragment
     */
    public long getEndTime ()
    {
        return this.endTime;
    }

    /**
     * This method sets the end time of the back end fragment.
     * @param endTime end time of the back end fragment
     */
    public void setEndTime ( final long endTime )
    {
        this.endTime = endTime;
    }

    /**
     * This method returns the flag indicating whether the back end fragment is marked as corrupt or not.
     * @return flag indicating whether the back end fragment is marked as corrupt or not
     */
    public boolean getIsCorrupt ()
    {
        return this.isCorrupt;
    }

    /**
     * This method sets the flag indicating whether the back end fragment is marked as corrupt or not.
     * @param isCorrupt flag indicating whether the back end fragment is marked as corrupt or not
     */
    public void setIsCorrupt ( final boolean isCorrupt )
    {
        this.isCorrupt = isCorrupt;
    }

    /**
     * This method returns the name of the back end fragment.
     * @return name of the back end fragment
     */
    public String getFragmentName ()
    {
        return this.fragmentName;
    }

    /**
     * This method sets the name of the back end fragment.
     * @param fragmentName name of the back end fragment
     */
    public void setFragmentName ( final String fragmentName )
    {
        this.fragmentName = fragmentName;
    }

    /**
     * This method returns the lock provider that is used to restrict the access to the back end fragment object.
     * @return lock provider that is used to restrict the access to the back end fragment object
     */
    public ReentrantReadWriteLock getLock ()
    {
        return this.lock;
    }

    /**
     * This method sets the lock provider that is used to restrict the access to the back end fragment object.
     * @param lock lock provider that is used to restrict the access to the back end fragment object
     */
    public void setLock ( final ReentrantReadWriteLock lock )
    {
        this.lock = lock;
    }

    /**
     * This method returns the flag indicating whether the back end fragment is empty or not.
     * @return flag indicating whether the back end fragment is empty or not
     */
    public Boolean getIsEmpty ()
    {
        return this.isEmpty;
    }

    /**
     * This method sets the flag indicating whether the back end fragment is empty or not.
     * @param isEmpty flag indicating whether the back end fragment is empty or not
     */
    public void setIsEmpty ( final boolean isEmpty )
    {
        this.isEmpty = isEmpty;
    }

    /**
     * This method returns the time of the first value that can be found within the back end fragment.
     * @return time of the first value that can be found within the back end fragment
     */
    public Long getSupposedEarliestValueTime ()
    {
        return this.earliestValueTime;
    }

    /**
     * This method sets the time of the first value that can be found within the back end fragment.
     * @param earliestValueTime time of the first value that can be found within the back end fragment
     */
    public void setSupposedEarliestValueTime ( final Long earliestValueTime )
    {
        this.earliestValueTime = earliestValueTime;
    }

    /**
     * @see Comparable#compareTo(Object)
     */
    public int compareTo ( final BackEndFragmentInformation o )
    {
        final long otherStartTime = o.getStartTime ();
        if ( this.startTime < otherStartTime )
        {
            return 1;
        }
        if ( this.startTime > otherStartTime )
        {
            return -1;
        }
        return 0;
    }
}
