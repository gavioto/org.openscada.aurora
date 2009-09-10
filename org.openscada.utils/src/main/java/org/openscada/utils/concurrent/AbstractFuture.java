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

package org.openscada.utils.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openscada.utils.concurrent.internal.FutureTaskNotifier;

public abstract class AbstractFuture<T> implements NotifyFuture<T>
{
    private final FutureTaskNotifier<T> notifier;

    private volatile Throwable error;

    private volatile T result;

    private enum State
    {
        WAITING,
        COMPLETE,
        CANCELED
    };

    private volatile State state = State.WAITING;

    private final Semaphore lock;

    private final Object setLock;

    public AbstractFuture ()
    {
        this.notifier = new FutureTaskNotifier<T> ( this );
        this.lock = new Semaphore ( -1 );
        this.setLock = new Object ();
    }

    public void addListener ( final FutureListener<T> listener )
    {
        this.notifier.addListener ( listener );
    }

    public void addListener ( final Runnable listener )
    {
        this.notifier.addListener ( listener );
    }

    public void removeListener ( final FutureListener<T> listener )
    {
        this.notifier.removeListener ( listener );
    }

    public void removeListener ( final Runnable listener )
    {
        this.notifier.removeListener ( listener );
    }

    protected void setResult ( final T result )
    {
        synchronized ( this.setLock )
        {
            if ( isDone () )
            {
                return;
            }
            this.result = result;
            this.state = State.COMPLETE;

            // release the wait lock
            this.lock.release ( 2 );
        }

        this.notifier.done ();
    }

    protected void setError ( final Throwable error )
    {
        synchronized ( this.setLock )
        {
            if ( isDone () )
            {
                return;
            }
            this.error = error;
            this.state = State.COMPLETE;

            // release the wait lock
            this.lock.release ( 2 );
        }

        this.notifier.done ();
    }

    public boolean cancel ( final boolean mayInterruptIfRunning )
    {
        synchronized ( this.setLock )
        {
            if ( isDone () )
            {
                return false;
            }
            this.state = State.CANCELED;

            // release the wait lock
            this.lock.release ( 2 );
        }

        this.notifier.done ();

        return true;
    }

    public T get () throws InterruptedException, ExecutionException
    {
        this.lock.acquire ( 0 );
        return fetchResult ();
    }

    protected T fetchResult () throws ExecutionException
    {
        if ( isCancelled () )
        {
            throw new CancellationException ();
        }
        if ( this.error != null )
        {
            throw new ExecutionException ( this.error );
        }

        return this.result;
    }

    public T get ( final long timeout, final TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException
    {
        if ( this.lock.tryAcquire ( 0, timeout, unit ) )
        {
            throw new TimeoutException ();
        }
        return fetchResult ();
    }

    public boolean isCancelled ()
    {
        return this.state == State.CANCELED;
    }

    public boolean isDone ()
    {
        final State state = this.state;
        return state == State.CANCELED || state == State.COMPLETE;
    }
}
