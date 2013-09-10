/*******************************************************************************
 * Copyright (c) 2011 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.utils.deadlogger;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;

public class Application
{
    public static void main ( final String[] args ) throws FileNotFoundException
    {
        if ( args.length == 0 )
        {
            System.out.println ( Application.class.getName () + " <output> [<host>:<port>] [[[<host>:<port>] ...]" );
            return;
        }

        PrintStream out;
        final String output = args[0];
        if ( "-".equals ( output ) )
        {
            out = System.out;
        }
        else if ( "=".equals ( output ) )
        {
            out = System.err;
        }
        else
        {
            out = new PrintStream ( output );
        }

        final Collection<Detector> detectors = new LinkedList<Detector> ();

        for ( int i = 1; i < args.length; i++ )
        {
            final String target = args[i];
            if ( target.isEmpty () || target.equals ( "-" ) )
            {
                detectors.add ( new JmxDetector () );
            }
            else
            {
                final String[] toks = target.split ( ":" );
                if ( toks.length == 2 )
                {
                    detectors.add ( new JmxDetector ( toks[0], Integer.parseInt ( toks[1] ) ) );
                }
                else
                {
                    throw new IllegalArgumentException ( String.format ( "Invalid syntax of target: '%s'", target ) );
                }
            }
        }

        out.println ( String.format ( " = %1$tF %1$tT.%1$tL - Starting detector", Calendar.getInstance () ) );

        new Processor ( detectors, Integer.getInteger ( "period", 10000 ), out );
        while ( true )
        {
            try
            {
                Thread.sleep ( 10 * 60 * 1000 );
                out.println ( String.format ( " = %1$tF %1$tT.%1$tL - Still alive", Calendar.getInstance () ) );
            }
            catch ( final InterruptedException e )
            {
                e.printStackTrace ( out );
            }
        }
    }
}
