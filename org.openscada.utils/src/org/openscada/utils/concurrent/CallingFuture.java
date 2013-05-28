/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.utils.concurrent;

import java.util.concurrent.Future;

/**
 * A future handler which calls a method and passes on the result to itself,
 * implementing another future.
 * 
 * @since 1.1
 */
public abstract class CallingFuture<T, T2> extends AbstractFuture<T2>
{
    private final NotifyFuture<T> future;

    public CallingFuture ( final NotifyFuture<T> future )
    {
        this.future = future;
        this.future.addListener ( new FutureListener<T> () {
            @Override
            public void complete ( final Future<T> future )
            {
                handleComplete ( future );
            }
        } );
    }

    public abstract T2 call ( final Future<T> future ) throws Exception;

    protected void handleComplete ( final Future<T> future )
    {
        try
        {
            setResult ( call ( future ) );
        }
        catch ( final Exception e )
        {
            setError ( e );
        }
    }

    @Override
    public boolean cancel ( final boolean mayInterruptIfRunning )
    {
        this.future.cancel ( mayInterruptIfRunning );
        return super.cancel ( mayInterruptIfRunning );
    }
}
