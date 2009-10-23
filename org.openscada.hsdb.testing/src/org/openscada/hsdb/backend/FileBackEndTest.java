package org.openscada.hsdb.backend;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URLEncoder;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.file.FileBackEnd;
import org.openscada.hsdb.datatypes.LongValue;

/**
 * Test class for class org.openscada.hsdb.backend.FileBackEnd.
 * @author Ludwig Straub
 */
public class FileBackEndTest extends BackEndTestBase
{
    /**
     * This method creates, initializes and returns the backend that has to be tested.
     * If a backend with the same meta data already exists, the old back end will be deleted.
     * @param metaData metadata that should be used when creating a back end
     * @return backend that has to be tested
     * @throws Exception in case of problems
     */
    @Override
    protected BackEnd createBackEnd ( final StorageChannelMetaData metaData ) throws Exception
    {
        final BackEnd backEnd = new FileBackEnd ( URLEncoder.encode ( metaData.getConfigurationId (), "utf-8" ) + ".va", false );
        backEnd.delete ();
        backEnd.create ( metaData );
        backEnd.initialize ( metaData );
        return backEnd;
    }

    /**
     * This method tests if a file that is currently opened for writing can be read in parallel.
     * @throws Exception in case of problems
     */
    @Test
    public void fileReadTest () throws Exception
    {
        final long value = 1L;
        final File file = new File ( "test.txt" );
        final RandomAccessFile raf1 = new RandomAccessFile ( file, "rwd" );
        final RandomAccessFile raf2 = new RandomAccessFile ( file, "r" );
        raf1.seek ( 0L );
        raf2.seek ( 0L );
        raf1.writeLong ( value );
        Assert.assertTrue ( raf1.length () == 8 );
        Assert.assertTrue ( raf2.readLong () == value );
        raf2.close ();
        raf1.close ();
        Assert.assertTrue ( file.delete () );
    }

    /**
     * Test for bound checking with long values
     * @throws Exception if test fails
     */
    @Test
    public void testLongBounds () throws Exception
    {
        backEnd.updateLong ( new LongValue ( -1, 100, 0, 1, 0 ) );
        backEnd.updateLong ( new LongValue ( MAX_COUNT - 2, 100, 0, 1, 1 ) );
        backEnd.updateLong ( new LongValue ( MAX_COUNT - 1, 100, 0, 1, 2 ) );
        backEnd.updateLong ( new LongValue ( MAX_COUNT, 100, 0, 1, 3 ) );
        backEnd.updateLong ( new LongValue ( MAX_COUNT + 1, 100, 0, 1, 4 ) );
        final LongValue[] result = backEnd.getLongValues ( -10, MAX_COUNT + 10 );
        Assert.assertEquals ( 2, result.length );
        Assert.assertEquals ( 1, result[0].getValue () );
        Assert.assertEquals ( 2, result[1].getValue () );
        final LongValue[] result2 = backEnd.getLongValues ( MAX_COUNT - 1, MAX_COUNT - 1 );
        Assert.assertEquals ( 0, result2.length );
        final LongValue[] result3 = backEnd.getLongValues ( MAX_COUNT - 2, MAX_COUNT - 1 );
        Assert.assertEquals ( 1, result3.length );
        Assert.assertEquals ( 1, result3[0].getValue () );
        backEnd.deinitialize ();
        backEnd.delete ();
    }
}
