package org.openscada.hsdb.datatypes;

/**
 * This class handles a long value for being storaged in a storage channel.
 * @author Ludwig Straub
 */
public class LongValue extends BaseValue
{
    /** Value to be handled. */
    private long value;

    /**
     * Fully initializing constructor.
     * @param time time stamp of the data
     * @param qualityIndicator quality information of the data
     * @param manualIndicator percentage of manually set values during the value time
     * @param baseValueCount count of values that have been combined to get the current value
     * @param value value to be handled
     */
    public LongValue ( final long time, final double qualityIndicator, final double manualIndicator, final long baseValueCount, final long value )
    {
        super ( time, qualityIndicator, manualIndicator, baseValueCount );
        this.value = value;
    }

    /**
     * This method returns the value to be handled.
     * @return value to be handled
     */
    public long getValue ()
    {
        return value;
    }

    /**
     * This method sets the value to be handled.
     * @param value value to be handled
     */
    public void setValue ( final long value )
    {
        this.value = value;
    }

    /**
     * @see java.lang.Object#equals
     */
    public boolean equals ( final Object baseValue )
    {
        return ( baseValue instanceof LongValue ) && super.equals ( baseValue ) && ( value == ( (LongValue)baseValue ).getValue () );
    }
}
