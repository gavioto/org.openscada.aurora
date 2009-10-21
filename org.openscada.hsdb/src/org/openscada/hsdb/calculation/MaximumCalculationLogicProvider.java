package org.openscada.hsdb.calculation;

import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;

/**
 * This class implements the CalculationLogicProvider interface for the calculation of maximum values.
 * @author Ludwig Straub
 */
public class MaximumCalculationLogicProvider extends CalculationLogicProviderBase
{
    /**
     * Constructor.
     * @param inputDataType data type of the input values
     * @param outputDataType data type of the output values
     * @param parameters parameters further specifying the behaviour
     */
    public MaximumCalculationLogicProvider ( final DataType inputDataType, final DataType outputDataType, final long[] parameters )
    {
        super ( inputDataType, outputDataType, parameters );
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProvider#getPassThroughValues
     */
    public boolean getPassThroughValues ()
    {
        return false;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateLong
     */
    protected long calculateLong ( final LongValue[] values )
    {
        long maxValue = Long.MIN_VALUE;
        for ( final LongValue value : values )
        {
            if ( value.getQualityIndicator () > 0 )
            {
                maxValue = Math.max ( maxValue, value.getValue () );
            }
        }
        return maxValue;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateLong
     */
    protected long calculateLong ( final DoubleValue[] values )
    {
        double maxValue = Double.NEGATIVE_INFINITY;
        for ( final DoubleValue value : values )
        {
            if ( value.getQualityIndicator () > 0 )
            {
                maxValue = Math.max ( maxValue, value.getValue () );
            }
        }
        return Math.round ( maxValue );
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateDouble
     */
    protected double calculateDouble ( final LongValue[] values )
    {
        double maxValue = Long.MIN_VALUE;
        for ( final LongValue value : values )
        {
            if ( value.getQualityIndicator () > 0 )
            {
                maxValue = Math.max ( maxValue, value.getValue () );
            }
        }
        return maxValue;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateDouble
     */
    protected double calculateDouble ( final DoubleValue[] values )
    {
        double maxValue = Double.NEGATIVE_INFINITY;
        for ( final DoubleValue value : values )
        {
            if ( value.getQualityIndicator () > 0 )
            {
                maxValue = Math.max ( maxValue, value.getValue () );
            }
        }
        return maxValue;
    }
}
