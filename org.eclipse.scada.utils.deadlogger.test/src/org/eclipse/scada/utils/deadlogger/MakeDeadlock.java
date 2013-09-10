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

public class MakeDeadlock
{

    public static class DeadRunner implements Runnable
    {
        private final Object l1;

        private final Object l2;

        private final Object l3 = new Object ();

        public DeadRunner ( final Object l1, final Object l2 )
        {
            this.l1 = l1;
            this.l2 = l2;
        }

        @Override
        public synchronized void run ()
        {
            lock1 ();
        }

        protected void lock1 ()
        {
            synchronized ( this.l3 )
            {
                synchronized ( this.l1 )
                {
                    try
                    {
                        Thread.sleep ( 500 );
                    }
                    catch ( final InterruptedException e )
                    {
                        e.printStackTrace ();
                    }
                    lock2 ();
                }
            }
        }

        protected void lock2 ()
        {
            synchronized ( this.l2 )
            {
                System.out.println ( "Here: " + DeadRunner.this );
            }
        }
    }

    public static void makeDeadlock ()
    {
        final Object l1 = new Object ();
        final Object l2 = new Object ();

        final Thread t1 = new Thread ( new DeadRunner ( l1, l2 ) );
        t1.setDaemon ( true );

        final Thread t2 = new Thread ( new DeadRunner ( l2, l1 ) );
        t2.setDaemon ( true );

        t1.start ();
        t2.start ();

        try
        {
            Thread.sleep ( 1000 );
        }
        catch ( final InterruptedException e )
        {
            e.printStackTrace ();
        }

        System.out.println ( "Bang" );
    }
}
