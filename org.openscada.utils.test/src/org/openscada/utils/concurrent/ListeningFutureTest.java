/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006, 2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.concurrent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ListeningFutureTest
{

    private FutureTestImpl<Object> future;

    @Before
    public void setUp () throws Exception
    {
        this.future = new FutureTestImpl<Object> ();
    }

    @Test ( expected = CancellationException.class )
    public void test1 () throws InterruptedException, ExecutionException
    {
        final boolean rc = this.future.cancel ( true );
        Assert.assertEquals ( "Cancel must return true", true, rc );
        this.future.get ();
    }

    @Test ( expected = ExecutionException.class )
    public void test2 () throws InterruptedException, ExecutionException
    {
        this.future.setError ( new RuntimeException ( "test" ).fillInStackTrace () );
        this.future.get ();
    }

    @Test
    public void test3 () throws InterruptedException, ExecutionException
    {
        final Object result = new Object ();
        this.future.setResult ( result );
        Assert.assertEquals ( "Result does not match", result, this.future.get () );
    }

    @Test
    public void test4 () throws InterruptedException, ExecutionException
    {
        final Object result = new Object ();
        this.future.setResult ( result );
        this.future.setResult ( new Object () );
        Assert.assertEquals ( "Result does not match", result, this.future.get () );
    }

    @Test
    public void test5 () throws InterruptedException, ExecutionException
    {
        final Object result = new Object ();
        this.future.setResult ( result );
        final boolean rc = this.future.cancel ( true );
        Assert.assertEquals ( "Cancel must return false", false, rc );
        Assert.assertEquals ( "Result does not match", result, this.future.get () );
    }

    @Test
    public void test6 () throws InterruptedException, ExecutionException
    {
        final int count = 10;
        final ExecutorService service = Executors.newFixedThreadPool ( count );

        final Collection<Future<Object>> futures = new LinkedList<Future<Object>> ();
        for ( int i = 0; i < count; i++ )
        {
            futures.add ( service.submit ( new Callable<Object> () {

                @Override
                public Object call () throws Exception
                {
                    System.out.println ( "Waiting" );
                    final Object result = ListeningFutureTest.this.future.get ();
                    System.out.println ( "Completed" );
                    return result;
                }
            } ) );
        }

        // set result
        final Object result = new Object ();
        this.future.setResult ( result );

        // shutdown
        service.shutdown ();

        // validate
        for ( final Future<Object> f : futures )
        {
            Assert.assertEquals ( "Result not ok", result, f.get () );
        }
    }
}
