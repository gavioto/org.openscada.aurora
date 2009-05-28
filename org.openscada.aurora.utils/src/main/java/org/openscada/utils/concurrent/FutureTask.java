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

    private final Set<FutureListener<T>> listeners = new HashSet<FutureListener<T>> ();

    private boolean done = false;

    @Override
    protected void done ()
    {
        super.done ();

        Set<FutureListener<T>> listeners;
        synchronized ( this.listeners )
        {
            if ( this.done )
            {
                return;
            }

            this.done = true;
            listeners = new HashSet<FutureListener<T>> ( this.listeners );
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

        // just clean up
        this.listeners.clear ();
    }

    public void addListener ( final FutureListener<T> listener )
    {
        boolean notifyNow = false;
        synchronized ( this.listeners )
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
        synchronized ( this.listeners )
        {
            if ( this.done )
            {
                return;
            }
            this.listeners.remove ( listener );
        }
    }

}
