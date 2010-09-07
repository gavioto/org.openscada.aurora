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

package org.openscada.hsdb;

import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.datatypes.DataType;

/**
 * This class provides methods for accessing meta information of storage channels.
 * @author Ludwig Straub
 */
public class StorageChannelMetaData
{
    /** Unique id specifying the combination of the meta data information. */
    private String configurationId;

    /** Method that is used to calculate the data that is stored in the channel. */
    private CalculationMethod calculationMethod;

    /** Parameters that are used in combination with the calculation method to specify the algorithm that has to be applied. */
    private long[] calculationMethodParameters;

    /** Detail level of the stored data. */
    private long detailLevelId;

    /** Time stamp of first possible entry of the channel. */
    private long startTime;

    /** Time stamp of first entry that will not be stored in the channel. */
    private long endTime;

    /** Age of the data in milliseconds a stored data should be kept available. */
    private long proposedDataAge;

    /** Maximum time in milliseconds the new value can differ from the current time in order to be processed. */
    private long acceptedTimeDelta;

    /** Data type of the stored values. */
    private DataType dataType;

    /**
     * Copy constructor
     * @param storageChannelMetaData instance to copy data from
     */
    public StorageChannelMetaData ( final StorageChannelMetaData storageChannelMetaData )
    {
        this ( storageChannelMetaData.getConfigurationId (), storageChannelMetaData.getCalculationMethod (), storageChannelMetaData.getCalculationMethodParameters (), storageChannelMetaData.getDetailLevelId (), storageChannelMetaData.getStartTime (), storageChannelMetaData.getEndTime (), storageChannelMetaData.getProposedDataAge (), storageChannelMetaData.getAcceptedTimeDelta (), storageChannelMetaData.getDataType () );
    }

    /**
     * Fully initializing constructor
     * @param configurationId unique id specifying the combination of the meta data information
     * @param calculationMethod method that is used to calculate the data that is stored in the channel
     * @param calculationMethodParameters parameters that are used in combination with the calculation method to specify the algorithm that has to be applied
     * @param detailLevelId detail level of the stored data
     * @param startTime time stamp of first possible entry of the channel
     * @param endTime time stamp of first entry that will not be stored in the channel
     * @param proposedDataAge age of the data in milliseconds a stored data should be kept available
     * @param acceptedTimeDelta maximum time in milliseconds the new value can differ from the current time in order to be processed
     * @param dataType data type of the stored values
     */
    public StorageChannelMetaData ( final String configurationId, final CalculationMethod calculationMethod, final long[] calculationMethodParameters, final long detailLevelId, final long startTime, final long endTime, final long proposedDataAge, final long acceptedTimeDelta, final DataType dataType )
    {
        this.configurationId = configurationId;
        this.calculationMethod = calculationMethod;
        this.calculationMethodParameters = calculationMethodParameters != null ? calculationMethodParameters : new long[0];
        this.detailLevelId = detailLevelId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.proposedDataAge = proposedDataAge;
        this.acceptedTimeDelta = acceptedTimeDelta;
        this.dataType = dataType;
    }

    /**
     * This method returns the unique id specifying the combination of the meta data information.
     * @return unique id specifying the combination of the meta data information
     */
    public String getConfigurationId ()
    {
        return this.configurationId;
    }

    /**
     * This method sets the unique id specifying the combination of the meta data information.
     * @param configurationId unique id specifying the combination of the meta data information
     */
    public void setConfigurationId ( final String configurationId )
    {
        this.configurationId = configurationId;
    }

    /**
     * This method returns the method that is used to calculate the data that is stored in the channel.
     * @return method that is used to calculate the data that is stored in the channel
     */
    public CalculationMethod getCalculationMethod ()
    {
        return this.calculationMethod;
    }

    /**
     * This method sets the method that is used to calculate the data that is stored in the channel.
     * @param calculationMethod method that is used to calculate the data that is stored in the channel
     */
    public void setCalculationMethod ( final CalculationMethod calculationMethod )
    {
        this.calculationMethod = calculationMethod;
    }

    /**
     * This method returns the parameters that are used in combination with the calculation method to specify the algorithm that has to be applied.
     * @return parameters that are used in combination with the calculation method to specify the algorithm that has to be applied. If no parameter is returned, an empty array is returned
     */
    public long[] getCalculationMethodParameters ()
    {
        return this.calculationMethodParameters;
    }

    /**
     * This method sets the parameters that are used in combination with the calculation method to specify the algorithm that has to be applied.
     * @param calculationMethodParameters parameters that are used in combination with the calculation method to specify the algorithm that has to be applied
     */
    public void setCalculationMethodParameters ( final long[] calculationMethodParameters )
    {
        this.calculationMethodParameters = calculationMethodParameters != null ? calculationMethodParameters : new long[0];
    }

    /**
     * This method returns the detail level of the stored data.
     * @return detail level of the stored data
     */
    public long getDetailLevelId ()
    {
        return this.detailLevelId;
    }

    /**
     * This method sets the detail level of the stored data.
     * @param detailLevelId detail level of the stored data
     */
    public void setDetailLevelId ( final long detailLevelId )
    {
        this.detailLevelId = detailLevelId;
    }

    /**
     * This method returns the time stamp of first possible entry of the channel
     * @return time stamp of first possible entry of the channel
     */
    public long getStartTime ()
    {
        return this.startTime;
    }

    /**
     * This method sets the time stamp of first possible entry of the channel.
     * @param startTime time stamp of first possible entry of the channel
     */
    public void setStartTime ( final long startTime )
    {
        this.startTime = startTime;
    }

    /**
     * This method returns the time stamp of first entry that will not be stored in the channel.
     * @return time stamp of first entry that will not be stored in the channel
     */
    public long getEndTime ()
    {
        return this.endTime;
    }

    /**
     * This method sets the time stamp of first entry that will not be stored in the channel.
     * @param endTime time stamp of first entry that will not be stored in the channel
     */
    public void setEndTime ( final long endTime )
    {
        this.endTime = endTime;
    }

    /**
     * This method returns the age of the data in milliseconds a stored data should be kept available.
     * @return age of the data in milliseconds a stored data should be kept available
     */
    public long getProposedDataAge ()
    {
        return this.proposedDataAge;
    }

    /**
     * This method sets the age of the data in milliseconds a stored data should be kept available.
     * @param proposedDataAge age of the data in milliseconds a stored data should be kept available
     */
    public void setProposedDataAge ( final long proposedDataAge )
    {
        this.proposedDataAge = proposedDataAge;
    }

    /**
     * This method returns the maximum time in milliseconds the new value can differ from the current time in order to be processed.
     * @return maximum time in milliseconds the new value can differ from the current time in order to be processed
     */
    public long getAcceptedTimeDelta ()
    {
        return this.acceptedTimeDelta;
    }

    /**
     * This method sets the maximum time in milliseconds the new value can differ from the current time in order to be processed.
     * @param acceptedTimeDelta maximum time in milliseconds the new value can differ from the current time in order to be processed
     */
    public void setAcceptedTimeDelta ( final long acceptedTimeDelta )
    {
        this.acceptedTimeDelta = acceptedTimeDelta;
    }

    /**
     * This method returns the data type of the stored values.
     * @return data type of the stored values
     */
    public DataType getDataType ()
    {
        return this.dataType;
    }

    /**
     * This method sets the data type of the stored values.
     * @param dataType data type of the stored values
     */
    public void setDataType ( final DataType dataType )
    {
        this.dataType = dataType;
    }

    /**
     * This method transform the data to a String and provides output for debugging.
     * @return data transformed to a String
     */
    @Override
    public String toString ()
    {
        return String.format ( "configurationId: %s; calculationMethod: %s; detailLevel: %s; startTime: %s; endTime: %s; proposedDataAge: %s; acceptedFutureTime: %s; datatype: %s", this.configurationId, CalculationMethod.convertCalculationMethodToString ( this.calculationMethod ), this.detailLevelId, this.startTime, this.endTime, this.proposedDataAge, this.acceptedTimeDelta, this.dataType );
    }
}
