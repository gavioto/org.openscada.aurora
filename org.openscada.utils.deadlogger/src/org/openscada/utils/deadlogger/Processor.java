package org.openscada.utils.deadlogger;

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

    public Processor ()
    {
        this.executor = Executors.newSingleThreadScheduledExecutor ( new NamedThreadFactory ( "DeadLockDetector", true ) );
        this.executor.scheduleWithFixedDelay ( new Runnable () {

            @Override
            public void run ()
            {
                detect ();
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS );
    }

    protected void detect ()
    {
        final LocalDetector detector = new LocalDetector ();
        if ( detector.isDeadlock () )
        {
            detector.dump ( System.out );
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
