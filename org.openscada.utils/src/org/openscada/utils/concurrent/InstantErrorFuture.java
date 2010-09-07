/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A future that never has to run since the result is already known
 * @author Jens Reimann
 *
 * @param <T> the type of the future
 */
public class InstantErrorFuture<T> extends InstantFutureBase<T>
{
    private final Throwable error;

    public InstantErrorFuture ( final Throwable error )
    {
        this.error = error;
    }

    public T get () throws InterruptedException, ExecutionException
    {
        throw new ExecutionException ( this.error );
    }

    public T get ( final long timeout, final TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException
    {
        throw new ExecutionException ( this.error );
    }
}