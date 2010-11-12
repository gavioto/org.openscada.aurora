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

package org.openscada.hsdb.testing.backend;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.BackEnd;
import org.openscada.hsdb.backend.file.FileBackEnd;
import org.openscada.hsdb.datatypes.LongValue;

/**
 * Test class for class org.openscada.hsdb.testing.backend.FileBackEnd.
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
        final FileBackEnd backEnd = new FileBackEnd ( URLEncoder.encode ( metaData.getConfigurationId (), "utf-8" ) + ".va", false );
        new File ( backEnd.getFileName () ).delete ();
        backEnd.setLock ( new ReentrantReadWriteLock () );
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
        final RandomAccessFile raf1 = new RandomAccessFile ( file, "rw" );
        final RandomAccessFile raf2 = new RandomAccessFile ( file, "r" );
        raf1.seek ( 0L );
        raf2.seek ( 0L );
        raf1.writeLong ( value );
        raf1.getChannel ().force ( false );
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
        this.backEnd.updateLong ( new LongValue ( -1, 100, 0, 1, 0 ) );
        this.backEnd.updateLong ( new LongValue ( MAX_COUNT - 2, 100, 0, 1, 1 ) );
        this.backEnd.updateLong ( new LongValue ( MAX_COUNT - 1, 100, 0, 1, 2 ) );
        this.backEnd.updateLong ( new LongValue ( MAX_COUNT, 100, 0, 1, 3 ) );
        this.backEnd.updateLong ( new LongValue ( MAX_COUNT + 1, 100, 0, 1, 4 ) );
        final LongValue[] result = this.backEnd.getLongValues ( -10, MAX_COUNT + 10 );
        Assert.assertEquals ( 2, result.length );
        Assert.assertEquals ( 1, result[0].getValue () );
        Assert.assertEquals ( 2, result[1].getValue () );
        final LongValue[] result2 = this.backEnd.getLongValues ( MAX_COUNT - 1, MAX_COUNT - 1 );
        Assert.assertEquals ( 0, result2.length );
        final LongValue[] result3 = this.backEnd.getLongValues ( MAX_COUNT - 2, MAX_COUNT - 1 );
        Assert.assertEquals ( 1, result3.length );
        Assert.assertEquals ( 1, result3[0].getValue () );
    }

    /**
     * This method cleans all artifacts that have been created during a test run.
     * @throws Exception in case of problems
     */
    @Override
    public void cleanup () throws Exception
    {
        if ( this.backEnd instanceof FileBackEnd )
        {
            final String fileName = ( (FileBackEnd)this.backEnd ).getFileName ();
            super.cleanup ();
            new File ( fileName ).delete ();
        }
    }
}
