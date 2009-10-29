package org.openscada.hsdb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.openscada.hsdb.calculation.CalculationLogicProvider;
import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.openscada.hsdb.utils.ValueArrayNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as base implementation for storage channel implementations providing calculation methods.
 * @author Ludwig Straub
 */
public class CalculatingStorageChannel extends SimpleStorageChannelManager
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( CalculatingStorageChannel.class );

    /** Storage channel that is used as main channel when writing results of calculations. */
    private final ExtendedStorageChannel baseStorageChannel;

    /** Storage channel that is used to request data if data is missing for instance after startup. */
    private final ExtendedStorageChannel inputStorageChannel;

    /** Logic provider for calculation of values for storage channel. */
    private final CalculationLogicProvider calculationLogicProvider;

    /** The start time of the latest processed time span. */
    private long latestProcessedTime;

    /** The latest value in the storage channel. */
    private BaseValue lastValue;

    /** Time span of a block. */
    private final long blockTimeSpan;

    /**
     * Fully initializing constructor.
     * @param baseStorageChannel storage channel that is used as main channel when writing results of calculations
     * @param inputStorageChannel storage channel that is used to request data if data is missing for instance after startup
     * @param calculationLogicProvider logic provider for calculation of values for storage channel
     */
    public CalculatingStorageChannel ( final ExtendedStorageChannel baseStorageChannel, final ExtendedStorageChannel inputStorageChannel, final CalculationLogicProvider calculationLogicProvider )
    {
        // initialize data
        this.baseStorageChannel = baseStorageChannel;
        this.inputStorageChannel = inputStorageChannel;
        this.calculationLogicProvider = calculationLogicProvider;
        blockTimeSpan = calculationLogicProvider.getRequiredTimespanForCalculation ();

        // calculate values of the past
        this.latestProcessedTime = getLatestProcessedValueTime ();
    }

    /**
     * This method returns the logic provider for calculation of values for storage channel.
     * @return logic provider for calculation of values for storage channel
     */
    public CalculationLogicProvider getCalculationLogicProvider ()
    {
        return calculationLogicProvider;
    }

    /**
     * This method returns an empty array of the data type that is requested.
     * @param dataType data type of the requested array
     * @return array of the data type that is requested
     */
    private BaseValue[] getEmptyArray ( final DataType dataType )
    {
        switch ( dataType )
        {
        case LONG_VALUE:
        {
            return ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY;
        }
        case DOUBLE_VALUE:
        {
            return ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY;
        }
        }
        return null;
    }

    /**
     * This method retrieves the values from the passed storage channel matching the specified time span.
     * @param storageChannel storage channel to be used
     * @param dataType data type that has to be retrieved
     * @param startTime start time of time span
     * @param endTime end time of time span
     * @return retrieved values
     */
    private BaseValue[] getValues ( final ExtendedStorageChannel storageChannel, final DataType dataType, final long startTime, final long endTime )
    {
        if ( storageChannel != null )
        {
            try
            {
                switch ( dataType )
                {
                case LONG_VALUE:
                {
                    return storageChannel.getLongValues ( startTime, endTime );
                }
                case DOUBLE_VALUE:
                {
                    return storageChannel.getDoubleValues ( startTime, endTime );
                }
                }
            }
            catch ( final Exception e )
            {
                logger.warn ( "could not retrieve values!", e );
            }
        }
        return null;
    }

    /**
     * This method returns the time when the time span containing the passed time stamp that has to be processed by the calculation logic starts.
     * @param time time stamp within the time span
     * @return start of the time span
     */
    private long getTimeSpanStart ( final long time )
    {
        return time - time % calculationLogicProvider.getRequiredTimespanForCalculation ();
    }

    /**
     * This method returns the last value that was calculated and processed.
     * If no value was calculated until now, Long.MIN_VALUE will be returned.
     * @return time of last value that was calculated and processed or Long.MIN_VALUE if no calculation has been performed yet
     */
    private long getLatestProcessedValueTime ()
    {
        lastValue = null;
        if ( baseStorageChannel != null )
        {
            final BaseValue[] values = getValues ( baseStorageChannel, calculationLogicProvider.getOutputType (), Long.MAX_VALUE - 1, Long.MAX_VALUE );
            lastValue = ( values != null ) && ( values.length > 0 ) ? values[0] : null;
        }
        return ( lastValue == null ) ? Long.MIN_VALUE : lastValue.getTime ();
    }

    /**
     * This method calculates all values that should have been calculated in the past, but are not available in the storage channel.
     * This is the case if the system was shut down or if a new storage channel with a new calculation method has been added.
     * @param startTime start time of the time span for which a calculation has to be made
     * @param endTime end time of the time span for which a calculation has to be made
     * @throws Exception in case of any problems
     */
    private void calculateOldValues ( final long startTime, final long endTime ) throws Exception
    {
        if ( inputStorageChannel != null )
        {
            final DataType inputType = calculationLogicProvider.getInputType ();
            final BaseValue[] values = getValues ( inputStorageChannel, inputType, startTime, endTime );
            if ( ( values != null ) && ( values.length > 0 ) )
            {
                processValues ( values, startTime, endTime );
            }
        }
    }

    /**
     * This method triggers the functionality that retrieves old values from the input storage channel and calculates values for the base storage channel.
     * The method should not be called outside of this package.
     * @param values values that have to be processed
     * @throws Exception in case of any problems
     */
    private void notifyNewValues ( final BaseValue[] values ) throws Exception
    {
        // return if no times have been passed to method
        if ( values == null )
        {
            return;
        }

        // extract and process times
        final long[] times = new long[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            times[i] = values[i].getTime ();
        }
        notifyNewValues ( times );
    }

    /**
     * This method triggers the functionality that retrieves old values from the input storage channel and calculates values for the base storage channel.
     * The method should not be called outside of this package.
     * @param times times that have to be processed
     * @throws Exception in case of any problems
     */
    private void notifyNewValues ( final long[] times ) throws Exception
    {
        // return if no times have been passed to method
        if ( ( times == null ) || ( times.length == 0 ) )
        {
            return;
        }

        // assure that at least one value exists
        if ( latestProcessedTime == Long.MIN_VALUE )
        {
            latestProcessedTime = getTimeSpanStart ( times[0] );
        }

        // collect all timespan blocks that have to be updated
        final long now = System.currentTimeMillis ();
        final Set<Long> startTimes = new HashSet<Long> ();
        final long timeOfInterest = getTimeSpanStart ( times[times.length - 1] - 1 );
        long futureLatestProcessedTime = Math.max ( latestProcessedTime, timeOfInterest );
        futureLatestProcessedTime = Math.max ( futureLatestProcessedTime, getTimeSpanStart ( now ) );

        // add blocks for which real values are available
        final long requiredTimespanForCalculation = calculationLogicProvider.getRequiredTimespanForCalculation ();
        for ( final long time : times )
        {
            if ( time < futureLatestProcessedTime )
            {
                final long startTime = getTimeSpanStart ( time );
                startTimes.add ( startTime );
                final long nextTime = latestProcessedTime + requiredTimespanForCalculation;
                if ( nextTime < futureLatestProcessedTime )
                {
                    startTimes.add ( nextTime );
                }
            }
        }

        // add blocks that have not yet been processed
        if ( latestProcessedTime < futureLatestProcessedTime )
        {
            startTimes.add ( latestProcessedTime );
            final long nextTime = latestProcessedTime + requiredTimespanForCalculation;
            if ( nextTime < futureLatestProcessedTime )
            {
                startTimes.add ( nextTime );
            }
        }

        // process time spans
        final long timeSpan = requiredTimespanForCalculation;
        final Long[] sortedStartTimes = startTimes.toArray ( new Long[0] );
        Arrays.sort ( sortedStartTimes );
        for ( final long startTime : sortedStartTimes )
        {
            final long endTime = startTime + timeSpan;
            calculateOldValues ( startTime, endTime );
            if ( latestProcessedTime < endTime )
            {
                latestProcessedTime = startTime;
            }
        }
    }

    /**
     * This method forwards the passed values to the correct processing method.
     * @param values values that have to be processed
     * @param minStartTime mimimum start time of the time spans that will be processed
     * @param maxEndTime maximum end of the time spans that will be processed
     */
    private void processValues ( final BaseValue[] values, final long minStartTime, final long maxEndTime ) throws Exception
    {
        if ( ( values != null ) && ( values.length > 0 ) )
        {
            final BaseValue[] emptyArray = getEmptyArray ( calculationLogicProvider.getInputType () );
            final long blockMid = Math.max ( minStartTime, values[0].getTime () );
            if ( blockMid >= getTimeSpanStart ( System.currentTimeMillis () ) )
            {
                return;
            }
            long blockStart = getTimeSpanStart ( blockMid );
            while ( blockStart < maxEndTime )
            {
                final long blockEnd = blockStart + blockTimeSpan;
                final BaseValue[] valueBlock = ValueArrayNormalizer.extractSubArray ( values, blockStart, blockEnd, 0, emptyArray );
                if ( valueBlock.length == 0 )
                {
                    break;
                }
                try
                {
                    final DataType dt = calculationLogicProvider.getOutputType ();
                    switch ( dt )
                    {
                    case LONG_VALUE:
                    {
                        final LongValue longValue = (LongValue)calculationLogicProvider.generateValues ( valueBlock );
                        if ( baseStorageChannel != null )
                        {
                            if ( ( longValue != null ) && ( ( lastValue == null ) || !valueContentEquals ( (LongValue)lastValue, longValue ) ) )
                            {
                                baseStorageChannel.updateLong ( longValue );
                            }
                            lastValue = longValue;
                        }
                        super.updateLong ( longValue );
                        break;
                    }
                    case DOUBLE_VALUE:
                    {
                        final DoubleValue doubleValue = (DoubleValue)calculationLogicProvider.generateValues ( valueBlock );
                        if ( baseStorageChannel != null )
                        {
                            if ( ( doubleValue != null ) && ( ( lastValue == null ) || !valueContentEquals ( (DoubleValue)lastValue, doubleValue ) ) )
                            {
                                baseStorageChannel.updateDouble ( doubleValue );
                            }
                            lastValue = doubleValue;
                        }
                        super.updateDouble ( doubleValue );
                        break;
                    }
                    }
                }
                catch ( final Exception e )
                {
                    final String message = "could not process values!";
                    logger.error ( message, e );
                    throw new Exception ( message, e );
                }
                blockStart = blockEnd;
            }
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getMetaData
     */
    public synchronized StorageChannelMetaData getMetaData () throws Exception
    {
        if ( baseStorageChannel == null )
        {
            final String message = "no base storage channel available for calculating storage channel! unable to retrieve meta data";
            logger.error ( message );
            throw new Exception ( message );
        }
        return baseStorageChannel.getMetaData ();
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateLong
     */
    public synchronized void updateLong ( final LongValue longValue ) throws Exception
    {
        updateLongs ( new LongValue[] { longValue } );
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateLongs
     */
    public synchronized void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        if ( calculationLogicProvider.getPassThroughValues () )
        {
            if ( baseStorageChannel != null )
            {
                baseStorageChannel.updateLongs ( longValues );
            }
            super.updateLongs ( longValues );
        }
        else
        {
            notifyNewValues ( longValues );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        if ( baseStorageChannel != null )
        {
            return baseStorageChannel.getLongValues ( startTime, endTime );
        }
        return EMPTY_LONGVALUE_ARRAY;
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDouble
     */
    public synchronized void updateDouble ( final DoubleValue doubleValue ) throws Exception
    {
        updateDoubles ( new DoubleValue[] { doubleValue } );
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDoubles
     */
    public synchronized void updateDoubles ( final DoubleValue[] doubleValues ) throws Exception
    {
        if ( calculationLogicProvider.getPassThroughValues () )
        {
            if ( baseStorageChannel != null )
            {
                baseStorageChannel.updateDoubles ( doubleValues );
            }
            super.updateDoubles ( doubleValues );
        }
        else
        {
            notifyNewValues ( doubleValues );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getDoubleValues
     */
    public synchronized DoubleValue[] getDoubleValues ( final long startTime, final long endTime ) throws Exception
    {
        if ( baseStorageChannel != null )
        {
            return baseStorageChannel.getDoubleValues ( startTime, endTime );
        }
        return EMPTY_DOUBLEVALUE_ARRAY;
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#cleanupRelicts
     */
    public synchronized void cleanupRelicts () throws Exception
    {
        notifyNewValues ( new long[] { System.currentTimeMillis () - 1 } );
        super.cleanupRelicts ();
        baseStorageChannel.cleanupRelicts ();
    }

    /**
     * This method returns true, if both values are identical in all values except the time stamp.
     * @param value1 first value to compare
     * @param value2 second value to compare
     * @return true, if both values are identical in all values except the time stamp, otherwise false
     */
    private static boolean valueContentEquals ( final LongValue value1, final LongValue value2 )
    {
        return ( value1.getQualityIndicator () == value2.getQualityIndicator () ) && ( value1.getValue () == value2.getValue () ) && ( value1.getBaseValueCount () == value2.getBaseValueCount () );
    }

    /**
     * This method returns true, if both values are identical in all values except the time stamp.
     * @param value1 first value to compare
     * @param value2 second value to compare
     * @return true, if both values are identical in all values except the time stamp, otherwise false
     */
    private static boolean valueContentEquals ( final DoubleValue value1, final DoubleValue value2 )
    {
        return ( value1.getQualityIndicator () == value2.getQualityIndicator () ) && ( ( Double.isNaN ( value1.getValue () ) && Double.isNaN ( value2.getValue () ) ) || ( value1.getValue () == value2.getValue () ) ) && ( value1.getBaseValueCount () == value2.getBaseValueCount () );
    }
}
