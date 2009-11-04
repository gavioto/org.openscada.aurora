package org.openscada.hsdb.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.hsdb.StorageChannelMetaData;
import org.openscada.hsdb.datatypes.LongValue;
import org.openscada.hsdb.relict.RelictCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This backend implementation is able to handle multiple storage channel backend objects.
 * It is arranged that each such backend object is responsible for its own exclusive time span.
 * @author Ludwig Straub
 */
public class BackEndMultiplexer implements BackEnd, RelictCleaner
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( BackEndMultiplexer.class );

    /** Metadata of the storage channel. */
    private StorageChannelMetaData metaData;

    /** Manager that is used to create and access backend objects. */
    private final BackEndManager<? extends BackEnd> backEndManager;

    /** Flag indicating whether the instance has been initialized or not. */
    private volatile boolean initialized;

    /**
     * Constructor.
     * @param backEndManager manager that is used to create and access backend objects
     */
    public BackEndMultiplexer ( final BackEndManager<? extends BackEnd> backEndManager )
    {
        this.backEndManager = backEndManager;
        initialized = false;
    }

    /**
     * This method returns the manager that is used to create and access backend objects
     * @return manager that is used to create and access backend objects
     */
    public BackEndManager<? extends BackEnd> getBackEndManager ()
    {
        return this.backEndManager;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#initialize
     */
    public synchronized void initialize ( final StorageChannelMetaData storageChannelMetaData ) throws Exception
    {
        deinitialize ();
        initialized = true;
        metaData = new StorageChannelMetaData ( storageChannelMetaData );
    }

    /**
     * @see org.openscada.hsdb.relict.RelictCleaner#cleanupRelicts()
     */
    public void cleanupRelicts () throws Exception
    {
        logger.debug ( "deleting old data... start" );
        try
        {
            // assure that at least the last two values remain
            final long proposedDataAge = metaData.getProposedDataAge ();
            final long now = System.currentTimeMillis ();
            final LongValue[] firstValues = getLongValues ( now - 1, now );
            if ( ( firstValues == null ) || ( firstValues.length == 0 ) )
            {
                return;
            }
            final long before = firstValues[0].getTime ();
            final LongValue[] lastValues = getLongValues ( before - proposedDataAge - 1, before - proposedDataAge );
            if ( ( lastValues == null ) || ( lastValues.length == 0 ) )
            {
                return;
            }

            // delete old back ends
            backEndManager.deleteOldBackEnds ( metaData.getDetailLevelId (), metaData.getCalculationMethod (), lastValues[0].getTime () - 1 );
        }
        catch ( final Exception e )
        {
            logger.error ( "unable to retrieve latest value", e );
        }
        logger.debug ( "deleting old data... end" );
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#getMetaData
     */
    public StorageChannelMetaData getMetaData () throws Exception
    {
        assureInitialized ();
        return metaData;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#isTimeSpanConstant
     */
    public boolean isTimeSpanConstant ()
    {
        return false;
    }

    /**
     * @see org.openscada.hsdb.backend.BackEnd#deinitialize
     */
    public synchronized void deinitialize () throws Exception
    {
        initialized = false;
        metaData = null;
    }

    /**
     * This method assures that the instance is initialized.
     * @throws Exception if the instance is not initialized
     */
    private void assureInitialized () throws Exception
    {
        if ( !initialized )
        {
            final String message = String.format ( "back end (%s) is not properly initialized!", metaData );
            logger.error ( message );
            throw new Exception ( message );
        }
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#updateLong
     */
    public synchronized void updateLong ( final LongValue longValue ) throws Exception
    {
        assureInitialized ();
        if ( longValue != null )
        {
            try
            {
                final BackEnd backEnd = backEndManager.getBackEndForInsert ( this, this.metaData.getDetailLevelId (), this.metaData.getCalculationMethod (), longValue.getTime () );
                backEnd.updateLong ( longValue );
                backEnd.deinitialize ();
            }
            catch ( final Exception e )
            {
                logger.error ( String.format ( "backend (%s): could not write to sub backend (startTime: %s)", metaData, longValue.getTime () ), e );
                backEndManager.markBackEndAsCorrupt ( this.metaData.getDetailLevelId (), this.metaData.getCalculationMethod (), longValue.getTime () );
            }
        }
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#updateLongs
     */
    public synchronized void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        assureInitialized ();
        if ( longValues != null )
        {
            // assign all long values to the backend that is responsible for their processing
            final Map<Long, List<LongValue>> backends = new HashMap<Long, List<LongValue>> ();
            for ( final LongValue longValue : longValues )
            {
                long startTime = 0L;
                try
                {
                    final BackEnd backEnd = backEndManager.getBackEndForInsert ( this, this.metaData.getDetailLevelId (), this.metaData.getCalculationMethod (), longValue.getTime () );
                    final StorageChannelMetaData metaData = backEnd.getMetaData ();
                    startTime = metaData.getStartTime ();
                    backEnd.deinitialize ();
                }
                catch ( final Exception e )
                {
                    logger.error ( String.format ( "backend (%s): could not access sub backend (startTime: %s)", metaData, longValue.getTime () ), e );
                    backEndManager.markBackEndAsCorrupt ( this.metaData.getDetailLevelId (), this.metaData.getCalculationMethod (), longValue.getTime () );
                }
                List<LongValue> longValuesToProcess = backends.get ( startTime );
                if ( longValuesToProcess == null )
                {
                    longValuesToProcess = new ArrayList<LongValue> ();
                    backends.put ( startTime, longValuesToProcess );
                }
                longValuesToProcess.add ( longValue );
            }

            // process the ordered long values as bulk
            for ( final Map.Entry<Long, List<LongValue>> entry : backends.entrySet () )
            {
                try
                {
                    final BackEnd backEnd = backEndManager.getBackEndForInsert ( this, this.metaData.getDetailLevelId (), this.metaData.getCalculationMethod (), entry.getKey () );
                    backEnd.updateLongs ( entry.getValue ().toArray ( EMPTY_LONGVALUE_ARRAY ) );
                    backEnd.deinitialize ();
                }
                catch ( final Exception e )
                {
                    logger.error ( String.format ( "backend (%s): could not write to sub backend (startTime: %s)", metaData, entry.getKey () ), e );
                    backEndManager.markBackEndAsCorrupt ( this.metaData.getDetailLevelId (), this.metaData.getCalculationMethod (), entry.getKey () );
                }
            }
        }
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        // assure that the current state is valid
        assureInitialized ();

        // collect result data
        final List<LongValue> longValues = new LinkedList<LongValue> ();
        final BackEnd[] backEnds = backEndManager.getExistingBackEnds ( this, metaData.getDetailLevelId (), metaData.getCalculationMethod (), startTime, endTime );
        try
        {
            for ( final BackEnd backEnd : backEnds )
            {
                try
                {
                    final StorageChannelMetaData metaData = backEnd.getMetaData ();
                    final long metaDataStartTime = metaData.getStartTime ();
                    final long metaDataEndTime = metaData.getEndTime ();
                    try
                    {
                        if ( ( startTime <= metaDataEndTime ) && ( endTime > metaDataStartTime ) )
                        {
                            // process values that match the time span
                            longValues.addAll ( 0, Arrays.asList ( backEnd.getLongValues ( startTime, endTime ) ) );
                        }
                        if ( startTime >= metaDataEndTime )
                        {
                            final LongValue[] olderValues = backEnd.getLongValues ( startTime, endTime );
                            if ( olderValues.length > 0 )
                            {
                                longValues.addAll ( 0, Arrays.asList ( olderValues ) );
                            }
                        }
                        if ( !longValues.isEmpty () && longValues.get ( 0 ).getTime () <= startTime )
                        {
                            break;
                        }
                    }
                    catch ( final Exception e )
                    {
                        final String message = String.format ( "backend (%s): could not read from sub backend (startTime: %s; endTime: %s)", metaData, startTime, endTime );
                        if ( startTime < ( System.currentTimeMillis () - metaData.getProposedDataAge () ) )
                        {
                            logger.info ( message + " - backend is probably outdated", e );
                        }
                        else
                        {
                            logger.error ( message, e );
                        }
                        backEndManager.markBackEndAsCorrupt ( metaData.getDetailLevelId (), metaData.getCalculationMethod (), metaData.getStartTime () );
                        longValues.add ( 0, new LongValue ( metaDataStartTime, 0, 0, 0, 0 ) );
                        if ( metaDataStartTime <= startTime )
                        {
                            break;
                        }
                    }
                }
                catch ( final Exception e1 )
                {
                    final String message = String.format ( "backend (%s): could not access sub backend (startTime: %s; endTime: %s)", metaData, startTime, endTime );
                    if ( startTime < ( System.currentTimeMillis () - metaData.getProposedDataAge () ) )
                    {
                        logger.info ( message + " - backend is probably outdated", e1 );
                    }
                    else
                    {
                        logger.error ( message, e1 );
                    }
                }
            }
        }
        finally
        {
            for ( final BackEnd backEnd : backEnds )
            {
                try
                {
                    backEnd.deinitialize ();
                }
                catch ( final Exception e )
                {
                    logger.warn ( "could not deinitialize back end", e );
                }
            }
        }

        // return final result
        return longValues.toArray ( EMPTY_LONGVALUE_ARRAY );
    }

    /**
     * This instance does not support locking logic.
     * @see org.openscada.hsdb.backend.BackEnd#setLock(ReentrantReadWriteLock)
     */
    public void setLock ( final ReentrantReadWriteLock lock )
    {
    }

    /**
     * This instance does not support locking logic.
     * @see org.openscada.hsdb.backend.BackEnd#getLock()
     */
    public ReentrantReadWriteLock getLock ()
    {
        return null;
    }
}
