package org.openscada.hsdb.testing.backend;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.BackEnd;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.LongValue;

/**
 * Test class provides methods for testing implementations of interface org.openscada.hsdb.testing.backend.BackEnd.
 * @author Ludwig Straub
 */
public abstract class BackEndTestBase
{
    /** This flag can be used to skip cleaning the created artefacts. Some tests will fail then but the relicts can be useful for testing. */
    public final static boolean PERFORM_CLEANUP = true;

    /** Maximum entries per test storage channel backend. */
    protected final static int MAX_COUNT = 500;

    /** Configuration id that is used for the tests. */
    private final static String CONFIGURATION_ID = "Conf�g_ura<tio\\n?:#\"'";

    /** Back end instance that is used for testing. */
    protected BackEnd backEnd;

    /**
     * This method creates, initializes and returns the backend manager for the back end that has to be tested.
     * If data with the same meta data already exists, the old back end will be deleted.
     * @param metaData metadata that should be used when creating a back end
     * @return backend manager for the back end that has to be tested
     * @throws Exception in case of problems
     */
    protected abstract BackEnd createBackEnd ( StorageChannelMetaData metaData ) throws Exception;

    /**
     * Test for creating a storage channel backend.
     * @throws Exception if test fails
     */
    @Before
    public void testFileCreate () throws Exception
    {
        backEnd = createBackEnd ( new StorageChannelMetaData ( CONFIGURATION_ID, CalculationMethod.NATIVE, new long[0], 0, 0, MAX_COUNT, Long.MAX_VALUE, Long.MAX_VALUE, DataType.LONG_VALUE ) );
    }

    /**
     * Test for adding a long value to the storage channel backend.
     * @throws Exception if test fails
     */
    @Test
    public void testAddLong1Data () throws Exception
    {
        backEnd.updateLong ( new LongValue ( 200, 100, 0, 1, 42 ) );
        backEnd.updateLongs ( new LongValue[] { new LongValue ( 204, 100, 0, 1, 46 ), new LongValue ( 202, 100, 0, 1, 44 ), new LongValue ( 203, 100, 0, 1, 45 ), new LongValue ( 201, 100, 0, 1, 43 ) } );
    }

    /**
     * Test for selecting a long value from the storage channel backend.
     * @throws Exception if test fails
     */
    @Test
    public void testSelectLong1Data () throws Exception
    {
        testAddLong1Data ();
        Assert.assertEquals ( 1, backEnd.getLongValues ( 200, 201 ).length );
        Assert.assertEquals ( 4, backEnd.getLongValues ( 201, 205 ).length );
        Assert.assertEquals ( 2, backEnd.getLongValues ( 201, 203 ).length );
        Assert.assertEquals ( 1, backEnd.getLongValues ( 210, 220 ).length );
    }

    /**
     * Test for adding lots of long values to the storage channel backend.
     * @throws Exception if test fails
     */
    @Test
    public void testRapidLong1DataInsert () throws Exception
    {
        for ( long i = 0; i < MAX_COUNT; i++ )
        {
            backEnd.updateLong ( new LongValue ( i, 100, 0, 1, i ) );
        }
        Assert.assertEquals ( MAX_COUNT, backEnd.getLongValues ( 0, MAX_COUNT ).length );
    }

    /**
     * Test for adding lots of long values as bulk to the storage channel backend.
     * @throws Exception if test fails
     */
    @Test
    public void testRapidLong1DataBulkInsert () throws Exception
    {
        final LongValue[] valuesToInsert = new LongValue[MAX_COUNT];
        for ( int i = 0; i < MAX_COUNT; i++ )
        {
            valuesToInsert[i] = new LongValue ( i, 100, 0, 1, i );
        }
        backEnd.updateLongs ( valuesToInsert );
        Assert.assertEquals ( 4, backEnd.getLongValues ( 201, 205 ).length );
        Assert.assertEquals ( MAX_COUNT, backEnd.getLongValues ( 0, MAX_COUNT + 1 ).length );
    }

    /**
     * This method deletes the created data after the test.
     * @throws Exception in case of problems
     */
    @After
    public void cleanup () throws Exception
    {
        if ( PERFORM_CLEANUP )
        {
            if ( backEnd != null )
            {
                backEnd.deinitialize ();
            }
        }
    }
}
