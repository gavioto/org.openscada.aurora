/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.eclipse.scada.utils.concurrent;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.utils.concurrent.CountingThreadPoolExecutor;
import org.eclipse.scada.utils.concurrent.NamedThreadFactory;
import org.eclipse.scada.utils.concurrent.CountingThreadPoolExecutor.Listener;

public class CountingTest
{
    public static void main ( final String[] args )
    {
        final CountingThreadPoolExecutor exec = new CountingThreadPoolExecutor ( 1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable> (), new NamedThreadFactory ( "Testing" ) );

        exec.addListener ( new Listener () {

            @Override
            public void countChanged ( final int count )
            {
                System.out.println ( "Count: " + count );
            }
        } );

        for ( int i = 0; i < 100; i++ )
        {
            exec.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    System.out.println ( "Test" );
                }
            } );
        }

        System.out.println ( "Before now" );
        exec.shutdownNow ();
        System.out.println ( "After now" );
    }
}
