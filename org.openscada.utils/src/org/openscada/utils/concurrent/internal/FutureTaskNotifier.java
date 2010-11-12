/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.concurrent.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import org.openscada.utils.concurrent.FutureListener;

public class FutureTaskNotifier<T>
{

    private final Object lock = new Object ();

    private final Set<FutureListener<T>> listeners = new HashSet<FutureListener<T>> ();

    private final Set<Runnable> runnables = new HashSet<Runnable> ();

    private boolean done = false;

    private final Future<T> future;

    public FutureTaskNotifier ( final Future<T> future )
    {
        this.future = future;
    }

    public void done ()
    {
        Set<FutureListener<T>> listeners;
        final Set<Runnable> runnables;
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
                listener.complete ( this.future );
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
            listener.complete ( this.future );
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
