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

import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.calculation.CalculationMethod;

/**
 * Interface for accessing storage channel backend objects that are suitable for specific circumstances.
 * These circumstances and constraints have to be passed as input parameters.
 * @author Ludwig Straub
 */
public interface BackEndFactory
{
    /**
     * This method returns the metadata objects of all existing back end objects.
     * If merge mode is specified then the following applies:
     * If more than one metadata object exists for the same configuration, calculation
     * method and detail level then the additional information is merged into one single meta data object.
     * The time span is hereby widened so that the earliest start time is used and the latest end time.
     * All other information is taken from the sub meta data object with the latest end time.
     * @param merge flag indicating whether the result object should be merged or not
     * @return metadata objects of all existing back end objects
     * @throws Exception in case of any problems
     */
    public abstract StorageChannelMetaData[] getExistingBackEndsMetaData ( final boolean merge ) throws Exception;

    /**
     * This method returns the metadata objects of all existing back end objects for the specified configuration.
     * If merge mode is specified then the following applies:
     * If more than one metadata object exists for the same configuration, calculation
     * method and detail level then the additional information is merged into one single meta data object.
     * The time span is hereby widened so that the earliest start time is used and the latest end time.
     * All other information is taken from the sub meta data object with the latest end time.
     * @param configurationId id of configuration for which all existing back end objects have to be retrieved
     * @param merge flag indicating whether the result object should be merged or not
     * @return metadata objects of all existing back end objects
     * @throws Exception in case of any problems
     */
    public abstract StorageChannelMetaData[] getExistingBackEndsMetaData ( final String configurationId, final boolean merge ) throws Exception;

    /**
     * This method returns all currently available and previously created backends matching the specified constraints.
     * @param configurationId id of the configuration for which the backends should be retrieved
     * @param detailLevelId detail level of the stored data
     * @param calculationMethod calculation method of the data source for which the backends should be retrieved
     * @return all currently available and previously created backends matching the specified constraints. If no backend objects are returned, an empty array is returned.
     * @throws Exception in case of any problems
     */
    public abstract BackEnd[] getExistingBackEnds ( final String configurationId, final long detailLevelId, final CalculationMethod calculationMethod ) throws Exception;

    /**
     * This method creates and returns a new storage channel backend object that can be used to store data matching the specified metadata.
     * The create method of the created object is not called by this method. This call has to be performed manually.
     * @param storageChannelMetaData meta data that has to be used as input when creating a new storage channel backend
     * @return new storage channel backend object that can be used to store data matching the specified metadata
     * @throws Exception in case of any problems
     */
    public BackEnd createNewBackEnd ( final StorageChannelMetaData storageChannelMetaData ) throws Exception;

    /**
     * This method deletes all back ends with configuration id.
     * @param configurationId id of configuration of back end objects that have to be deleted
     */
    public abstract void deleteBackEnds ( final String configurationId );
}
