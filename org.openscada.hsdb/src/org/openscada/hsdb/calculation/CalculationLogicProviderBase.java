package org.openscada.hsdb.calculation;

import org.openscada.hsdb.datatypes.BaseValue;
import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provided methods for CalculationLogicProvider implementations.
 * @author Ludwig Straub
 */
public abstract class CalculationLogicProviderBase implements CalculationLogicProvider
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( CalculationLogicProviderBase.class );

    /** Parameter index of the calculation time span. */
    protected final static int TIMESPAN_FOR_CALCULATION_INDEX = 0;

    /** Default value of the calculation time span. */
    protected final static long TIMESPAN_FOR_CALCULATION_DEFAULT = 1000;

    /** Data type of the input values. */
    private final DataType inputDataType;

    /** Data type of the output values. */
    private final DataType outputDataType;

    /** Parameters further specifying the behaviour. */
    private final long[] parameters;

    /**
     * Constructor.
     * @param inputDataType data type of the input values
     * @param outputDataType data type of the output values
     * @param parameters parameters further specifying the behaviour
     */
    public CalculationLogicProviderBase ( final DataType inputDataType, final DataType outputDataType, final long[] parameters )
    {
        this.inputDataType = inputDataType;
        this.outputDataType = outputDataType;
        this.parameters = new long[parameters.length];
        for ( int i = 0; i < parameters.length; i++ )
        {
            this.parameters[i] = parameters[i];
        }
    }

    /**
     * This method returns the specified parameter.
     * @param index index of the parameter that has to be retrieved
     * @param defaultValue default value that will be returned if the index is not available
     * @return value of the specified parameter or the passed default value if the requested index is not available
     */
    protected long getParameterValue ( final int index, final long defaultValue )
    {
        if ( ( parameters == null ) || ( index < 0 ) || ( parameters.length <= index ) )
        {
            return defaultValue;
        }
        return parameters[index];
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProvider#getRequiredTimespanForCalculation
     */
    public long getRequiredTimespanForCalculation ()
    {
        return getParameterValue ( TIMESPAN_FOR_CALCULATION_INDEX, TIMESPAN_FOR_CALCULATION_DEFAULT );
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProvider#getInputType
     */
    public DataType getInputType ()
    {
        return inputDataType;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProvider#getOutputType
     */
    public DataType getOutputType ()
    {
        return outputDataType;
    }

    /**
     * This method calculates a value applying the implementation specific calculation logic using the passed values as input.
     * @param values input values with at least one element
     * @return calculated value
     */
    protected abstract long calculateLong ( final LongValue[] values );

    /**
     * This method calculates a value applying the implementation specific calculation logic using the passed values as input.
     * @param values input values with at least one element
     * @return calculated value
     */
    protected abstract long calculateLong ( final DoubleValue[] values );

    /**
     * This method calculates a value applying the implementation specific calculation logic using the passed values as input.
     * @param values input values with at least one element
     * @return calculated value
     */
    protected abstract double calculateDouble ( final LongValue[] values );

    /**
     * This method calculates a value applying the implementation specific calculation logic using the passed values as input.
     * @param values input values with at least one element
     * @return calculated value
     */
    protected abstract double calculateDouble ( final DoubleValue[] values );

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProvider#generateValues
     */
    public BaseValue generateValues ( final BaseValue[] values )
    {
        // check input
        if ( ( values == null ) || ( values.length == 0 ) )
        {
            return null;
        }

        // calculate base values
        final BaseValue firstValue = values[0];
        final long time = firstValue.getTime ();
        double quality = 0;
        double manual = 0;
        long baseValueCount = 0;
        boolean validValue = false;
        if ( values.length == 1 )
        {
            quality = firstValue.getQualityIndicator ();
            manual = firstValue.getManualIndicator ();
            baseValueCount = firstValue.getBaseValueCount ();
            validValue = quality > 0;
        }
        else
        {
            long lastTimeStamp = firstValue.getTime ();
            double lastQuality = firstValue.getQualityIndicator ();
            double lastManual = firstValue.getManualIndicator ();
            baseValueCount = firstValue.getBaseValueCount ();
            validValue = lastQuality > 0.0;
            for ( int i = 1; i < values.length; i++ )
            {
                final BaseValue value = values[i];
                baseValueCount += value.getBaseValueCount ();
                final long currentTime = value.getTime ();
                final long timeSpan = currentTime - lastTimeStamp;
                final double qualityIndicator = value.getQualityIndicator ();
                validValue |= qualityIndicator > 0;
                final double manualIndicator = value.getManualIndicator ();
                quality += lastQuality * timeSpan;
                manual += lastManual * timeSpan;
                lastTimeStamp = currentTime;
                lastQuality = qualityIndicator;
                lastManual = manualIndicator;
            }
            final long timeSpanSize = values[values.length - 1].getTime () - time;
            quality /= timeSpanSize;
            manual /= timeSpanSize;
        }

        // process values
        switch ( getInputType () )
        {
        case LONG_VALUE:
        {
            switch ( getOutputType () )
            {
            case LONG_VALUE:
            {
                return new LongValue ( time, quality, manual, baseValueCount, validValue ? calculateLong ( (LongValue[])values ) : 0 );
            }
            case DOUBLE_VALUE:
            {
                return new DoubleValue ( time, quality, manual, baseValueCount, validValue ? calculateDouble ( (LongValue[])values ) : 0 );
            }
            default:
            {
                logger.error ( "invalid output data type specified within CalculationLogicProvider!" );
            }
            }
            break;
        }
        case DOUBLE_VALUE:
        {
            switch ( getOutputType () )
            {
            case LONG_VALUE:
            {
                return new LongValue ( time, quality, manual, baseValueCount, validValue ? calculateLong ( (DoubleValue[])values ) : 0 );
            }
            case DOUBLE_VALUE:
            {
                return new DoubleValue ( time, quality, manual, baseValueCount, validValue ? calculateDouble ( (DoubleValue[])values ) : 0 );
            }
            default:
            {
                logger.error ( "invalid output data type specified within CalculationLogicProvider!" );
            }
            }
            break;
        }
        default:
        {
            logger.error ( "invalid input data type specified within CalculationLogicProvider!" );
        }
        }
        return null;
    }
}
