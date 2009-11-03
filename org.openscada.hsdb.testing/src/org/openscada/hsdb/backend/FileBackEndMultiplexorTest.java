package org.openscada.hsdb.backend;

import java.io.File;

import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.file.FileBackEndFactory;
import org.openscada.hsdb.backend.file.FileBackEndManager;
import org.openscada.hsdb.backend.file.FileBackEndManagerFactory;
import org.openscada.hsdb.configuration.Configuration;
import org.openscada.hsdb.configuration.Conversions;

/**
 * Test class for following classes:
 * org.openscada.hsdb.backend.FileBackEndFactory.
 * org.openscada.hsdb.backend.BackEndMultiplexor.
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
        manager = new FileBackEndManager ( configuration, backEndManagerFactory, backEndFactory );
        manager.delete ();
        manager = null;
        System.gc ();
        manager = backEndManagerFactory.getBackEndManager ( configuration, true );
        manager.initialize ();
        final BackEndMultiplexer backEnd = new BackEndMultiplexer ( manager );
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
        if ( PERFORM_CLEANUP && ( manager != null ) )
        {
            manager.delete ();
            new File ( ROOT ).delete ();
        }
    }
}
