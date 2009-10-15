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
        return true;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#generateLongValues
     */
    protected LongValue[] generateLongValues ( final LongValue[] values )
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
        return new LongValue[] { new LongValue ( values[0].getTime (), quality / values.length, baseValueCount, minValue ) };
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#generateDoubleValues
     */
    protected LongValue[] generateLongValues ( final DoubleValue[] values )
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
        return new LongValue[] { new LongValue ( values[0].getTime (), quality / values.length, baseValueCount, (long)minValue ) };
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#generateDoubleValues
     */
    protected DoubleValue[] generateDoubleValues ( final LongValue[] values )
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
        return new DoubleValue[] { new DoubleValue ( values[0].getTime (), quality / values.length, baseValueCount, minValue ) };
    }

    /**
     * This method generates double values for the time span starting with the first element in the array and ending after {@link #getRequiredTimespanForCalculation()}.
     * @param values double values that were processed during the time span
     * @return calculated double values
     */
    protected DoubleValue[] generateDoubleValues ( final DoubleValue[] values )
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
        return new DoubleValue[] { new DoubleValue ( values[0].getTime (), quality / values.length, baseValueCount, minValue ) };
    }
}
