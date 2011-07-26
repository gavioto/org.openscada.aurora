package org.openscada.utils.deadlogger;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalDetector implements Detector
{

    private final static Logger logger = LoggerFactory.getLogger ( LocalDetector.class );

    @Override
    public boolean isDeadlock ()
    {
        logger.info ( "Checking for deadlocks" );

        return false;
    }

    @Override
    public void dump ( final PrintStream out )
    {
        if ( !isDeadlock () )
        {
            out.println ( "No deadlock detected" );
            return;
        }
    }

}
