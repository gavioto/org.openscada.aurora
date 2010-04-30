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

package org.openscada.hsdb.testing.backend;

import java.io.File;

import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.BackEnd;
import org.openscada.hsdb.backend.BackEndMultiplexer;
import org.openscada.hsdb.backend.file.FileBackEndFactory;
import org.openscada.hsdb.backend.file.FileBackEndManager;
import org.openscada.hsdb.backend.file.FileBackEndManagerFactory;
import org.openscada.hsdb.configuration.Configuration;
import org.openscada.hsdb.configuration.Conversions;

/**
 * Test class for following classes:
 * org.openscada.hsdb.testing.backend.FileBackEndFactory.
 * org.openscada.hsdb.testing.backend.BackEndMultiplexor.
 * @author Ludwig Straub
 */
public class FileBackEndMultiplexorTest extends BackEndTestBase
{
    /** Base directory for test files. */
    private final static String ROOT = "va_base_test";

    /** Manager that will be used to create the back end objects. */
    private FileBackEndManager manager = null;

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
        final Configuration configuration = Conversions.convertMetaDatasToConfiguration ( new StorageChannelMetaData[] { metaData } );
        configuration.getData ().put ( Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + 0, MAX_COUNT + Conversions.MILLISECOND_SPAN_SUFFIX );
        final FileBackEndFactory backEndFactory = new FileBackEndFactory ( ROOT, 0 );
        final FileBackEndManagerFactory backEndManagerFactory = new FileBackEndManagerFactory ( backEndFactory );
        this.manager = new FileBackEndManager ( configuration, backEndManagerFactory, backEndFactory );
        this.manager.delete ();
        backEndFactory.deleteBackEnds ( configuration.getId () );
        this.manager = null;
        System.gc ();
        this.manager = backEndManagerFactory.getBackEndManager ( configuration, true );
        this.manager.initialize ();
        final BackEndMultiplexer backEnd = new BackEndMultiplexer ( this.manager );
        backEnd.initialize ( metaData );
        return backEnd;
    }

    /**
     * This method cleans all artefacts that have been created during a test run.
     * @throws Exception in case of problems
     */
    @Override
    public void cleanup () throws Exception
    {
        super.cleanup ();
        if ( PERFORM_CLEANUP && this.manager != null )
        {
            this.manager.delete ();
            new File ( ROOT ).delete ();
        }
    }
}
