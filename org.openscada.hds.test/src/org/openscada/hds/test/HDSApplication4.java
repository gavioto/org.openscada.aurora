package org.openscada.hds.test;

import org.openscada.hds.RunningAverage;

public class HDSApplication4
{
    public static void main ( final String[] args )
    {
        RunningAverage avg = new RunningAverage ();

        avg.next ( 0.0, 0 );
        avg.step ( 100 );
        avg.next ( 1.0, 200 );
        avg.next ( 2.0, 300 );
        avg.step ( 400 );
        System.out.println ( avg.getAverage ( 500 ) );

        avg = new RunningAverage ();
        avg.step ( 100 );
        avg.next ( 1.0, 200 );
        System.out.println ( avg.getAverage ( 300 ) );
    }
}
