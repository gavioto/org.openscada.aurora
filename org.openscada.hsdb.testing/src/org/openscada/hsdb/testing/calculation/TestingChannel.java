package org.openscada.hsdb.testing.calculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;

public class TestingChannel implements ExtendedStorageChannel
{

    private final List<DoubleValue> values;

    private final String name;

    public TestingChannel ( final String name, final List<DoubleValue> values )
    {
        this.name = name;
        this.values = new ArrayList<DoubleValue> ( values );
    }

    public TestingChannel ( final String name )
    {
        this.name = name;
        this.values = new ArrayList<DoubleValue> ();
    }

    public List<DoubleValue> getValues ()
    {
        return this.values;
    }

    public void findValues ( final List<DoubleValue> result, final long startTime, final long endTime )
    {
        System.out.println ( String.format ( "Read %s: %s -> %s", this.name, startTime, endTime ) );

        int count = 0;
        for ( final DoubleValue value : this.values )
        {
            final long time = value.getTime ();
            if ( time >= startTime && time <= endTime )
            {
                count++;
                result.add ( value );
            }
        }

        System.out.println ( String.format ( "Read returns %s entries", count ) );
    }

    @Override
    public DoubleValue[] getDoubleValues ( final long startTime, final long endTime ) throws Exception
    {
        final List<DoubleValue> result = new ArrayList<DoubleValue> ();
        findValues ( result, startTime, endTime );
        return result.toArray ( new DoubleValue[0] );
    }

    @Override
    public LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        return null;
    }

    @Override
    public void updateDouble ( final DoubleValue doubleValue ) throws Exception
    {
        this.values.add ( doubleValue );
    }

    @Override
    public void updateDoubles ( final DoubleValue[] doubleValues ) throws Exception
    {
        this.values.addAll ( Arrays.asList ( doubleValues ) );
    }

    @Override
    public void cleanupRelicts () throws Exception
    {
    }

    @Override
    public StorageChannelMetaData getMetaData () throws Exception
    {
        return null;
    }

    @Override
    public void updateLong ( final LongValue longValue ) throws Exception
    {
        System.out.println ( "" + longValue );
    }

    @Override
    public void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        for ( final LongValue value : longValues )
        {
            System.out.println ( "" + value );
        }
    }

}
