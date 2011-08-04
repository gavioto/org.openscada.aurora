package org.openscada.hds.test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openscada.hds.Quantizer;

public class HDSApplication2
{
    public static void main ( final String[] args )
    {
        final Quantizer q = new Quantizer ( 1, TimeUnit.SECONDS );

        test ( q, 500 );
        test ( q, 1000 );
        test ( q, 1500 );
    }

    private static void test ( final Quantizer q, final int i )
    {
        final Date timestamp = new Date ( i );
        final Date start = q.getStart ( timestamp );
        System.out.println ( String.format ( "%tc -> %tc", timestamp, start ) );
    }
}
