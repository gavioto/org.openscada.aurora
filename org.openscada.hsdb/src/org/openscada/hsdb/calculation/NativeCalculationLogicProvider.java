package org.openscada.hsdb.calculation;

import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;

/**
 * This class implements the CalculationLogicProvider interface for the processing of native values.
 * @author Ludwig Straub
 */
public class NativeCalculationLogicProvider extends CalculationLogicProviderBase
{
    /**
     * Constructor.
     * @param inputDataType data type of the input values
     * @param outputDataType data type of the output values
     * @param parameters parameters further specifying the behaviour
     */
    public NativeCalculationLogicProvider ( final DataType inputDataType, final DataType outputDataType, final long[] parameters )
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
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateLong
     */
    protected long calculateLong ( final LongValue[] values )
    {
        return 0;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateLong
     */
    protected long calculateLong ( final DoubleValue[] values )
    {
        return 0;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateDouble
     */
    protected double calculateDouble ( final LongValue[] values )
    {
        return Double.NaN;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateDouble
     */
    protected double calculateDouble ( final DoubleValue[] values )
    {
        return Double.NaN;
    }
}
