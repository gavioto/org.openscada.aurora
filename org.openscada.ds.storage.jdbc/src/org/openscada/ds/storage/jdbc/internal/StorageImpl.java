/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ds.storage.jdbc.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.openscada.ds.DataNode;
import org.openscada.ds.storage.AbstractStorage;
import org.openscada.utils.concurrent.ExecutorServiceExporterImpl;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageImpl extends AbstractStorage
{
    private final static Logger logger = LoggerFactory.getLogger ( StorageImpl.class );

    private final JdbcStorageDAO storage;

    private final ExecutorService executorService;

    private final ExecutorServiceExporterImpl executorExporter;

    public StorageImpl ( final JdbcStorageDAO storage )
    {
        this.executorService = new ThreadPoolExecutor ( 1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable> (), new NamedThreadFactory ( StorageImpl.class.getName () ) );
        this.executorExporter = new ExecutorServiceExporterImpl ( this.executorService, StorageImpl.class.getName () );

        this.storage = storage;
    }

    @Override
    protected Executor getExecutor ()
    {
        return this.executorService;
    }

    @Override
    public synchronized void dispose ()
    {
        super.dispose ();

        this.executorExporter.dispose ();

        this.executorService.shutdown ();
        this.storage.dispose ();
    }

    @Override
    public synchronized NotifyFuture<DataNode> readNode ( final String nodeId )
    {
        try
        {
            final FutureTask<DataNode> task = new FutureTask<DataNode> ( new Callable<DataNode> () {

                @Override
                public DataNode call () throws Exception
                {
                    return StorageImpl.this.storage.readNode ( nodeId );
                }
            } );

            this.executorService.execute ( task );
            return task;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to load data node", e );
            return new InstantErrorFuture<DataNode> ( e );
        }
    }

    @Override
    public synchronized NotifyFuture<Void> writeNode ( final DataNode node )
    {
        try
        {
            final FutureTask<Void> task = new FutureTask<Void> ( new Callable<Void> () {

                @Override
                public Void call () throws Exception
                {
                    try
                    {
                        StorageImpl.this.storage.writeNode ( node );
                        fireUpdate ( node );
                    }
                    catch ( final Exception e )
                    {
                        logger.warn ( "Failed to write node", e );
                        throw new RuntimeException ( "Failed to write node", e );
                    }
                    return null;
                }
            } );

            this.executorService.execute ( task );
            return task;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to write data node", e );
            return new InstantErrorFuture<Void> ( e );
        }
    }

    @Override
    public synchronized NotifyFuture<Void> deleteNode ( final String nodeId )
    {
        try
        {
            final FutureTask<Void> task = new FutureTask<Void> ( new Callable<Void> () {

                @Override
                public Void call () throws Exception
                {
                    StorageImpl.this.storage.deleteNode ( nodeId );
                    return null;
                }
            } );

            this.executorService.execute ( task );
            return task;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to write data node", e );
            return new InstantErrorFuture<Void> ( e );
        }
    }
}
