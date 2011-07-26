package org.openscada.utils.deadlogger;

public class Test1
{
    public static void main ( final String[] args )
    {
        MakeDeadlock.makeDeadlock ();

        new JmxDetector ().dump ( System.err );
    }
}
