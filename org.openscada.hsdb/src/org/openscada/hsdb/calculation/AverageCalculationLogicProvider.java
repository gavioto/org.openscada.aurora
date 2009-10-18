package org.openscada.hsdb.calculation;

import org.openscada.hsdb.datatypes.DataType;
import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;

/**
 * This class implements the CalculationLogicProvider interface for the calculation of average values.
 * @author Ludwig Straub
 */
public class AverageCalculationLogicProvider extends CalculationLogicProviderBase
{
    /**
     * Constructor.
     * @param inputDataType data type of the input values
     * @param outputDataType data type of the output values
     * @param parameters parameters further specifying the behaviour
     */
    public AverageCalculationLogicProvider ( final DataType inputDataType, final DataType outputDataType, final long[] parameters )
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
        if ( values.length == 1 )
        {
            return values[0].getValue ();
        }
        long timespan = 0;
        double avgValue = 0;
        LongValue lastValidValue = null;
        for ( final LongValue value : values )
        {
            if ( lastValidValue != null )
            {
                final long weight = value.getTime () - lastValidValue.getTime ();
                avgValue += lastValidValue.getValue () * weight;
                timespan += weight;
            }
            lastValidValue = value.getQualityIndicator () > 0 ? value : null;
        }
        return (long) ( avgValue / timespan );
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateLong
     */
    protected long calculateLong ( final DoubleValue[] values )
    {
        if ( values.length == 1 )
        {
            return (long)values[0].getValue ();
        }
        long timespan = 0;
        double avgValue = 0;
        DoubleValue lastValidValue = null;
        for ( final DoubleValue value : values )
        {
            if ( lastValidValue != null )
            {
                final long weight = value.getTime () - lastValidValue.getTime ();
                avgValue += lastValidValue.getValue () * weight;
                timespan += weight;
            }
            lastValidValue = value.getQualityIndicator () > 0 ? value : null;
        }
        return (long) ( avgValue / timespan );
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateDouble
     */
    protected double calculateDouble ( final LongValue[] values )
    {
        if ( values.length == 1 )
        {
            return values[0].getValue ();
        }
        long timespan = 0;
        double avgValue = 0;
        LongValue lastValidValue = null;
        for ( final LongValue value : values )
        {
            if ( lastValidValue != null )
            {
                final long weight = value.getTime () - lastValidValue.getTime ();
                avgValue += lastValidValue.getValue () * weight;
                timespan += weight;
            }
            lastValidValue = value.getQualityIndicator () > 0 ? value : null;
        }
        return avgValue / timespan;
    }

    /**
     * @see org.openscada.hsdb.calculation.CalculationLogicProviderBase#calculateDouble
     */
    protected double calculateDouble ( final DoubleValue[] values )
    {
        if ( values.length == 1 )
        {
            return values[0].getValue ();
        }
        long timespan = 0;
        double avgValue = 0;
        DoubleValue lastValidValue = null;
        for ( final DoubleValue value : values )
        {
            if ( lastValidValue != null )
            {
                final long weight = value.getTime () - lastValidValue.getTime ();
                avgValue += lastValidValue.getValue () * weight;
                timespan += weight;
            }
            lastValidValue = value.getQualityIndicator () > 0 ? value : null;
        }
        return avgValue / timespan;
    }
}
