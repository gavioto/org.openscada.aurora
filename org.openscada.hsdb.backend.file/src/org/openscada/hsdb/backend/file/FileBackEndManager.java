package org.openscada.hsdb.backend.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.backend.BackEndFragmentInformation;
import org.openscada.hsdb.backend.BackEndManagerBase;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.configuration.Configuration;
import org.openscada.hsdb.configuration.Conversions;
import org.openscada.hsdb.datatypes.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides functionality for managing back end objects concerning FileBackEnd objects.
 * @author Ludwig Straub
 */
public class FileBackEndManager extends BackEndManagerBase<FileBackEnd>
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( FileBackEndManager.class );

    /**
     * Constructor.
     * @param configuration configuration of the manager instance
     * @param fileBackEndManagerFactory factory that has been used to create this instance
     * @param fileBackEndFactory factory that will be used to create new FileBackEnd objects
     */
    public FileBackEndManager ( final Configuration configuration, final FileBackEndManagerFactory fileBackEndManagerFactory, final FileBackEndFactory fileBackEndFactory )
    {
        super ( configuration, fileBackEndManagerFactory, fileBackEndFactory, new FileBackEnd[0] );
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#initialize()
     */
    @Override
    public void initialize () throws Exception
    {
        final Configuration configuration = getConfiguration ();
        final Map<String, String> data = configuration.getData ();
        if ( data == null )
        {
            throw new Exception ( "configuration is not initialized!" );
        }
        final long fragmentCount = Conversions.parseLong ( data.get ( Configuration.MANAGER_KNOWN_FRAGMENTS_COUNT ), 0 );
        final String configurationId = configuration.getId ();
        if ( fragmentCount == 0 )
        {
            // search for files in the folder
            logger.info ( "no fragment information could be located in the configuration file for configuration with id '{}'. existing fragments will be evaluated and combined to a new configuration", configurationId );
            try
            {
                final FileBackEndFactory backEndFactory = (FileBackEndFactory)getBackEndFactory ();
                final StorageChannelMetaData[] metaDatas = backEndFactory.getExistingBackEndsMetaData ( configurationId, false );
                if ( ( metaDatas != null ) && ( metaDatas.length > 0 ) )
                {
                    final List<BackEndFragmentInformation> fragmentInformations = new ArrayList<BackEndFragmentInformation> ();
                    for ( final StorageChannelMetaData metaData : metaDatas )
                    {
                        final BackEndFragmentInformation backEndFragmentInformation = new BackEndFragmentInformation ();
                        backEndFragmentInformation.setConfigurationId ( configurationId );
                        backEndFragmentInformation.setLock ( new ReentrantReadWriteLock () );
                        backEndFragmentInformation.setCalculationMethod ( metaData.getCalculationMethod () );
                        backEndFragmentInformation.setDetailLevelId ( metaData.getDetailLevelId () );
                        backEndFragmentInformation.setStartTime ( metaData.getStartTime () );
                        backEndFragmentInformation.setEndTime ( metaData.getEndTime () );
                        backEndFragmentInformation.setFragmentName ( backEndFactory.generateFileName ( metaData ) );
                        backEndFragmentInformation.setIsCorrupt ( false );
                        fragmentInformations.add ( backEndFragmentInformation );
                    }
                    for ( int i = 0; i < fragmentInformations.size (); i++ )
                    {
                        addBackEndFragmentInformation ( fragmentInformations.get ( i ), i == fragmentInformations.size () - 1 );
                    }
                    flushConfiguration ();
                }
            }
            catch ( final Exception e )
            {
                logger.error ( String.format ( "could not retrieve meta data information of existing back end fragments for configuration with id '%s'", configurationId ), e );
            }
            initialized = true;
        }
        else
        {
            super.initialize ();
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerBase#delete()
     */
    @Override
    public void delete ()
    {
        // delete control file
        super.delete ();

        // delete back end files
        ( (FileBackEndFactory)getBackEndFactory () ).deleteBackEnds ( getConfiguration ().getId () );
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerBase#createBackEnd(org.openscada.hsdb.backend.BackEndFragmentInformation, boolean)
     */
    @Override
    protected FileBackEnd createBackEnd ( final BackEndFragmentInformation backEndInformation, final boolean initialize, final boolean keepOpen ) throws Exception
    {
        final Map<String, String> data = getConfiguration ().getData ();
        final String configurationId = backEndInformation.getConfigurationId ();
        final CalculationMethod calculationMethod = backEndInformation.getCalculationMethod ();
        final long detailLevelId = backEndInformation.getDetailLevelId ();
        final long startTime = backEndInformation.getStartTime ();
        final long endTime = backEndInformation.getEndTime ();
        final String fileName = backEndInformation.getFragmentName ();
        final long[] calculationMethodParameters = detailLevelId == 0 ? new long[0] : new long[] {};
        long proposedDataAge = Conversions.parseLong ( data.get ( Configuration.PROPOSED_DATA_AGE_KEY_PREFIX + detailLevelId ), 1 );
        if ( proposedDataAge < 0 )
        {
            proposedDataAge = 1;
        }
        long acceptedTimeDelta = Conversions.parseLong ( data.get ( Configuration.ACCEPTED_TIME_DELTA_KEY ), 1 );
        if ( acceptedTimeDelta < 0 )
        {
            acceptedTimeDelta = 1;
        }
        final DataType dataType = DataType.convertShortStringToDataType ( data.get ( Configuration.DATA_TYPE_KEY ) );
        if ( dataType == DataType.UNKNOWN )
        {
            throw new Exception ( "invalid data type specified in configuration" );
        }
        final StorageChannelMetaData metaData = new StorageChannelMetaData ( configurationId, calculationMethod, calculationMethodParameters, detailLevelId, startTime, endTime, proposedDataAge, acceptedTimeDelta, dataType );
        final FileBackEnd result = new FileBackEnd ( fileName, keepOpen );
        result.setLock ( backEndInformation.getLock () );
        if ( !new File ( fileName ).exists () )
        {
            logger.debug ( "creating file {}", fileName );
            result.create ( metaData );
        }
        if ( initialize )
        {
            result.initialize ( metaData );
        }
        return result;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerBase#getFragmentName(long, org.openscada.hsdb.calculation.CalculationMethod, long, long)
     */
    @Override
    protected String getFragmentName ( final long detailLevelId, final CalculationMethod calculationMethod, final long startTime, final long endTime )
    {
        return ( (FileBackEndFactory)getBackEndFactory () ).generateFileName ( new StorageChannelMetaData ( getConfiguration ().getId (), calculationMethod, new long[0], detailLevelId, startTime, endTime, 0, 0, DataType.LONG_VALUE ) );
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerBase#deleteBackEnd(org.openscada.hsdb.backend.BackEnd)
     */
    @Override
    protected void deleteBackEnd ( final BackEndFragmentInformation backEndInformation )
    {
        final String fileName = backEndInformation.getFragmentName ();
        final File file = new File ( fileName );
        if ( file.exists () )
        {
            logger.info ( "deleting file '{}'", fileName );
            if ( !file.delete () )
            {
                logger.warn ( "could not delete file '{}'. trying to delete file during next application shutdown", fileName );
                file.deleteOnExit ();
            }
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerBase#checkIsBackEndCorrupt(org.openscada.hsdb.backend.BackEndFragmentInformation)
     */
    @Override
    protected boolean checkIsBackEndCorrupt ( final BackEndFragmentInformation backEndInformation )
    {
        return !new File ( backEndInformation.getFragmentName () ).exists ();
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerBase#readyForRepair(org.openscada.hsdb.backend.BackEndFragmentInformation)
     */
    @Override
    protected boolean readyForRepair ( final BackEndFragmentInformation backEndInformation )
    {
        return !new File ( backEndInformation.getFragmentName () ).exists ();
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerBase#updateBackEndEmptyInformation(org.openscada.hsdb.backend.BackEndFragmentInformation)
     */
    @Override
    protected boolean updateBackEndEmptyInformation ( final BackEndFragmentInformation backEndInformation ) throws Exception
    {
        if ( backEndInformation.getIsCorrupt () )
        {
            return false;
        }
        if ( backEndInformation.getIsEmpty () == null )
        {
            final String fileName = backEndInformation.getFragmentName ();
            if ( !new File ( fileName ).exists () )
            {
                // the file does not exist but it should be there.
                // since no check can be performed, assume that the file contains invalid data
                backEndInformation.setIsEmpty ( false );
                backEndInformation.setSupposedEarliestValueTime ( null );
            }
            else
            {
                final FileBackEnd backEnd = new FileBackEnd ( backEndInformation.getFragmentName (), true );
                backEnd.setLock ( backEndInformation.getLock () );
                backEnd.initialize ( null );
                backEndInformation.setIsEmpty ( backEnd.isEmpty () );
                backEndInformation.setSupposedEarliestValueTime ( backEnd.getFirstEntryTime () );
                backEnd.deinitialize ();
            }
        }
        return backEndInformation.getIsEmpty ();
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManagerBase#updateBackEndEarliestTimeInformation(org.openscada.hsdb.backend.BackEndFragmentInformation)
     */
    @Override
    protected Long updateBackEndEarliestTimeInformation ( final BackEndFragmentInformation backEndInformation ) throws Exception
    {
        if ( backEndInformation.getIsCorrupt () || updateBackEndEmptyInformation ( backEndInformation ) )
        {
            return null;
        }
        return backEndInformation.getSupposedEarliestValueTime ();
    }
}
