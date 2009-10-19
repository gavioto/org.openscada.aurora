package org.openscada.hsdb.relict;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This task is used to periodically delete old data.
 * @author Ludwig Straub
 */
public class RelictCleanerCallerTask implements Runnable
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( RelictCleanerCallerTask.class );

    /** Object of which old data has to be deleted. */
    private final RelictCleaner relictCleaner;

    /**
     * Constructor.
     * @param relictCleaner Object of which old data has to be deleted
     */
    public RelictCleanerCallerTask ( final RelictCleaner relictCleaner )
    {
        this.relictCleaner = relictCleaner;
    }

    /**
     * This method performs the cleaning actions.
     */
    public void run ()
    {
        try
        {
            relictCleaner.cleanupRelicts ();
        }
        catch ( final Exception e )
        {
            logger.warn ( "error while cleaning relicts", e );
        }
    }
}
