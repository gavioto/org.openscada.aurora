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

package org.openscada.hsdb.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides configuration data that can be used to build up a structure of multiple back end objects that are related to the same configuration id.
 * @author Ludwig Straub
 */
public class Configuration
{
    /** Key in configuration containing the id of the configuration. */
    public final static String MANAGER_CONFIGURATION_ID = "hsdb.manager.configurationId";

    /** Prefix of key in configuration containing the time span of a back end fragment for the specified level. */
    public final static String MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX = "hsdb.manager.fragment.timespan.level.";

    /** Key in configuration for the count of known files. */
    public final static String MANAGER_KNOWN_FRAGMENTS_COUNT = "hsdb.manager.knownFragmentsCount";

    /** Prefix of key in configuration for the name of a known fragment. */
    public final static String MANAGER_KNOWN_FRAGMENT_NAME_PREFIX = "hsdb.manager.knownFragment.name.";

    /** Prefix of key in configuration for the corrupt status of a known fragment. */
    public final static String MANAGER_KNOWN_FRAGMENT_CORRUPT_STATUS_PREFIX = "hsdb.manager.knownFragment.corruptStatus.";

    /** Prefix of key in configuration for the compression level of a known fragment. */
    public final static String MANAGER_KNOWN_FRAGMENT_COMPRESSION_LEVEL_PREFIX = "hsdb.manager.knownFragment.compressionLevel.";

    /** Prefix of key in configuration for the calculation method of a known fragment. */
    public final static String MANAGER_KNOWN_FRAGMENT_CALCULATION_METHOD_PREFIX = "hsdb.manager.knownFragment.calculationMethod.";

    /** Prefix of key in configuration for the start time of a known fragment. */
    public final static String MANAGER_KNOWN_FRAGMENT_START_TIME_PREFIX = "hsdb.manager.knownFragment.startTime.";

    /** Prefix of key in configuration for the end time of a known fragment. */
    public final static String MANAGER_KNOWN_FRAGMENT_END_TIME_PREFIX = "hsdb.manager.knownFragment.endTime.";

    /** Prefix of key in configuration for the proposed data age setting. */
    public final static String PROPOSED_DATA_AGE_KEY_PREFIX = "hsdb.proposedDataAge.level.";

    /** Key in configuration for the accepted future time setting. */
    public final static String ACCEPTED_TIME_DELTA_KEY = "hsdb.acceptedTimeDelta";

    /** Prefix of key in configuration for the compression time span setting. */
    public final static String COMPRESSION_TIMESPAN_KEY_PREFIX = "hsdb.compressionTimeSpan.level.";

    /** Key in configuration for the data type setting of NATIVE calculation method setting. */
    public final static String DATA_TYPE_KEY = "hsdb.dataType";

    /** Key in configuration for the set of configured calculation methods setting. */
    public final static String CALCULATION_METHODS = "hsdb.calculationMethods";

    /** Key in configuration for the maximum compression level setting. */
    public final static String MAX_COMPRESSION_LEVEL = "hsdb.maxCompressionLevel";

    /** Id of the configuration itself. */
    private String id;

    /** Additional configuration data. */
    private Map<String, String> data;

    /**
     * Standard constructor.
     */
    public Configuration ()
    {
    }

    /**
     * Copy constructor.
     * @param configuration configuration object from which data has to be copied
     */
    public Configuration ( final Configuration configuration )
    {
        if ( configuration != null )
        {
            this.id = configuration.getId ();
            setData ( new HashMap<String, String> ( configuration.getData () ) );
        }
    }

    /**
     * This method returns the id of the configuration itself.
     * @return id of the configuration itself
     */
    public String getId ()
    {
        return this.id;
    }

    /**
     * This method sets the id of the configuration itself.
     * @param id id of the configuration itself
     */
    public void setId ( final String id )
    {
        this.id = id;
    }

    /**
     * This method return the internal map with additional configuration data.
     * @return additional configuration data
     */
    public Map<String, String> getData ()
    {
        return this.data == null ? null : this.data;
    }

    /**
     * This method sets the additional configuration data.
     * @param data additional configuration data
     */
    public void setData ( final Map<String, String> data )
    {
        this.data = data == null ? null : new HashMap<String, String> ( data );
    }
}
