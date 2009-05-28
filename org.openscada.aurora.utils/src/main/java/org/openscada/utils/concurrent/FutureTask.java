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
