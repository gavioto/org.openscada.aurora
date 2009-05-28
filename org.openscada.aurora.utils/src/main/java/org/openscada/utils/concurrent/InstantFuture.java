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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A future that never has to run since the result is already known
 * @author Jens Reimann
 *
 * @param <T> the type of the future
 */
public class InstantFuture<T> implements NotifyFuture<T>
{
    private final T value;

    public InstantFuture ( final T value )
    {
        this.value = value;
    }

    public boolean cancel ( final boolean mayInterruptIfRunning )
    {
        return false;
    }

    public T get () throws InterruptedException, ExecutionException
    {
        return this.value;
    }

    public T get ( final long timeout, final TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException
    {
        return this.value;
    }

    public boolean isCancelled ()
    {
        return false;
    }

    public boolean isDone ()
    {
        return true;
    }

    public void addListener ( final FutureListener<T> listener )
    {
        // we can simple trigger the listener
        listener.complete ( this );
    }

    public void removeListener ( final FutureListener<T> listener )
    {
        // nothing to do
    }
}