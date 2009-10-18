package org.openscada.hsdb.calculation;

import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;

/**
 * This class implements the CalculationLogicProvider interface for the calculation of minimum values.
 * @author Ludwig Straub
 */
public class MinimumCalculationLogicProvider extends CalculationLogicProviderBase
{
    /**
     * Constructor.
     * @param inputDataType data type of the input values
     * @param outputDataType data type of the output values
     * @param parameters parameters further specifying the behaviour
     */
    public MinimumCalculationLogicProvider ( final DataType inputDataType, final DataType outputDataType, final long[] parameters )
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
        long minValue = Long.MAX_VALUE;
        for ( final LongValue value : values )
        {
            if ( value.getQualityIndicator () > 0 )
            {
                minValue = Math.min ( minValue, value.getValue () );
            }
        }
        return minValue;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateLong
     */
    protected long calculateLong ( final DoubleValue[] values )
    {
        double minValue = Double.POSITIVE_INFINITY;
        for ( final DoubleValue value : values )
        {
            if ( value.getQualityIndicator () > 0 )
            {
                minValue = Math.min ( minValue, value.getValue () );
            }
        }
        return (long)minValue;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateDouble
     */
    protected double calculateDouble ( final LongValue[] values )
    {
        long minValue = Long.MAX_VALUE;
        for ( final LongValue value : values )
        {
            if ( value.getQualityIndicator () > 0 )
            {
                minValue = Math.min ( minValue, value.getValue () );
            }
        }
        return minValue;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateDouble
     */
    protected double calculateDouble ( final DoubleValue[] values )
    {
        double minValue = Double.POSITIVE_INFINITY;
        for ( final DoubleValue value : values )
        {
            if ( value.getQualityIndicator () > 0 )
            {
                minValue = Math.min ( minValue, value.getValue () );
            }
        }
        return minValue;
    }
}
