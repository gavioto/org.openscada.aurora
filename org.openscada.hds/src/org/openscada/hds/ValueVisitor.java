package org.openscada.hds;

import java.util.Date;

public interface ValueVisitor
{
    /**
     * Gets called for each record found
     * @param value the value
     * @param date the timestamp of the value
     * @param error the error flag for the value
     * @param manual the manual flag for the value
     * @return <code>true</code> if the callee wants more data, <code>false</code> otherwise
     */
    public boolean value ( double value, Date date, boolean error, boolean manual );
}
