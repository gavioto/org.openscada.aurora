/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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
