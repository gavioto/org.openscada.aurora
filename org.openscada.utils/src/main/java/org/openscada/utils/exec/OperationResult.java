/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.exec;

import java.util.concurrent.TimeUnit;

/**
 * A class that can be used to track the progress of an asynchronous execution
 * 
 * @author jens
 * @param <R>
 */
public class OperationResult<R>
{
    private OperationResultHandler<R> handler = null;

    private R result = null;

    private Exception exception = null;

    private boolean complete = false;

    public OperationResult ()
    {
        this ( null );
    }

    public OperationResult ( final OperationResultHandler<R> handler )
    {
        this.handler = handler;
    }

    public synchronized boolean isComplete ()
    {
        return this.complete;
    }

    public synchronized boolean isSuccess ()
    {
        return this.exception == null;
    }

    public synchronized Exception getException ()
    {
        return this.exception;
    }

    public synchronized R getResult ()
    {
        return this.result;
    }

    public synchronized R get ()
    {
        return getResult ();
    }

    public synchronized void notifySuccess ( final R result )
    {
        if ( this.complete )
        {
            return;
        }

        this.complete = true;
        this.result = result;
        this.exception = null;

        notifyAll ();

        if ( this.handler != null )
        {
            this.handler.success ( result );
        }
    }

    public synchronized void notifyFailure ( final Exception e )
    {
        if ( this.complete )
        {
            return;
        }

        this.complete = true;
        this.result = null;
        this.exception = e;

        notifyAll ();

        if ( this.handler != null )
        {
            this.handler.failure ( e );
        }
    }

    public synchronized void complete () throws InterruptedException
    {
        if ( this.complete )
        {
            return;
        }

        wait ();
    }

    public synchronized boolean complete ( final long timeout, final TimeUnit t ) throws InterruptedException
    {
        if ( this.complete )
        {
            return true;
        }

        wait ( t.toMillis ( timeout ) );

        return this.complete;
    }
}
