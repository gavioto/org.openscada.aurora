package org.openscada.hsdb.concurrent;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This task is used to call a runnable within a timer task.
 * @author Ludwig Straub
 */
public class RunnableTimerTask extends TimerTask
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( RunnableTimerTask.class );

    /** Runnable that has to be executed. */
    private final Runnable runnable;

    /**
     * Constructor.
     * @param runnable runnable that has to be executed
     */
    public RunnableTimerTask ( final Runnable runnable )
    {
        this.runnable = runnable;
    }

    /**
     * This method performs the cleaning actions.
     */
    public void run ()
    {
        try
        {
            runnable.run ();
        }
        catch ( final Exception e )
        {
            logger.warn ( "error while cleaning relicts", e );
        }
    }
}
