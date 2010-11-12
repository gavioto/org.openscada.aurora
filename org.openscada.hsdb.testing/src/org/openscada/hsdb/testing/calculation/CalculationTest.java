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

package org.openscada.hsdb.testing.calculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.openscada.hsdb.CalculatingStorageChannel;
import org.openscada.hsdb.calculation.MaximumCalculationLogicProvider;
import org.openscada.hsdb.calculation.NativeCalculationLogicProvider;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.utils.HsdbHelper;

public class CalculationTest
{

    private final long variance = 10;

    private TestingChannel input;

    private TestingChannel output;

    protected void createRamp ( final List<DoubleValue> values, final long startTime, final long endTime, final double endValue, final int entries )
    {
        final Random r = new Random ();
        final double ts = (double) ( endTime - startTime ) / (double)entries;
        final double step = endValue / entries;

        for ( int i = 0; i < entries; i++ )
        {
            long longCurrent = (long) ( ts * i );
            if ( this.variance > 0 )
            {
                longCurrent += r.nextLong () % ( this.variance * 2 ) - this.variance;
            }
            values.add ( new DoubleValue ( startTime + longCurrent, 1.0, 0.0, 1, i * step ) );
        }
    }

    @Before
    public void setup ()
    {
        final List<DoubleValue> values = new ArrayList<DoubleValue> ();
        createRamp ( values, 10 * 1000, 20 * 1000, 10 * 1000, 100 );
        values.add ( new DoubleValue ( 20 * 1000, 1, 0, 1, 0 ) );
        createRamp ( values, 30 * 1000, 40 * 1000, 10 * 1000, 100 );
        this.input = new TestingChannel ( "input", values );

        this.output = new TestingChannel ( "output" );

    }

    @Test
    public void test1 () throws Exception
    {
        final long[] parameters1 = new long[] { //
        1 //
        };

        final long[] parameters2 = new long[] { //
        4321 //
        };

        // final MaximumCalculationLogicProvider provider1 = new MaximumCalculationLogicProvider ( DataType.DOUBLE_VALUE, DataType.DOUBLE_VALUE, parameters1 );
        final NativeCalculationLogicProvider provider1 = new NativeCalculationLogicProvider ( DataType.DOUBLE_VALUE, DataType.DOUBLE_VALUE, parameters1 );
        final MaximumCalculationLogicProvider provider2 = new MaximumCalculationLogicProvider ( DataType.DOUBLE_VALUE, DataType.DOUBLE_VALUE, parameters2 );

        final long offset = 100;
        final long start = 0 + offset;
        final long end = 50000 + offset;
        HsdbHelper.processData ( this.input, this.output, provider1, provider2, start, end );

        final List<List<DoubleValue>> dumpValues = new ArrayList<List<DoubleValue>> ();
        dumpValues.add ( this.input.getValues () );
        dumpValues.add ( this.output.getValues () );
        dumpValues ( dumpValues, start, end );
    }

    @Test
    public void test2 () throws Exception
    {
        final long[] parameters2 = new long[] { //
        1111 //
        };

        final MaximumCalculationLogicProvider provider2 = new MaximumCalculationLogicProvider ( DataType.DOUBLE_VALUE, DataType.DOUBLE_VALUE, parameters2 );
        final CalculatingStorageChannel chan = new CalculatingStorageChannel ( this.output, this.input, provider2 );

        final long offset = 50;
        final long start = 0 + offset;
        final long end = 50000 + offset;

        chan.updateDoubles ( this.input.getValues ().toArray ( new DoubleValue[0] ) );

        final DoubleValue[] result = chan.getDoubleValues ( start, end );
        final List<List<DoubleValue>> values = new ArrayList<List<DoubleValue>> ();
        values.add ( this.input.getValues () );
        values.add ( Arrays.asList ( result ) );
        dumpValues ( values, start, end );
    }

    @Test
    public void test3 ()
    {
    }

    private void dumpValues ( final List<List<DoubleValue>> values, final long start, final long end )
    {
        final Map<Long, DoubleValue[]> map = new HashMap<Long, DoubleValue[]> ();

        final int max = values.size ();

        for ( int i = 0; i < max; i++ )
        {
            final List<DoubleValue> list = values.get ( i );
            for ( final DoubleValue entry : list )
            {
                final long time = entry.getTime ();
                if ( !map.containsKey ( time ) )
                {
                    map.put ( time, new DoubleValue[max] );
                }
                map.get ( time )[i] = entry;
            }
        }

        final ArrayList<Long> keys = new ArrayList<Long> ( map.keySet () );
        Collections.sort ( keys );

        boolean didStart = false;
        boolean didEnd = false;
        for ( final Long time : keys )
        {
            if ( time > start && !didStart )
            {
                didStart = true;
                System.out.println ( "==== START ====" );
            }
            if ( time > end && !didEnd )
            {
                didEnd = true;
                System.out.println ( "==== END ====" );
            }
            final DoubleValue[] dv = map.get ( time );
            System.out.println ( format ( time, dv ) );
        }
    }

    private String format ( final Long time, final DoubleValue[] values )
    {
        final StringBuilder sb = new StringBuilder ();
        sb.append ( String.format ( "%06d", time ) );
        sb.append ( ": " );
        for ( final DoubleValue value : values )
        {
            sb.append ( String.format ( "%60s", value ) );
            sb.append ( "\t" );
        }
        return sb.toString ();
    }
}
