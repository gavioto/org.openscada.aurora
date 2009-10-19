package org.openscada.hsdb.utils;

import java.util.ArrayList;
import java.util.List;

import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides methods for normalizing arrrays of base values.
 * Normalizing means, that all time differences of adjacent elements within the array are equal in size.
 * @author Ludwig Straub
 */
public class ValueArrayNormalizer
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( ValueArrayNormalizer.class );

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
        if ( ( values == null ) || ( values.length == 0 ) || ( startTime >= endTime ) || ( startIndex >= values.length ) )
        {
            return emptyResultArray;
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
                    blockValues.add ( new LongValue ( startTime, firstValue.getQualityIndicator (), firstValue.getBaseValueCount (), firstValue instanceof LongValue ? ( (LongValue)firstValue ).getValue () : (long) ( (DoubleValue)firstValue ).getValue () ) );
                }
                else
                {
                    blockValues.add ( new LongValue ( startTime, 0, 0, 0 ) );
                }
            }
            else
            {
                if ( firstStartTime < startTime )
                {
                    blockValues.add ( new DoubleValue ( startTime, firstValue.getQualityIndicator (), firstValue.getBaseValueCount (), firstValue instanceof LongValue ? ( (LongValue)firstValue ).getValue () : ( (DoubleValue)firstValue ).getValue () ) );
                }
                else
                {
                    blockValues.add ( new DoubleValue ( startTime, 0, 0, 0 ) );
                }
            }
        }
        for ( int i = firstRelevantEntryIndex + ( firstStartTime == startTime ? 0 : 1 ); i < lastRelevantEntryIndex; i++ )
        {
            blockValues.add ( values[i] );
        }
        if ( blockValues.isEmpty () )
        {
            logger.error ( String.format ( "no block block values have been calculated (start:%s;end:%s;values:%s)", startTime, endTime, values ) );
            return emptyResultArray;
        }
        final BaseValue lastValue = blockValues.get ( blockValues.size () - 1 );
        if ( lastValue.getTime () != endTime )
        {
            if ( emptyResultArray instanceof LongValue[] )
            {
                blockValues.add ( new LongValue ( endTime, lastValue.getQualityIndicator (), lastValue.getBaseValueCount (), lastValue instanceof LongValue ? ( (LongValue)lastValue ).getValue () : (long) ( (DoubleValue)lastValue ).getValue () ) );
            }
            else
            {
                blockValues.add ( new DoubleValue ( endTime, lastValue.getQualityIndicator (), lastValue.getBaseValueCount (), lastValue instanceof LongValue ? ( (LongValue)lastValue ).getValue () : (long) ( (DoubleValue)lastValue ).getValue () ) );
            }
        }
        return blockValues.toArray ( emptyResultArray );
    }
}
