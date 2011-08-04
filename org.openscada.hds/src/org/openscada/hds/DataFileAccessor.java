package org.openscada.hds;

import java.io.IOException;
import java.util.Date;

public interface DataFileAccessor
{

    public abstract void insertValue ( final double value, final Date date, final boolean error, final boolean manual, final boolean heartbeat ) throws IOException;

    public abstract boolean visit ( final ValueVisitor visitor ) throws Exception;

    public abstract void dispose ();

    public abstract boolean visitFirstValue ( ValueVisitor visitor ) throws Exception;

    /**
     * Forward correct entries
     * <p>
     * </p>
     * @param value the value
     * @param date the starting point
     * @param error the error flag
     * @param manual the manual flag
     * @throws Exception 
     */
    public abstract void forwardCorrect ( double value, Date date, boolean error, boolean manual ) throws Exception;

}