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

package org.openscada.hsdb.testing.compression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.hsdb.CalculatingStorageChannel;
import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.ExtendedStorageChannelAdapter;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.file.FileBackEnd;
import org.openscada.hsdb.backend.file.FileBackEndFactory;
import org.openscada.hsdb.backend.file.FileBackEndManager;
import org.openscada.hsdb.backend.file.FileBackEndManagerFactory;
import org.openscada.hsdb.calculation.AverageCalculationLogicProvider;
import org.openscada.hsdb.calculation.CalculationLogicProvider;
import org.openscada.hsdb.configuration.Configuration;
import org.openscada.hsdb.configuration.Conversions;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;

/**
 * This class has been created to reproduce a misbehaviour on the test server.
 * The input data is located in project root.
 * @author Ludwig Straub
 */
public class CalculatingStorageChannelTest
{
    /** Base directory for test files. */
    private final static String ROOT = "C:/Temp/va_base_test2";

    /**
     * This method fills the passed map with default settings if it is passed empty.
     * @param properties map to be filled with default settings if map is passed empty
     */
    private static void fillConfigurationDefaultSettings ( final Map<String, String> properties )
    {
        if ( properties != null && properties.isEmpty () )
        {
            properties.put ( Configuration.PROPOSED_DATA_AGE_KEY_PREFIX + 0, "2d" );
            properties.put ( Configuration.PROPOSED_DATA_AGE_KEY_PREFIX + 1, "90d" );
            properties.put ( Configuration.PROPOSED_DATA_AGE_KEY_PREFIX + 2, "5y" );
            properties.put ( Configuration.PROPOSED_DATA_AGE_KEY_PREFIX + 3, "10y" );
            properties.put ( Configuration.PROPOSED_DATA_AGE_KEY_PREFIX + 4, "15y" );
            properties.put ( Configuration.COMPRESSION_TIMESPAN_KEY_PREFIX + 1, "1s" );
            properties.put ( Configuration.COMPRESSION_TIMESPAN_KEY_PREFIX + 2, "1m" );
            properties.put ( Configuration.COMPRESSION_TIMESPAN_KEY_PREFIX + 3, "10m" );
            properties.put ( Configuration.COMPRESSION_TIMESPAN_KEY_PREFIX + 4, "1h" );
            properties.put ( Configuration.ACCEPTED_TIME_DELTA_KEY, "90m" );
            properties.put ( Configuration.MAX_COMPRESSION_LEVEL, "4" );
            properties.put ( Configuration.DATA_TYPE_KEY, DataType.convertDataTypeToShortString ( DataType.DOUBLE_VALUE ) );
            properties.put ( Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 0, "1d" );
            properties.put ( Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 1, "1d" );
            properties.put ( Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 2, "100d" );
            properties.put ( Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 3, "1y" );
            properties.put ( Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 4, "5y" );
            properties.put ( Configuration.CALCULATION_METHODS, "AVG,MIN,MAX" );
        }
    }

    /**
     * Test for compressing live data.
     * @throws Exception if test fails
     */
    @Test
    public void calculateData () throws Exception
    {
        final CalculationLogicProvider clp = new AverageCalculationLogicProvider ( DataType.DOUBLE_VALUE, DataType.DOUBLE_VALUE, new long[] { 1000 } );
        final DoubleValue dv = (DoubleValue)clp.generateValue ( new DoubleValue[] { new DoubleValue ( 1, 1, 0, 1, 1 ), new DoubleValue ( 2, 1, 0.5, 1, 1 ), new DoubleValue ( 3, 1, 0.5, 1, 1 ) } );
        Assert.assertTrue ( "Invalid manual value calculated!", dv.getManualIndicator () == 0.25 );
    }

    /**
     * Test for compressing live data.
     * @throws Exception if test fails
     */
    @Test
    public void compressData () throws Exception
    {
        // get input data
        final FileBackEnd inputBackEnd = new FileBackEnd ( "foobar_0_NAT_20091113.000000.000.0_20091114.000000.000.0.va", false );
        inputBackEnd.initialize ( null );
        final StorageChannelMetaData metaData = inputBackEnd.getMetaData ();
        final ExtendedStorageChannel inputChannel = new ExtendedStorageChannelAdapter ( inputBackEnd );
        final List<DoubleValue> doubleValues = new ArrayList<DoubleValue> ( Arrays.asList ( inputChannel.getDoubleValues ( Long.MIN_VALUE, Long.MAX_VALUE ) ) );
        inputBackEnd.deinitialize ();

        // prepare configuration
        final Map<String, String> data = new HashMap<String, String> ();
        fillConfigurationDefaultSettings ( data );
        data.put ( Configuration.MANAGER_CONFIGURATION_ID, metaData.getConfigurationId () );
        data.put ( Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 0, 1 + Conversions.DAY_SPAN_SUFFIX );
        final FileBackEndFactory backEndFactory = new FileBackEndFactory ( ROOT, 0 );
        final FileBackEndManagerFactory backEndManagerFactory = new FileBackEndManagerFactory ( backEndFactory );
        final Configuration configuration = new Configuration ();
        configuration.setId ( metaData.getConfigurationId () );
        configuration.setData ( data );
        final FileBackEndManager manager = backEndManagerFactory.getBackEndManager ( configuration, true );
        manager.initialize ();
        final CalculatingStorageChannel rootChannel = manager.buildStorageChannelTree ();

        // write data
        long count = 0;
        for ( final DoubleValue doubleValue : doubleValues )
        {
            rootChannel.updateDouble ( doubleValue );
            if ( count++ % 100 == 0 )
            {
                rootChannel.cleanupRelicts ();
            }
        }
    }
}
