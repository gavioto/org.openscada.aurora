/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.utils.concurrent;

import java.util.Iterator;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;

/**
 * @since 1.1
 * @author Jens Reimann
 * @param <T>
 *            the type of the future
 * @param <S>
 *            the type of the list
 */
public abstract class IteratingFuture<T, S> extends AbstractFuture<T>
{
    public final class FutureListenerImpl implements FutureListener<T>
    {
        private final S current;

        public FutureListenerImpl ( final S current )
        {
            this.current = current;
        }

        @Override
        public void complete ( final Future<T> future )
        {
            try
            {
                doComplete ( future, this.current );
            }
            catch ( final Exception e )
            {
                setError ( e );
            }
        }
    }

    private final Iterator<? extends S> iterator;

    private S current;

    private volatile NotifyFuture<T> currentFuture;

    private boolean canceled;

    public IteratingFuture ( final Iterable<? extends S> iterable )
    {
        this.iterator = iterable.iterator ();
    }

    /**
     * Start iterating over the list
     * 
     * @return this instance
     */
    public IteratingFuture<T, S> startIterating ()
    {
        processNext ();
        return this;
    }

    protected void processCurrent ()
    {
        try
        {
            process ( perform ( this.current ) );
        }
        catch ( final Exception e )
        {
            setError ( e );
        }
    }

    protected void processNext ()
    {
        try
        {
            process ( next () );
        }
        catch ( final Exception e )
        {
            setError ( e );
        }
    }

    private void process ( final NotifyFuture<T> future )
    {
        synchronized ( this )
        {
            if ( this.canceled )
            {
                future.cancel ( true );
                throw new CancellationException ();
            }
            else
            {
                this.currentFuture = future;
            }
        }

        future.addListener ( new FutureListenerImpl ( this.current ) );
    }

    @Override
    public boolean cancel ( final boolean mayInterruptIfRunning )
    {
        final NotifyFuture<T> current;

        synchronized ( this )
        {
            this.canceled = true;
            current = this.currentFuture;
        }

        if ( current != null )
        {
            current.cancel ( mayInterruptIfRunning );
        }

        return super.cancel ( mayInterruptIfRunning );
    }

    private NotifyFuture<T> next ()
    {
        if ( this.iterator.hasNext () )
        {
            this.current = this.iterator.next ();
            return perform ( this.current );
        }
        else
        {
            this.current = null;
            return last ();
        }
    }

    protected void doComplete ( final Future<T> future, final S current ) throws Exception
    {
        this.currentFuture = null;
        handleComplete ( future, current );
    }

    protected abstract void handleComplete ( final Future<T> future, S current ) throws Exception;

    protected abstract NotifyFuture<T> perform ( S s );

    protected abstract NotifyFuture<T> last ();

}
