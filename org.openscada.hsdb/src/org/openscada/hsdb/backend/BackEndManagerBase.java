package org.openscada.hsdb.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.hsdb.CalculatingStorageChannel;
import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.ExtendedStorageChannelAdapter;
import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.calculation.CalculationLogicProvider;
import org.openscada.hsdb.calculation.CalculationLogicProviderFactoryImpl;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.concurrent.HsdbThreadFactory;
import org.openscada.hsdb.configuration.Configuration;
import org.openscada.hsdb.configuration.Conversions;
import org.openscada.hsdb.utils.HsdbHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides base functionality for managing back end objects.
 * @param <B> type of BackEnd implementation that will be managed by this class
 * @author Ludwig Straub
 */
public abstract class BackEndManagerBase<B extends BackEnd> implements BackEndManager<B>
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( BackEndManagerBase.class );

    /** Id of the repair thread. */
    private final String REPAIR_THREAD_ID = "hsdb.RepairThread";

    /** Configuration of the manager instance. */
    private final Configuration configuration;

    /** Factory that has been used to create this instance. */
    private final BackEndManagerFactory backEndManagerFactory;

    /** Factory that can be used to create new back end objects. */
    private final BackEndFactory backEndFactory;

    /** Empty back end array. */
    private final B[] emptyBackEndArray;

    /** Maximum compression level. */
    private final long maximumCompressionLevel;

    /** Set of all currently available calculation methods. */
    private final CalculationMethod[] calculationMethods;

    /** Factory that will be used when creating new calculation logic provider objects. */
    private final CalculationLogicProviderFactoryImpl calculationLogicProviderFactory;

    /** Currently available back end objects. */
    private final Map<Long, Map<CalculationMethod, List<BackEndFragmentInformation<B>>>> masterBackEnds;

    /** This list contains all back end objects that have been allocated to build up a storage channel tree. */
    private final List<BackEnd> storageChannelTreeBackEnds;

    /** This array contains the storage channels that are part of the tree. */
    private CalculatingStorageChannel[] storageChannels;

    /** Flag indicating whether corrupt files exist or not. */
    private boolean corruptFilesExist;

    /** Task that is used to perform repair jobs. */
    private final ExecutorService repairTask;

    /** Lock object to avoid usage of synchronize. */
    private final ReentrantReadWriteLock lock;

    /**
     * Constructor.
     * @param configuration configuration of the manager instance
     * @param backEndManagerFactory factory that has been used to create this instance
     * @param backEndFactory factory that can be used to create new back end objects
     * @param emptyBackEndArray empty array of back end objects
     */
    public BackEndManagerBase ( final Configuration configuration, final BackEndManagerFactory backEndManagerFactory, final BackEndFactory backEndFactory, final B[] emptyBackEndArray )
    {
        this.configuration = configuration;
        this.backEndManagerFactory = backEndManagerFactory;
        this.backEndFactory = backEndFactory;
        this.emptyBackEndArray = emptyBackEndArray;
        this.corruptFilesExist = false;
        this.storageChannels = null;
        this.masterBackEnds = new HashMap<Long, Map<CalculationMethod, List<BackEndFragmentInformation<B>>>> ();
        this.calculationLogicProviderFactory = new CalculationLogicProviderFactoryImpl ();
        this.calculationMethods = Conversions.getCalculationMethods ( configuration );
        this.storageChannelTreeBackEnds = new ArrayList<BackEnd> ();
        final Map<String, String> data = configuration.getData ();
        this.maximumCompressionLevel = data == null ? 0 : Conversions.parseLong ( data.get ( Configuration.MAX_COMPRESSION_LEVEL ), 0 );
        this.repairTask = Executors.newSingleThreadExecutor ( HsdbThreadFactory.createFactory ( REPAIR_THREAD_ID ) );
        lock = new ReentrantReadWriteLock ();
    }

    /**
     * This method updates the configuration using the current internal object structure as input.
     * After that the updated configuration is stored to the configuration file.
     */
    protected void flushConfiguration ()
    {
        // remove existing entries in the configuration that have to be created again
        final Map<String, String> data = configuration.getData ();
        final List<String> keysToRemove = new ArrayList<String> ();
        for ( final String key : data.keySet () )
        {
            if ( key.startsWith ( Configuration.MANAGER_KNOWN_FRAGMENT_CALCULATION_METHOD_PREFIX ) )
            {
                keysToRemove.add ( key );
                continue;
            }
            if ( key.startsWith ( Configuration.MANAGER_KNOWN_FRAGMENT_COMPRESSION_LEVEL_PREFIX ) )
            {
                keysToRemove.add ( key );
                continue;
            }
            if ( key.startsWith ( Configuration.MANAGER_KNOWN_FRAGMENT_CORRUPT_STATUS_PREFIX ) )
            {
                keysToRemove.add ( key );
                continue;
            }
            if ( key.startsWith ( Configuration.MANAGER_KNOWN_FRAGMENT_START_TIME_PREFIX ) )
            {
                keysToRemove.add ( key );
                continue;
            }
            if ( key.startsWith ( Configuration.MANAGER_KNOWN_FRAGMENT_END_TIME_PREFIX ) )
            {
                keysToRemove.add ( key );
                continue;
            }
            if ( key.startsWith ( Configuration.MANAGER_KNOWN_FRAGMENT_NAME_PREFIX ) )
            {
                keysToRemove.add ( key );
                continue;
            }
        }
        for ( final String key : keysToRemove )
        {
            data.remove ( key );
        }

        // build new configuration structure
        final List<BackEndFragmentInformation<B>> backEndFragmentInformations = new ArrayList<BackEndFragmentInformation<B>> ();
        for ( long i = 0; i <= maximumCompressionLevel; i++ )
        {
            if ( i == 0 )
            {
                backEndFragmentInformations.addAll ( getBackEndInformations ( i, CalculationMethod.NATIVE, Long.MIN_VALUE, Long.MAX_VALUE, true ) );
            }
            else
            {
                for ( final CalculationMethod calculationMethod : calculationMethods )
                {
                    backEndFragmentInformations.addAll ( getBackEndInformations ( i, calculationMethod, Long.MIN_VALUE, Long.MAX_VALUE, true ) );
                }
            }
        }

        // prepare new configuration
        final int size = backEndFragmentInformations.size ();
        data.put ( Configuration.MANAGER_KNOWN_FRAGMENTS_COUNT, "" + size );
        corruptFilesExist = false;
        for ( int i = 0; i < size; i++ )
        {
            final BackEndFragmentInformation<B> backendInformation = backEndFragmentInformations.get ( i );
            data.put ( Configuration.MANAGER_KNOWN_FRAGMENT_CALCULATION_METHOD_PREFIX + i, CalculationMethod.convertCalculationMethodToShortString ( backendInformation.getCalculationMethod () ) );
            data.put ( Configuration.MANAGER_KNOWN_FRAGMENT_COMPRESSION_LEVEL_PREFIX + i, "" + backendInformation.getDetailLevelId () );
            data.put ( Configuration.MANAGER_KNOWN_FRAGMENT_START_TIME_PREFIX + i, "" + backendInformation.getStartTime () );
            data.put ( Configuration.MANAGER_KNOWN_FRAGMENT_END_TIME_PREFIX + i, "" + backendInformation.getEndTime () );
            data.put ( Configuration.MANAGER_KNOWN_FRAGMENT_CORRUPT_STATUS_PREFIX + i, "" + backendInformation.getIsCorrupt () );
            data.put ( Configuration.MANAGER_KNOWN_FRAGMENT_NAME_PREFIX + i, backendInformation.getFragmentName () );
            corruptFilesExist |= ( backendInformation.getDetailLevelId () > 0 ) && backendInformation.getIsCorrupt ();
        }

        // save configuration
        backEndManagerFactory.save ( configuration );
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#initialize()
     */
    public void initialize () throws Exception
    {
        final Map<String, String> data = configuration.getData ();
        if ( data == null )
        {
            throw new Exception ( "configuration is not initialized!" );
        }
        final long fragmentCount = Conversions.parseLong ( data.get ( Configuration.MANAGER_KNOWN_FRAGMENTS_COUNT ), 0 );
        final String configurationId = configuration.getId ();
        boolean updateConfiguration = false;
        for ( long i = 0; i < fragmentCount; i++ )
        {
            try
            {
                final CalculationMethod calculationMethod = CalculationMethod.convertShortStringToCalculationMethod ( data.get ( Configuration.MANAGER_KNOWN_FRAGMENT_CALCULATION_METHOD_PREFIX + i ) );
                final long detailLevelId = Conversions.parseLong ( data.get ( Configuration.MANAGER_KNOWN_FRAGMENT_COMPRESSION_LEVEL_PREFIX + i ), 0 );
                final long startTime = Conversions.parseLong ( data.get ( Configuration.MANAGER_KNOWN_FRAGMENT_START_TIME_PREFIX + i ), 0 );
                final long endTime = Conversions.parseLong ( data.get ( Configuration.MANAGER_KNOWN_FRAGMENT_END_TIME_PREFIX + i ), 0 );
                final boolean isCorrupt = Boolean.parseBoolean ( data.get ( Configuration.MANAGER_KNOWN_FRAGMENT_CORRUPT_STATUS_PREFIX + i ) );
                final String fragmentName = data.get ( Configuration.MANAGER_KNOWN_FRAGMENT_NAME_PREFIX + i );
                if ( calculationMethod == CalculationMethod.UNKNOWN )
                {
                    throw new Exception ( String.format ( "invalid calculation method specified for file with index %s", i ) );
                }
                if ( detailLevelId < 0 )
                {
                    throw new Exception ( String.format ( "invalid compression level specified for file with index %s", i ) );
                }
                if ( startTime >= endTime )
                {
                    throw new Exception ( String.format ( "invalid start/end time specified for file with index %s", i ) );
                }
                if ( ( fragmentName == null ) || ( fragmentName.trim ().length () == 0 ) )
                {
                    throw new Exception ( String.format ( "invalid file name specified for file with index %s", i ) );
                }
                final BackEndFragmentInformation<B> backEndFragmentInformation = new BackEndFragmentInformation<B> ();
                backEndFragmentInformation.setConfigurationId ( configurationId );
                backEndFragmentInformation.setLock ( new ReentrantReadWriteLock () );
                backEndFragmentInformation.setCalculationMethod ( calculationMethod );
                backEndFragmentInformation.setDetailLevelId ( detailLevelId );
                backEndFragmentInformation.setStartTime ( startTime );
                backEndFragmentInformation.setEndTime ( endTime );
                backEndFragmentInformation.setFragmentName ( fragmentName );
                final boolean mergedCorruptFlag = isCorrupt || checkIsBackEndCorrupt ( backEndFragmentInformation );
                corruptFilesExist |= ( detailLevelId > 0 ) && mergedCorruptFlag;
                backEndFragmentInformation.setIsCorrupt ( mergedCorruptFlag );
                addBackEndFragmentInformation ( backEndFragmentInformation, i == fragmentCount - 1 );
                if ( !isCorrupt && mergedCorruptFlag )
                {
                    logger.error ( String.format ( "back end fragment '%s' for configuration '%s' has been marked as corrupt", fragmentName, configurationId ) );
                    updateConfiguration = true;
                }
            }
            catch ( final Exception e )
            {
                logger.error ( String.format ( "invalid configuration set detected - will be ignored (%s)", configurationId ) );
            }
        }
        if ( updateConfiguration )
        {
            flushConfiguration ();
        }
    }

    /**
     * This method adds the passed object to the internal data structure.
     * @param backEndFragmentInformation object that has to be added
     * @param sort flag indicating whether the sort order should be maintained. This flag may only be set to false, if another call of the method will be performed afterwards and then the flag is set to true
     */
    protected void addBackEndFragmentInformation ( final BackEndFragmentInformation<B> backEndFragmentInformation, final boolean sort )
    {
        final long detailLevelId = backEndFragmentInformation.getDetailLevelId ();
        Map<CalculationMethod, List<BackEndFragmentInformation<B>>> map = masterBackEnds.get ( detailLevelId );
        if ( map == null )
        {
            map = new HashMap<CalculationMethod, List<BackEndFragmentInformation<B>>> ();
            masterBackEnds.put ( detailLevelId, map );
        }
        final CalculationMethod calculationMethod = backEndFragmentInformation.getCalculationMethod ();
        List<BackEndFragmentInformation<B>> list = map.get ( detailLevelId == 0 ? CalculationMethod.NATIVE : calculationMethod );
        if ( list == null )
        {
            list = new LinkedList<BackEndFragmentInformation<B>> ();
            map.put ( calculationMethod, list );
        }
        boolean add = true;
        for ( final BackEndFragmentInformation<B> existingBackEndFragmentInformation : list )
        {
            if ( existingBackEndFragmentInformation.getFragmentName ().equals ( backEndFragmentInformation.getFragmentName () ) )
            {
                backEndFragmentInformation.setLock ( existingBackEndFragmentInformation.getLock () );
                existingBackEndFragmentInformation.setBackEndFragment ( backEndFragmentInformation.getBackEndFragment () );
                existingBackEndFragmentInformation.setCalculationMethod ( backEndFragmentInformation.getCalculationMethod () );
                existingBackEndFragmentInformation.setConfigurationId ( backEndFragmentInformation.getConfigurationId () );
                existingBackEndFragmentInformation.setDetailLevelId ( backEndFragmentInformation.getDetailLevelId () );
                existingBackEndFragmentInformation.setStartTime ( backEndFragmentInformation.getStartTime () );
                existingBackEndFragmentInformation.setEndTime ( backEndFragmentInformation.getEndTime () );
                existingBackEndFragmentInformation.setIsCorrupt ( existingBackEndFragmentInformation.getIsCorrupt () || backEndFragmentInformation.getIsCorrupt () );
                add = false;
                break;
            }
        }
        if ( add )
        {
            list.add ( backEndFragmentInformation );
            if ( sort )
            {
                Collections.sort ( list );
            }
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#getConfiguration()
     */
    public Configuration getConfiguration ()
    {
        return new Configuration ( configuration );
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#getBackEndManagerFactory()
     */
    public BackEndManagerFactory getBackEndManagerFactory ()
    {
        return backEndManagerFactory;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#getBackEndFactory()
     */
    public BackEndFactory getBackEndFactory ()
    {
        return backEndFactory;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#getMaximumCompressionLevel()
     */
    public long getMaximumCompressionLevel ()
    {
        return maximumCompressionLevel;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#getCalculationMethods()
     */
    public CalculationMethod[] getCalculationMethods ()
    {
        return calculationMethods.clone ();
    }

    /**
     * This method deinitializes all passed back end objects.
     * @param backEnds back end objects that have to be deinitialized
     */
    protected void deinitializeBackEnds ( final List<? extends BackEnd> backEnds )
    {
        if ( backEnds != null )
        {
            for ( final BackEnd backEnd : backEnds )
            {
                try
                {
                    backEnd.deinitialize ();
                }
                catch ( final Exception e1 )
                {
                    StorageChannelMetaData metaData = null;
                    try
                    {
                        metaData = backEnd.getMetaData ();
                    }
                    catch ( final Exception e2 )
                    {
                    }
                    logger.error ( String.format ( "unable to delete/deinitialize back end for meta data '%s'", metaData ), e1 );
                }
            }
        }
    }

    /**
     * This method builds a tree structure where all relevant back end objects for the different calculation methods and detail levels are linked with each other.
     * In order to process a single value within all back end objects, it only is required to pass that value to the root channel of the tree.
     * That root channel will be returned as result of the method.
     * @return root channel of the built tree
     */
    public CalculatingStorageChannel buildStorageChannelTree ()
    {
        lock.writeLock ().lock ();
        try
        {
            // optimize calculation
            if ( ( storageChannels != null ) && ( storageChannels.length > 0 ) )
            {
                return storageChannels[0];
            }

            // create back end objects 
            Exception exception = null;
            try
            {
                // create back end objects
                final StorageChannelMetaData[] metaDatas = Conversions.convertConfigurationToMetaDatas ( configuration );
                if ( ( metaDatas == null ) || ( metaDatas.length == 0 ) )
                {
                    final String message = String.format ( "invalid configuration (%s)", configuration.getId () );
                    logger.error ( message );
                    throw new Exception ( message );
                }
                for ( final StorageChannelMetaData metaData : metaDatas )
                {
                    final BackEndMultiplexer backEnd = new BackEndMultiplexer ( this );
                    storageChannelTreeBackEnds.add ( backEnd );
                    backEnd.initialize ( metaData );
                }

                // create hierarchical storage channel structure
                storageChannels = new CalculatingStorageChannel[storageChannelTreeBackEnds.size ()];
                for ( int i = 0; i < storageChannelTreeBackEnds.size (); i++ )
                {
                    final BackEnd backEnd = storageChannelTreeBackEnds.get ( i );
                    final CalculationMethod calculationMethod = backEnd.getMetaData ().getCalculationMethod ();
                    int superBackEndIndex = -1;
                    for ( int j = i - 1; j >= 0; j-- )
                    {
                        final BackEnd superBackEndCandidate = storageChannelTreeBackEnds.get ( j );
                        final CalculationMethod superCalculationMethod = superBackEndCandidate.getMetaData ().getCalculationMethod ();
                        if ( ( superCalculationMethod == calculationMethod ) || ( superCalculationMethod == CalculationMethod.NATIVE ) )
                        {
                            superBackEndIndex = j;
                            break;
                        }
                    }
                    storageChannels[i] = new CalculatingStorageChannel ( new ExtendedStorageChannelAdapter ( backEnd ), superBackEndIndex >= 0 ? storageChannels[superBackEndIndex] : null, calculationLogicProviderFactory.getCalculationLogicProvider ( backEnd.getMetaData () ) );
                    if ( superBackEndIndex >= 0 )
                    {
                        storageChannels[superBackEndIndex].registerStorageChannel ( storageChannels[i] );
                    }
                }
                return storageChannels[0];
            }
            catch ( final Exception e )
            {
                exception = e;
            }
            final String message = String.format ( "could not create all back ends required for configuration '%s'", configuration.getId () );
            logger.error ( message, exception );
            releaseStorageChannelTree ();
            throw new RuntimeException ( message, exception );
        }
        finally
        {
            lock.writeLock ().unlock ();
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#releaseStorageChannelTree()
     */
    public void releaseStorageChannelTree ()
    {
        lock.writeLock ().lock ();
        try
        {
            storageChannelTreeBackEnds.clear ();
            storageChannels = null;
        }
        finally
        {
            lock.writeLock ().unlock ();
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#buildStorageChannelStructure()
     */
    public Map<Long, Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>>> buildStorageChannelStructure ()
    {
        lock.readLock ().lock ();
        try
        {
            // build the storage channel tree structure if it does not yet exist
            final boolean storageChannelTreeExists = storageChannels != null;
            if ( !storageChannelTreeExists )
            {
                buildStorageChannelTree ();
            }

            // build structure
            final Map<Long, Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>>> resultMap = new HashMap<Long, Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>>> ();
            try
            {
                for ( final CalculatingStorageChannel calculatingStorageChannel : storageChannels )
                {
                    // collect data for map entry
                    final StorageChannelMetaData metaData = new StorageChannelMetaData ( calculatingStorageChannel.getMetaData () );
                    final long detailLevelId = metaData.getDetailLevelId ();
                    final CalculationMethod calculationMethod = metaData.getCalculationMethod ();
                    final CalculationLogicProvider calculationLogicProvider = calculationLogicProviderFactory.getCalculationLogicProvider ( metaData );
                    final BackEndMultiplexer backEnd = new BackEndMultiplexer ( this );
                    backEnd.initialize ( metaData );
                    final ExtendedStorageChannel storageChannel = new ExtendedStorageChannelAdapter ( backEnd );

                    // create map entry
                    Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>> map = resultMap.get ( detailLevelId );
                    if ( map == null )
                    {
                        map = new HashMap<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>> ();
                        resultMap.put ( detailLevelId, map );
                    }
                    Map<ExtendedStorageChannel, CalculationLogicProvider> map2 = map.get ( calculationMethod );
                    if ( map2 == null )
                    {
                        map2 = new HashMap<ExtendedStorageChannel, CalculationLogicProvider> ();
                        map.put ( calculationMethod, map2 );
                    }
                    map2.put ( storageChannel, calculationLogicProvider );
                }
            }
            catch ( final Exception e )
            {
                logger.error ( "problem while building the storage channel structure", e );
            }

            // release the storage channel tree structure if did not exist before
            if ( !storageChannelTreeExists )
            {
                releaseStorageChannelTree ();
            }
            return resultMap;
        }
        finally
        {
            lock.readLock ().unlock ();
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#delete()
     */
    public void delete ()
    {
        lock.writeLock ().lock ();
        try
        {
            backEndManagerFactory.delete ( configuration );
        }
        finally
        {
            lock.writeLock ().unlock ();
        }
    }

    /**
     * This method returns the currently available back end information objects matching the specified criteria.
     * The newest back end information objects will be returned first.
     * If the oldest back end object fitting the the specified time span has to entries, then the next oldest object with entries will be retrieved.
     * @param detailLevelId detail level id for which the back end information objects have to be retrieved
     * @param calculationMethod calculation method for which the back end information objects have to be retrieved
     * @param startTime start time for which the back end information objects have to be retrieved
     * @param endTime end time for which the back end information objects have to be retrieved
     * @param addIfEmpty flag indicating whether back end objects should also be returned if they contain no data
     * @return currently available back end information objects matching the specified criteria
     */
    protected List<BackEndFragmentInformation<B>> getBackEndInformations ( final long detailLevelId, final CalculationMethod calculationMethod, final long startTime, final long endTime, final boolean addIfEmpty )
    {
        final List<BackEndFragmentInformation<B>> result = new ArrayList<BackEndFragmentInformation<B>> ();
        final Map<CalculationMethod, List<BackEndFragmentInformation<B>>> map = masterBackEnds.get ( detailLevelId );
        if ( map == null )
        {
            return result;
        }
        final List<BackEndFragmentInformation<B>> list = map.get ( detailLevelId == 0 ? CalculationMethod.NATIVE : calculationMethod );
        if ( list == null )
        {
            return result;
        }
        for ( final BackEndFragmentInformation<B> backEndFragmentInformation : list )
        {
            final long metaDataStartTime = backEndFragmentInformation.getStartTime ();
            final long metaDataEndTime = backEndFragmentInformation.getEndTime ();
            if ( ( startTime <= metaDataEndTime ) && ( endTime > metaDataStartTime ) )
            {
                try
                {
                    if ( addIfEmpty || backEndFragmentInformation.getIsCorrupt () || !isBackEndEmpty ( backEndFragmentInformation ) )
                    {
                        result.add ( backEndFragmentInformation );
                    }
                }
                catch ( final Exception e )
                {
                    logger.error ( String.format ( "marking back end fragment (%s) of configuration with id '%s' as corrupt", backEndFragmentInformation.getFragmentName (), configuration.getId () ) );
                    backEndFragmentInformation.setIsCorrupt ( true );
                    flushConfiguration ();
                }
            }
            if ( startTime >= metaDataEndTime )
            {
                try
                {
                    if ( backEndFragmentInformation.getIsCorrupt () || !isBackEndEmpty ( backEndFragmentInformation ) )
                    {
                        result.add ( backEndFragmentInformation );
                        break;
                    }
                }
                catch ( final Exception e )
                {
                    logger.error ( String.format ( "marking back end fragment (%s) of configuration with id '%s' as corrupt", backEndFragmentInformation.getFragmentName (), configuration.getId () ) );
                    backEndFragmentInformation.setIsCorrupt ( true );
                    flushConfiguration ();
                }
            }
        }
        return result;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#getBackEndForInsert(java.lang.Object, long, org.openscada.hsdb.calculation.CalculationMethod, long)
     */
    public B getBackEndForInsert ( final Object user, final long detailLevelId, final CalculationMethod calculationMethod, final long timestamp ) throws Exception
    {
        lock.writeLock ().lock ();
        try
        {
            final List<BackEndFragmentInformation<B>> backEndInformations = getBackEndInformations ( detailLevelId, calculationMethod, timestamp, timestamp + 1, true );
            BackEndFragmentInformation<B> result;
            if ( backEndInformations.isEmpty () )
            {
                // create new back end object
                result = addNewBackEndObjects ( detailLevelId, calculationMethod, timestamp, timestamp );
            }
            else
            {
                final BackEndFragmentInformation<B> existingBackEndInformation = backEndInformations.get ( 0 );
                if ( ( existingBackEndInformation.getStartTime () <= timestamp ) && ( existingBackEndInformation.getEndTime () > timestamp ) )
                {
                    // all is good. the current back end object can be used
                    result = existingBackEndInformation;
                }
                else
                {
                    // create new back end object
                    result = addNewBackEndObjects ( detailLevelId, calculationMethod, existingBackEndInformation.getEndTime (), timestamp );
                }
            }
            if ( result.getBackEndFragment () == null )
            {
                result.setBackEndFragment ( createBackEnd ( result, false ) );
            }
            final B backEnd = createBackEnd ( result, true );
            return backEnd;
        }
        finally
        {
            lock.writeLock ().unlock ();
        }
    }

    /**
     * This method adds new back end objects to the internal storage.
     * If there is a time gap between the last object and the new one, the gap will be filled by an additional element.
     * @param detailLevelId detail level of the stored data
     * @param calculationMethod method that is used to calculate the data that is stored in the channel
     * @param timestamp time stamp for which the object has to be created
     * @return added back end object
     * @throws Exception in case of problems
     */
    private BackEndFragmentInformation<B> addNewBackEndObjects ( final long detailLevelId, final CalculationMethod calculationMethod, final long startTime, final long time ) throws Exception
    {
        final Map<String, String> data = configuration.getData ();
        long timespan = Conversions.decodeTimeSpan ( data.get ( Configuration.MANAGER_FRAGMENT_TIMESPAN_PER_LEVEL_PREFIX + detailLevelId ) );
        if ( timespan < 1 )
        {
            timespan = 1;
        }
        final long fragmentStart = Conversions.getFragmentStartTime ( time, timespan );
        if ( startTime < fragmentStart )
        {
            final BackEndFragmentInformation<B> backEndFragmentInformation = new BackEndFragmentInformation<B> ();
            backEndFragmentInformation.setCalculationMethod ( calculationMethod );
            backEndFragmentInformation.setConfigurationId ( configuration.getId () );
            backEndFragmentInformation.setLock ( new ReentrantReadWriteLock () );
            backEndFragmentInformation.setDetailLevelId ( detailLevelId );
            backEndFragmentInformation.setIsCorrupt ( false );
            backEndFragmentInformation.setFragmentName ( getFragmentName ( detailLevelId, calculationMethod, startTime, fragmentStart ) );
            backEndFragmentInformation.setStartTime ( startTime );
            backEndFragmentInformation.setEndTime ( fragmentStart );
            createBackEnd ( backEndFragmentInformation, false );
            addBackEndFragmentInformation ( backEndFragmentInformation, false );
        }
        final BackEndFragmentInformation<B> backEndFragmentInformation = new BackEndFragmentInformation<B> ();
        backEndFragmentInformation.setCalculationMethod ( calculationMethod );
        backEndFragmentInformation.setConfigurationId ( configuration.getId () );
        backEndFragmentInformation.setLock ( new ReentrantReadWriteLock () );
        backEndFragmentInformation.setDetailLevelId ( detailLevelId );
        backEndFragmentInformation.setIsCorrupt ( false );
        backEndFragmentInformation.setFragmentName ( getFragmentName ( detailLevelId, calculationMethod, fragmentStart, fragmentStart + timespan ) );
        backEndFragmentInformation.setStartTime ( fragmentStart );
        backEndFragmentInformation.setEndTime ( fragmentStart + timespan );
        createBackEnd ( backEndFragmentInformation, false );
        addBackEndFragmentInformation ( backEndFragmentInformation, true );
        flushConfiguration ();
        return backEndFragmentInformation;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#getExistingBackEnds(java.lang.Object, long, org.openscada.hsdb.calculation.CalculationMethod, long, long)
     */
    public B[] getExistingBackEnds ( final Object user, final long detailLevelId, final CalculationMethod calculationMethod, final long startTime, final long endTime ) throws Exception
    {
        lock.readLock ().lock ();
        try
        {
            final List<BackEndFragmentInformation<B>> backEndInformations = getBackEndInformations ( detailLevelId, calculationMethod, startTime, endTime, false );
            final List<B> result = new ArrayList<B> ();
            for ( final BackEndFragmentInformation<B> backEndInformation : backEndInformations )
            {
                if ( backEndInformation.getBackEndFragment () == null )
                {
                    backEndInformation.setBackEndFragment ( createBackEnd ( backEndInformation, false ) );
                }
                result.add ( createBackEnd ( backEndInformation, true ) );
            }
            return result.toArray ( emptyBackEndArray );
        }
        finally
        {
            lock.readLock ().unlock ();
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#deleteOldBackEnds(long, org.openscada.hsdb.calculation.CalculationMethod, long)
     */
    public void deleteOldBackEnds ( final long detailLevelId, final CalculationMethod calculationMethod, final long endTime )
    {
        lock.writeLock ().lock ();
        try
        {
            final List<BackEndFragmentInformation<B>> backEndFragmentInformationToDelete = new ArrayList<BackEndFragmentInformation<B>> ();
            final Map<CalculationMethod, List<BackEndFragmentInformation<B>>> map = masterBackEnds.get ( detailLevelId );
            if ( map == null )
            {
                return;
            }
            final List<BackEndFragmentInformation<B>> list = map.get ( detailLevelId == 0 ? CalculationMethod.NATIVE : calculationMethod );
            if ( list == null )
            {
                return;
            }
            for ( final BackEndFragmentInformation<B> backEndFragmentInformation : list )
            {
                if ( backEndFragmentInformation.getEndTime () <= endTime )
                {
                    final B backEnd = backEndFragmentInformation.getBackEndFragment ();
                    if ( backEnd != null )
                    {
                        try
                        {
                            backEnd.deinitialize ();
                        }
                        catch ( final Exception e )
                        {
                            final String message = String.format ( "could not deinitialize back end for configuration '%s'", configuration.getId () );
                            logger.error ( message, e );
                        }
                    }
                    deleteBackEnd ( backEndFragmentInformation );
                    backEndFragmentInformationToDelete.add ( backEndFragmentInformation );
                }
            }
            if ( !backEndFragmentInformationToDelete.isEmpty () )
            {
                list.removeAll ( backEndFragmentInformationToDelete );
                flushConfiguration ();
            }
        }
        finally
        {
            lock.writeLock ().unlock ();
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#markBackEndAsCorrupt(long, org.openscada.hsdb.calculation.CalculationMethod, long)
     */
    public void markBackEndAsCorrupt ( final long detailLevelId, final CalculationMethod calculationMethod, final long timestamp )
    {
        lock.readLock ().lock ();
        try
        {
            final List<BackEndFragmentInformation<B>> backEndInformations = getBackEndInformations ( detailLevelId, calculationMethod, timestamp, timestamp + 1, true );
            boolean statusChanged = false;
            for ( final BackEndFragmentInformation<B> backEndInformation : backEndInformations )
            {
                if ( !backEndInformation.getIsCorrupt () )
                {
                    logger.error ( String.format ( "marking back end fragment (%s) of configuration with id '%s' as corrupt", backEndInformation.getFragmentName (), configuration.getId () ) );
                    backEndInformation.setIsCorrupt ( true );
                    statusChanged = true;
                }
            }
            if ( statusChanged )
            {
                flushConfiguration ();
            }
        }
        finally
        {
            lock.readLock ().unlock ();
        }
    }

    /**
     * @see org.openscada.hsdb.backend.BackEndManager#repairBackEndFragmentsIfRequired(AbortNotificator)
     */
    public boolean repairBackEndFragmentsIfRequired ( final AbortNotificator abortNotificator )
    {
        if ( corruptFilesExist )
        {
            // collect information of all corrupt back end object
            logger.info ( "collecting data required for repair action..." );
            final long now = System.currentTimeMillis ();
            final List<BackEndFragmentInformation<B>> corruptBackEndFragmentInformations = new ArrayList<BackEndFragmentInformation<B>> ();
            for ( long i = 1; i <= maximumCompressionLevel; i++ )
            {
                for ( final CalculationMethod calculationMethod : calculationMethods )
                {
                    final List<BackEndFragmentInformation<B>> backEndInformations = getBackEndInformations ( i, calculationMethod, Long.MIN_VALUE, Long.MAX_VALUE, false );
                    for ( final BackEndFragmentInformation<B> backEndInformation : backEndInformations )
                    {
                        if ( backEndInformation.getIsCorrupt () )
                        {
                            if ( readyForRepair ( backEndInformation ) )
                            {
                                final B backEnd = backEndInformation.getBackEndFragment ();
                                if ( backEnd != null )
                                {
                                    backEndInformation.setBackEndFragment ( null );
                                    try
                                    {
                                        backEnd.deinitialize ();
                                    }
                                    catch ( final Exception e )
                                    {
                                        logger.error ( String.format ( "unable to deinitialize back end fragment '%s' for configuration with id '%s'", backEndInformation.getFragmentName (), configuration.getId () ) );
                                        continue;
                                    }
                                }
                                corruptBackEndFragmentInformations.add ( backEndInformation );
                            }
                            else
                            {
                                logger.error ( String.format ( "corrupt back end fragment '%s' for configuration '%s' is not ready for being repaired!", backEndInformation.getFragmentName (), configuration.getId () ) );
                            }
                        }
                    }
                }
            }
            logger.info ( String.format ( "[%s] corrupt back end fragments scheduled to be repaired...", corruptBackEndFragmentInformations.size () ) );
            if ( !corruptBackEndFragmentInformations.isEmpty () )
            {
                buildStorageChannelTree ();
                final CalculatingStorageChannel[] storageChannels = this.storageChannels;
                logger.info ( String.format ( "start processing [%s] corrupt back end fragments...", corruptBackEndFragmentInformations.size () ) );
                for ( final BackEndFragmentInformation<B> backEndInformation : corruptBackEndFragmentInformations )
                {
                    // abort if abort is requested
                    if ( ( abortNotificator != null ) && abortNotificator.getAbort () )
                    {
                        break;
                    }

                    // process this back end fragment
                    final long detailLevelId = backEndInformation.getDetailLevelId ();
                    final CalculationMethod calculationMethod = backEndInformation.getCalculationMethod ();
                    final long startTime = backEndInformation.getStartTime ();
                    final long endTime = backEndInformation.getEndTime ();

                    // check whether the corrupt fragment is the latest of its detail level and calculation method or whether it is needed by other such corrupt fragments for input
                    final List<BackEndFragmentInformation<B>> existingBackEndInformations = getBackEndInformations ( detailLevelId, calculationMethod, Long.MAX_VALUE - 1, Long.MAX_VALUE, false );
                    final boolean urgentlyNeededBackEndFragment = existingBackEndInformations.isEmpty () || ( existingBackEndInformations.get ( 0 ).getStartTime () == startTime ) || getIsDependingRequiredBackEndFragment ( backEndInformation );

                    // search for the storage channel that is responsible for the corrupt back end fragment
                    try
                    {
                        for ( final CalculatingStorageChannel outputCalculatingStorageChannel : storageChannels )
                        {
                            final StorageChannelMetaData metaData = outputCalculatingStorageChannel.getMetaData ();
                            if ( ( metaData.getDetailLevelId () == detailLevelId ) && ( metaData.getCalculationMethod () == calculationMethod ) )
                            {
                                // process the data for the corrupt time span
                                final CalculatingStorageChannel inputCalculatingStorageChannel = (CalculatingStorageChannel)outputCalculatingStorageChannel.getInputStorageChannel ();
                                final CalculationLogicProvider outputCalculationLogicProvider = outputCalculatingStorageChannel.getCalculationLogicProvider ();
                                final CalculationLogicProvider inputCalculationLogicProvider = inputCalculatingStorageChannel.getCalculationLogicProvider ();
                                final ExtendedStorageChannel outputChannel = outputCalculatingStorageChannel.getBaseStorageChannel ();
                                final ExtendedStorageChannel inputChannel = inputCalculatingStorageChannel.getBaseStorageChannel ();
                                if ( urgentlyNeededBackEndFragment )
                                {
                                    if ( ( abortNotificator == null ) || !abortNotificator.getAbort () )
                                    {
                                        logger.info ( String.format ( "processing [%s]...", backEndInformation.getFragmentName () ) );
                                        deleteBackEnd ( backEndInformation );
                                        HsdbHelper.processData ( inputChannel, outputChannel, inputCalculationLogicProvider, outputCalculationLogicProvider, startTime, Math.min ( now, endTime ) );
                                        backEndInformation.setIsCorrupt ( false );
                                    }
                                }
                                else
                                {
                                    repairTask.submit ( new Runnable () {
                                        public void run ()
                                        {
                                            try
                                            {
                                                if ( ( abortNotificator == null ) || !abortNotificator.getAbort () )
                                                {
                                                    logger.info ( String.format ( "processing [%s]...", backEndInformation.getFragmentName () ) );
                                                    deleteBackEnd ( backEndInformation );
                                                    HsdbHelper.processData ( inputChannel, outputChannel, inputCalculationLogicProvider, outputCalculationLogicProvider, startTime, Math.min ( now, endTime ) );
                                                    backEndInformation.setIsCorrupt ( false );
                                                    flushConfiguration ();
                                                }
                                            }
                                            catch ( final Exception e )
                                            {
                                                final String message = String.format ( "problem while repairing corrupt back end fragment (%s) for configuration '%s'", backEndInformation.getFragmentName (), configuration.getId () );
                                                logger.error ( message, e );
                                            }
                                        }
                                    } );
                                }
                                break;
                            }
                        }
                    }
                    catch ( final Exception e )
                    {
                        logger.error ( String.format ( "unable to access meta data for storage channel of configuration '%s'", configuration.getId () ) );
                    }
                }
                flushConfiguration ();
                logger.info ( "end processing corrupt back end fragments!" );
            }
            releaseStorageChannelTree ();
        }
        return !corruptFilesExist;
    }

    /**
     * This method checks whether the passed corrupt back end fragment should be repaired at once or whether the repair action can be performed later without disadvantage.
     * The repair action has to be performed at once if at least one back end fragment of the next highest compression level and a possibly affected time span is also corrupt.
     * @param backEndInformation information object of the back end fragment that has to be checked
     * @return true, if the back end fragment specified by the passed information object has to be repaired at once, otherwise false
     */
    private boolean getIsDependingRequiredBackEndFragment ( final BackEndFragmentInformation<B> backEndInformation )
    {
        if ( backEndInformation == null )
        {
            return false;
        }
        final List<BackEndFragmentInformation<B>> backEndInformations = getBackEndInformations ( backEndInformation.getDetailLevelId () + 1, backEndInformation.getCalculationMethod (), backEndInformation.getStartTime (), Long.MAX_VALUE, false );
        for ( final BackEndFragmentInformation<B> higherBackEndInformation : backEndInformations )
        {
            if ( higherBackEndInformation.getIsCorrupt () )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * This method returns the fragment name that suits the passed criteria.
     * @param detailLevelId detail level of the fragment
     * @param calculationMethod calculation method of the fragment
     * @param startTime start time of the fragment
     * @param endTime end time of the fragment
     * @return fragment name that suits the passed criteria
     */
    protected abstract String getFragmentName ( final long detailLevelId, final CalculationMethod calculationMethod, final long startTime, final long endTime );

    /**
     * This method creates a back end object according to the data of the passed object and assigns the created object to the passed object.
     * If the passed object already contains a back end object, then only the created new backend object will be returned.
     * @param backEndInformation object containing meta information that has to be used when creating the back end object
     * @param initialize flag indicating whether the back end object that has to be created should also be initialized or not
     * @return created back end object
     * @throws Exception in case of problems
     */
    protected abstract B createBackEnd ( final BackEndFragmentInformation<B> backEndInformation, final boolean initialize ) throws Exception;

    /**
     * This method checks whether the passed back end fragment is corrupt or not.
     * @param backEndInformation object to be checked
     * @return true, if the passed back end fragment is corrupt, otherwise false
     */
    protected abstract boolean checkIsBackEndCorrupt ( final BackEndFragmentInformation<B> backEndInformation );

    /**
     * This method returns whether the passed back end fragment is ready for being repaired.
     * @param backEndInformation object that has to be checked
     * @return true, if the passed back end fragment is ready for being repaired, otherwise false
     */
    protected abstract boolean readyForRepair ( final BackEndFragmentInformation<B> backEndInformation );

    /**
     * This method returns whether the passed back end fragment contains data or not
     * @param backEndInformation object that has to be checked
     * @return true, if the passed fragment does not contain any data, otherwise false
     * @throws Exception if back end fragment is corrupt
     */
    protected abstract boolean isBackEndEmpty ( final BackEndFragmentInformation<B> backEndInformation ) throws Exception;

    /**
     * This method deletes the passed back end fragment.
     * @param backEndInformation object containing meta information that has to be used when deleting the back end object
     */
    protected abstract void deleteBackEnd ( final BackEndFragmentInformation<B> backEndInformation );
}
