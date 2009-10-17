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
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#generateLongValue
     */
    protected LongValue generateLongValue ( final LongValue[] values )
    {
        long minValue = Long.MAX_VALUE;
        double quality = 0;
        long baseValueCount = 0;
        for ( LongValue value : values )
        {
            minValue = Math.min ( minValue, value.getValue () );
            quality += value.getQualityIndicator ();
            baseValueCount += value.getBaseValueCount ();
        }
        return new LongValue ( values[0].getTime (), quality / values.length, baseValueCount, minValue );
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#generateDoubleValue
     */
    protected LongValue generateLongValue ( final DoubleValue[] values )
    {
        double minValue = Double.POSITIVE_INFINITY;
        double quality = 0;
        long baseValueCount = 0;
        for ( DoubleValue value : values )
        {
            minValue = Math.min ( minValue, value.getValue () );
            quality += value.getQualityIndicator ();
            baseValueCount += value.getBaseValueCount ();
        }
        return new LongValue ( values[0].getTime (), quality / values.length, baseValueCount, (long)minValue );
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#generateDoubleValue
     */
    protected DoubleValue generateDoubleValue ( final LongValue[] values )
    {
        long minValue = Long.MAX_VALUE;
        double quality = 0;
        long baseValueCount = 0;
        for ( LongValue value : values )
        {
            minValue = Math.min ( minValue, value.getValue () );
            quality += value.getQualityIndicator ();
            baseValueCount += value.getBaseValueCount ();
        }
        return new DoubleValue ( values[0].getTime (), quality / values.length, baseValueCount, minValue );
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#generateDoubleValue
     */
    protected DoubleValue generateDoubleValue ( final DoubleValue[] values )
    {
        double minValue = Double.POSITIVE_INFINITY;
        double quality = 0;
        long baseValueCount = 0;
        for ( DoubleValue value : values )
        {
            minValue = Math.min ( minValue, value.getValue () );
            quality += value.getQualityIndicator ();
            baseValueCount += value.getBaseValueCount ();
        }
        return new DoubleValue ( values[0].getTime (), quality / values.length, baseValueCount, minValue );
    }
}
