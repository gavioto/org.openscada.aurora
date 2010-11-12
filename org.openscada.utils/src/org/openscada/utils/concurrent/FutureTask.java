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

package org.openscada.utils.concurrent;

import java.util.concurrent.Callable;

import org.openscada.utils.concurrent.internal.FutureTaskNotifier;

public class FutureTask<T> extends java.util.concurrent.FutureTask<T> implements NotifyFuture<T>
{

    private final FutureTaskNotifier<T> notifier;

    public FutureTask ( final Callable<T> callable )
    {
        super ( callable );
        this.notifier = new FutureTaskNotifier<T> ( this );
    }

    public FutureTask ( final Runnable runnable, final T result )
    {
        super ( runnable, result );
        this.notifier = new FutureTaskNotifier<T> ( this );
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
