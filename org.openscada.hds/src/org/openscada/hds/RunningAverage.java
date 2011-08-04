package org.openscada.hds;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RunningAverage
{
    private double lastValue = Double.NaN;

    private long lastTimestamp;

    private BigDecimal counter;

    private long firstTimestamp;

    public void next ( final double value, final long timestamp )
    {
        increment ( timestamp );

        this.lastValue = value;
        this.lastTimestamp = timestamp;
    }

    private void increment ( final long timestamp )
    {
        if ( !Double.isNaN ( this.lastValue ) )
        {
            final long offset = timestamp - this.lastTimestamp;

            final BigDecimal localCounter = BigDecimal.valueOf ( offset ).multiply ( BigDecimal.valueOf ( this.lastValue ) );

            if ( this.counter != null )
            {
                this.counter = this.counter.add ( localCounter );
            }
            else
            {
                this.counter = localCounter;
            }
        }
    }

    public void step ( final long timestamp )
    {
        this.firstTimestamp = timestamp;
        this.lastTimestamp = timestamp;
        this.counter = null;
    }

    public double getAverage ( final long lastTimestamp )
    {
        increment ( lastTimestamp );
        if ( this.counter == null )
        {
            return Double.NaN;
        }
        else
        {
            return this.counter.divide ( BigDecimal.valueOf ( lastTimestamp - this.firstTimestamp ), RoundingMode.HALF_DOWN ).doubleValue ();
        }
    }
}