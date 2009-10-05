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

import java.util.concurrent.Callable;

import org.openscada.utils.concurrent.internal.FutureTaskNotifier;

public class FutureTask<T> extends java.util.concurrent.FutureTask<T> implements NotifyFuture<T>
{

    private FutureTaskNotifier<T> notifier;

    public FutureTask ( final Callable<T> callable )
    {
        super ( callable );
        this.notifier = new FutureTaskNotifier<T> ( this );
    }

    public FutureTask ( final Runnable runnable, final T result )
    {
        super ( runnable, result );
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

    @Override
    protected void done ()
    {
        this.notifier.done ();
    }
}
