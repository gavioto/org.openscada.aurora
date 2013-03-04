/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ScheduledExportedExecutorService implements ScheduledExecutorService
{
    private final ScheduledExecutorService executor;

    private final ExecutorServiceExporterImpl executorExporter;

    public ScheduledExportedExecutorService ( final String name, final int corePoolSize )
    {
        this.executor = new ScheduledThreadPoolExecutor ( corePoolSize, new NamedThreadFactory ( name ) );
        this.executorExporter = new ExecutorServiceExporterImpl ( this.executor, name );
    }

    @Override
    public void execute ( final Runnable command )
    {
        this.executor.execute ( command );
    }

    @Override
    public void shutdown ()
    {
        this.executorExporter.dispose ();
        this.executor.shutdown ();
    }

    @Override
    protected void finalize () throws Throwable
    {
        shutdown ();
        super.finalize ();
    }

    @Override
    public List<Runnable> shutdownNow ()
    {
        return this.executor.shutdownNow ();
    }

    @Override
    public boolean isShutdown ()
    {
        return this.executor.isShutdown ();
    }

    @Override
    public boolean isTerminated ()
    {
        return this.executor.isTerminated ();
    }

    @Override
    public boolean awaitTermination ( final long timeout, final TimeUnit unit ) throws InterruptedException
    {
        return this.executor.awaitTermination ( timeout, unit );
    }

    @Override
    public <T> Future<T> submit ( final Callable<T> task )
    {
        return this.executor.submit ( task );
    }

    @Override
    public <T> Future<T> submit ( final Runnable task, final T result )
    {
        return this.executor.submit ( task, result );
    }

    @Override
    public Future<?> submit ( final Runnable task )
    {
        return this.executor.submit ( task );
    }

    @Override
    public <T> List<Future<T>> invokeAll ( final Collection<? extends Callable<T>> tasks ) throws InterruptedException
    {
        return this.executor.invokeAll ( tasks );
    }

    @Override
    public <T> List<Future<T>> invokeAll ( final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit ) throws InterruptedException
    {
        return this.executor.invokeAll ( tasks, timeout, unit );
    }

    @Override
    public <T> T invokeAny ( final Collection<? extends Callable<T>> tasks ) throws InterruptedException, ExecutionException
    {
        return this.executor.invokeAny ( tasks );
    }

    @Override
    public <T> T invokeAny ( final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException
    {
        return this.executor.invokeAny ( tasks, timeout, unit );
    }

    @Override
    public ScheduledFuture<?> schedule ( final Runnable command, final long delay, final TimeUnit unit )
    {
        return this.executor.schedule ( command, delay, unit );
    }

    @Override
    public <V> ScheduledFuture<V> schedule ( final Callable<V> callable, final long delay, final TimeUnit unit )
    {
        return this.executor.schedule ( callable, delay, unit );
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate ( final Runnable command, final long initialDelay, final long period, final TimeUnit unit )
    {
        return this.executor.scheduleAtFixedRate ( command, initialDelay, period, unit );
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay ( final Runnable command, final long initialDelay, final long delay, final TimeUnit unit )
    {
        return this.executor.scheduleWithFixedDelay ( command, initialDelay, delay, unit );
    }
}
