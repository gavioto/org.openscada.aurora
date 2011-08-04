package org.openscada.hds.test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.openscada.hds.DataFileAccessor;
import org.openscada.hds.DataFileAccessorImpl;
import org.openscada.hds.ValueVisitor;

public class HDSApplication1
{
    public static void main ( final String[] args ) throws Exception
    {
        final Calendar start = Calendar.getInstance ();
        start.set ( Calendar.MILLISECOND, 0 );
        start.set ( Calendar.SECOND, 0 );
        start.set ( Calendar.MINUTE, 0 );
        start.set ( Calendar.HOUR_OF_DAY, 0 );

        final Calendar end = (Calendar)start.clone ();
        end.add ( Calendar.DAY_OF_MONTH, 1 );

        final File path = new File ( "testdata" );
        path.mkdir ();

        final File file = new File ( path, "file1.hds" );

        file.delete ();

        final DataFileAccessor writer = DataFileAccessorImpl.create ( file, start.getTime (), end.getTime () );

        for ( int i = 0; i < 20; i++ )
        {
            writer.insertValue ( i, new Date (), false, false, false );
        }

        writer.visit ( new ValueVisitor () {

            @Override
            public boolean value ( final double value, final Date date, final boolean error, final boolean manual )
            {
                System.out.println ( String.format ( "Value: %s, Timestamp: %tc, Error: %s, Manual: %s", value, date, error, manual ) );
                return true;
            }
        } );

        writer.dispose ();
        file.delete ();
    }
}
