package org.openscada.utils.deadlogger;

import java.io.PrintStream;

public interface Detector
{

    public void dump ( final PrintStream out );

    public boolean isDeadlock ();

}
