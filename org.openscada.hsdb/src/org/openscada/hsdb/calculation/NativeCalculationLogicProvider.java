package org.openscada.hsdb.calculation;

import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DataType;

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
     * @see org.openscada.hsdb.calculation.CalculationLogicProvider#generateValues
     */
    public BaseValue[] generateValues ( BaseValue[] values )
    {
        return values;
    }
}
