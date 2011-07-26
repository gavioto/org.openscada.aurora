package org.openscada.utils.deadlogger;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openscada.utils.concurrent.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Processor
{

    private final static Logger logger = LoggerFactory.getLogger ( Processor.class );

    private volatile ScheduledExecutorService executor;

    private final ArrayList<Detector> detectors;

    private final PrintStream out;

    public Processor ( final Collection<Detector> detectors, final int milliseconds, final PrintStream out )
    {
        this.detectors = new ArrayList<Detector> ( detectors );
        this.out = out;

        if ( !detectors.isEmpty () )
        {
            this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "DeadLockDetector", true ) );
            this.executor.scheduleWithFixedDelay ( new Runnable () {

                @Override
                public void run ()
                {
                    detect ();
                }
            }, 0, milliseconds, TimeUnit.MILLISECONDS );
        }
    }

    protected void detect ()
    {
        logger.debug ( "Checking for deadlocks" );
        try
        {
            for ( final Detector detector : this.detectors )
            {
                if ( detector.isDeadlock () )
                {
                    detector.dump ( this.out );
                }
            }
        }
        finally
        {
            this.out.flush ();
        }
    }

    public void dispose ()
    {
        final ExecutorService executor = this.executor;
        this.executor = null;
        if ( executor != null )
        {
            // no problem shutting down twice
            executor.shutdown ();
        }
    }
}
