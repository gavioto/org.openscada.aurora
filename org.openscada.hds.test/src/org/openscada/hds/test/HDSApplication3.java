package org.openscada.hds.test;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openscada.hds.DataFilePool;
import org.openscada.hds.DataStoreAccesor;
import org.openscada.hds.ValueVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class HDSApplication3
{

    private final static Logger logger = LoggerFactory.getLogger ( HDSApplication3.class );

    public static void main ( final String[] args ) throws Exception
    {
        final File base = new File ( "base", "data1" );

        Files.deleteRecursively ( base.getParentFile () );
        base.getParentFile ().mkdir ();

        final DataFilePool pool = new DataFilePool ( 10000 );
        final DataStoreAccesor accessor = DataStoreAccesor.create ( base, 1, TimeUnit.SECONDS, 90, pool );
        final Date start = new Date ();
        for ( int i = 0; i < 100; i++ )
        {
            try
            {
                accessor.insertValue ( i, new Date ( start.getTime () - 900 * i ), false, false );
            }
            catch ( final Exception e )
            {
                logger.debug ( "Failed to write", e );
            }
        }

        accessor.visit ( new ValueVisitor () {

            @Override
            public boolean value ( final double value, final Date date, final boolean error, final boolean manual )
            {
                System.out.println ( String.format ( "Value: %s, Timestamp: %tc, Error: %s, Manual: %s", value, date, error, manual ) );
                return true;
            }
        }, new Date ( start.getTime () - 600 * 100 ), new Date ( start.getTime () + 400 * 100 ) );

        accessor.dispose ();
        pool.dispose ();
    }
}
