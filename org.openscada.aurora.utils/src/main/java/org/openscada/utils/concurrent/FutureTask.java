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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class FutureTask<T> extends java.util.concurrent.FutureTask<T> implements NotifyFuture<T>
{

    public FutureTask ( final Callable<T> callable )
    {
        super ( callable );
    }

    public FutureTask ( final Runnable runnable, final T result )
    {
        super ( runnable, result );
    }

    private final Object lock = new Object ();

    private final Set<FutureListener<T>> listeners = new HashSet<FutureListener<T>> ();

    private final Set<Runnable> runnables = new HashSet<Runnable> ();

    private boolean done = false;

    @Override
    protected void done ()
    {
        super.done ();

        Set<FutureListener<T>> listeners;
        Set<Runnable> runnables;
        synchronized ( this.lock )
        {
            if ( this.done )
            {
                return;
            }

            this.done = true;
            listeners = new HashSet<FutureListener<T>> ( this.listeners );
            runnables = new HashSet<Runnable> ( this.runnables );
        }

        // notify
        for ( final FutureListener<T> listener : listeners )
        {
            try
            {
                listener.complete ( this );
            }
            catch ( final Throwable e )
            {
            }
        }
        for ( final Runnable runnable : runnables )
        {
            try
            {
                runnable.run ();
            }
            catch ( final Throwable e )
            {
            }
        }

        // just clean up
        this.listeners.clear ();
        this.runnables.clear ();
    }

    public void addListener ( final Runnable listener )
    {
        boolean notifyNow = false;
        synchronized ( this.lock )
        {
            if ( this.done )
            {
                notifyNow = true;
            }
            else
            {
                this.runnables.add ( listener );
            }
        }

        if ( notifyNow )
        {
            listener.run ();
        }
    }

    public void removeListener ( final Runnable listener )
    {
        synchronized ( this.lock )
        {
            this.runnables.remove ( listener );
        }
    }

    public void addListener ( final FutureListener<T> listener )
    {
        boolean notifyNow = false;
        synchronized ( this.lock )
        {
            if ( this.done )
            {
                notifyNow = true;
            }
            else
            {
                this.listeners.add ( listener );
            }
        }

        if ( notifyNow )
        {
            listener.complete ( this );
        }
    }

    public void removeListener ( final FutureListener<T> listener )
    {
        synchronized ( this.lock )
        {
            this.listeners.remove ( listener );
        }
    }

}
