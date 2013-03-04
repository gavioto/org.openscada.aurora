package org.openscada.utils.lang;

import org.junit.Test;

public class BitArrayTest
{
    @Test
    public void test1 ()
    {
        final BitArray ba = new BitArray ();

        ba.set ( 0, true );
        ba.set ( 1, true );
        ba.set ( 8, true );
        System.out.println ( ba );
        ba.set ( 1, false );
        ba.set ( 8, false );
        System.out.println ( ba );
    }
}
