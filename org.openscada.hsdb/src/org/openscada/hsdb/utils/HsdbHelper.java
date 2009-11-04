package org.openscada.hsdb.utils;

import java.util.ArrayList;
import java.util.List;

import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.calculation.CalculationLogicProvider;
import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides methods for normalizing arrrays of base values.
 * Normalizing means, that all time differences of adjacent elements within the array are equal in size.
 * @author Ludwig Straub
 */
public class HsdbHelper
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( HsdbHelper.class );

    /** Maximum values that will be read from the input storage channel when processing data. */
    private final static long MAX_GAP_COUNT = 100000;

    /**
     * This method extracts a sub array out of the passed array of elements matching the specified criteria.
     * If the exact specified start time is not available in the array then a new virtual entry will be created at the beginning of the resulting array with the start time.
     * If the exact specified end time is not available in the array then a new virtual entry will be created at the beginning of the resulting array with the start time.
     * @param values values to be processed
     * @param startTime start time for extracting
     * @param endTime end time for extracting
     * @param startIndex index where to start the search for valid array entries. the index has to be set before the first value that lies within the requested time span or it has to be set to 0, if the search should start at the beginning of the array
     * @param emptyResultArray empty array that can be used as template for the result
     * @return normalized array
     */
    public static BaseValue[] extractSubArray ( final BaseValue[] values, final long startTime, final long endTime, final int startIndex, final BaseValue[] emptyResultArray )
    {
        if ( startTime >= endTime )
        {
            return emptyResultArray;
        }
        if ( ( values == null ) || ( values.length == 0 ) || ( startIndex >= values.length ) )
        {
            if ( emptyResultArray instanceof LongValue[] )
            {
                return new LongValue[] { new LongValue ( startTime, 0, 0, 0, 0 ), new LongValue ( endTime, 0, 0, 0, 0 ) };
            }
            else
            {
                return new DoubleValue[] { new DoubleValue ( startTime, 0, 0, 0, 0 ), new DoubleValue ( endTime, 0, 0, 0, 0 ) };
            }
        }
        final List<BaseValue> blockValues = new ArrayList<BaseValue> ();
        int firstRelevantEntryIndex = startIndex;
        int lastRelevantEntryIndex = values.length;
        for ( int i = firstRelevantEntryIndex; i < values.length; i++ )
        {
            if ( values[i].getTime () <= startTime )
            {
                firstRelevantEntryIndex = i;
            }
            if ( values[i].getTime () >= endTime )
            {
                lastRelevantEntryIndex = i;
                break;
            }
        }
        final BaseValue firstValue = values[firstRelevantEntryIndex];
        final long firstStartTime = firstValue.getTime ();
        if ( firstStartTime != startTime )
        {
            if ( emptyResultArray instanceof LongValue[] )
            {
                if ( firstStartTime < startTime )
                {
                    blockValues.add ( new LongValue ( startTime, firstValue.getQualityIndicator (), firstValue.getManualIndicator (), firstValue.getBaseValueCount (), firstValue instanceof LongValue ? ( (LongValue)firstValue ).getValue () : Math.round ( ( (DoubleValue)firstValue ).getValue () ) ) );
                }
                else
                {
                    blockValues.add ( new LongValue ( startTime, 0, 0, 0, 0 ) );
                }
            }
            else
            {
                if ( firstStartTime < startTime )
                {
                    blockValues.add ( new DoubleValue ( startTime, firstValue.getQualityIndicator (), firstValue.getManualIndicator (), firstValue.getBaseValueCount (), firstValue instanceof LongValue ? ( (LongValue)firstValue ).getValue () : ( (DoubleValue)firstValue ).getValue () ) );
                }
                else
                {
                    blockValues.add ( new DoubleValue ( startTime, 0, 0, 0, 0 ) );
                }
            }
        }
        for ( int i = firstRelevantEntryIndex + ( firstStartTime < startTime ? 1 : 0 ); i < lastRelevantEntryIndex; i++ )
        {
            blockValues.add ( values[i] );
        }
        if ( blockValues.isEmpty () )
        {
            // this is not reachable if the logic is implemented correctly
            logger.error ( String.format ( "no block block values have been calculated (start:%s;end:%s;values:%s)", startTime, endTime, values ) );
            return emptyResultArray;
        }
        final BaseValue lastValue = blockValues.get ( blockValues.size () - 1 );
        if ( lastValue.getTime () != endTime )
        {
            if ( emptyResultArray instanceof LongValue[] )
            {
                blockValues.add ( new LongValue ( endTime, lastValue.getQualityIndicator (), lastValue.getManualIndicator (), 0, lastValue instanceof LongValue ? ( (LongValue)lastValue ).getValue () : Math.round ( ( (DoubleValue)lastValue ).getValue () ) ) );
            }
            else
            {
                blockValues.add ( new DoubleValue ( endTime, lastValue.getQualityIndicator (), lastValue.getManualIndicator (), 0, lastValue instanceof LongValue ? ( (LongValue)lastValue ).getValue () : ( (DoubleValue)lastValue ).getValue () ) );
            }
        }
        return blockValues.toArray ( emptyResultArray );
    }

    /**
     * This method reads data from the input channel and creates values for the output channel.
     * @param inputChannel storage channel from which data has to be extracted
     * @param outputChannel storage channel to which data has to be written
     * @param inputCalculationLogicProvider calculation logic provider containing information for the input channel
     * @param outputCalculationLogicProvider calculation logic provider containing information for the output channel
     * @param startTime time stamp where to begin the data processing
     * @param endTime time stamp where to end the data processing
     * @throws Exception in case of read or write problems during the data processing
     */
    public static void processData ( final ExtendedStorageChannel inputChannel, final ExtendedStorageChannel outputChannel, final CalculationLogicProvider inputCalculationLogicProvider, final CalculationLogicProvider outputCalculationLogicProvider, final long startTime, final long endTime ) throws Exception
    {
        // prepare data that is required for processing
        if ( endTime <= startTime )
        {
            return;
        }
        if ( ( inputChannel == null ) || ( outputChannel == null ) || ( inputCalculationLogicProvider == null ) || ( outputCalculationLogicProvider == null ) )
        {
            final String message = "insufficient input data: parameters must not be null";
            logger.error ( message );
            throw new Exception ( message );
        }
        final DataType inputDataType = inputCalculationLogicProvider.getOutputType ();
        final DataType outputDataType = outputCalculationLogicProvider.getOutputType ();
        if ( inputDataType != outputCalculationLogicProvider.getInputType () )
        {
            final String message = "input datatype does not match expected datatype";
            logger.error ( message );
            throw new Exception ( message );
        }
        final long inputTimespan = inputCalculationLogicProvider.getRequiredTimespanForCalculation ();
        final long outputTimespan = outputCalculationLogicProvider.getRequiredTimespanForCalculation ();

        // calculate time span that will be read and processed per iteration step
        final long compressionFactor = outputTimespan / inputTimespan;
        final long gapCount = Math.max ( MAX_GAP_COUNT / compressionFactor, 1 );
        final long fetchTimeSpan = gapCount * outputTimespan;

        // process data
        final BaseValue[] emptyInputArray = inputDataType == DataType.LONG_VALUE ? ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY : ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY;
        final BaseValue[] emptyOutputArray = outputDataType == DataType.LONG_VALUE ? ExtendedStorageChannel.EMPTY_LONGVALUE_ARRAY : ExtendedStorageChannel.EMPTY_DOUBLEVALUE_ARRAY;
        long currentStart = startTime;
        final List<BaseValue> newValues = new ArrayList<BaseValue> ();
        BaseValue oldValue = null;
        do
        {
            final long currentEnd = Math.min ( currentStart + fetchTimeSpan, endTime );
            final BaseValue[] inputValues = inputDataType == DataType.LONG_VALUE ? inputChannel.getLongValues ( currentStart, currentEnd ) : inputChannel.getDoubleValues ( currentStart, currentEnd );
            int startIndex = 0;
            while ( currentStart < currentEnd )
            {
                final long gapEnd = currentStart + outputTimespan;
                final BaseValue[] normalizedValues = HsdbHelper.extractSubArray ( inputValues, currentStart, gapEnd, 0, emptyInputArray );
                // maximum 2 entries are completely virtual due to the algorithm
                // it is possible that one value will be processed with a time span before the interval start time
                // therefore the index can be increased by length-3 to optimize performance of this method
                if ( inputValues.length > 1 )
                {
                    if ( normalizedValues.length > 3 )
                    {
                        startIndex += normalizedValues.length - 3;
                    }
                    final long lastFilledValueTime = inputValues[inputValues.length - 1].getTime ();
                    final long size = inputValues.length;
                    while ( ( startIndex + 1 < size ) && ( inputValues[startIndex + 1].getTime () < lastFilledValueTime ) )
                    {
                        startIndex++;
                    }
                }
                final BaseValue newValue = outputCalculationLogicProvider.generateValues ( normalizedValues );
                if ( valueChanged ( oldValue, newValue ) )
                {
                    newValues.add ( newValue );
                }
                oldValue = newValue;
                currentStart = gapEnd;
            }
            if ( !newValues.isEmpty () )
            {
                if ( outputDataType == DataType.LONG_VALUE )
                {
                    outputChannel.updateLongs ( (LongValue[])newValues.toArray ( emptyOutputArray ) );
                }
                else
                {
                    outputChannel.updateDoubles ( (DoubleValue[])newValues.toArray ( emptyOutputArray ) );
                }
                newValues.clear ();
            }
        } while ( currentStart < endTime );
    }

    /**
     * This method checks whether the new value changed its internal attributes so that a new entry has to be stored.
     * All attributes will be compared except the time stamp.
     * @param oldValue old value that will be used as reference
     * @param newValue new value value that will be compared
     * @return true, if the value changed, otherwise false
     */
    public static boolean valueChanged ( final BaseValue oldValue, final BaseValue newValue )
    {
        if ( newValue == null )
        {
            return false;
        }
        if ( oldValue == null )
        {
            return newValue != null;
        }
        if ( ( oldValue.getQualityIndicator () != newValue.getQualityIndicator () ) || ( oldValue.getManualIndicator () != newValue.getManualIndicator () ) )
        {
            return true;
        }
        if ( ( oldValue instanceof LongValue ) && ( newValue instanceof LongValue ) && ( ( (LongValue)oldValue ).getValue () != ( (LongValue)newValue ).getValue () ) )
        {
            return true;
        }
        if ( ( oldValue instanceof DoubleValue ) && ( newValue instanceof DoubleValue ) && ( ( (DoubleValue)oldValue ).getValue () != ( (DoubleValue)newValue ).getValue () ) )
        {
            return true;
        }
        return false;
    }
}
