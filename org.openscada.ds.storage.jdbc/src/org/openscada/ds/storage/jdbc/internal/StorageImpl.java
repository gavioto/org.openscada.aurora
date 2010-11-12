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

package org.openscada.ds.storage.jdbc.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openscada.ds.DataNode;
import org.openscada.ds.storage.AbstractStorage;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageImpl extends AbstractStorage
{

    private final static Logger logger = LoggerFactory.getLogger ( StorageImpl.class );

    private JdbcStorageDAO storage;

    public void setStorage ( final JdbcStorageDAO storage )
    {
        this.storage = storage;
    }

    public StorageImpl ()
    {
        super ( Executors.newSingleThreadExecutor ( new NamedThreadFactory ( StorageImpl.class.getName () ) ) );
    }

    @Override
    public void dispose ()
    {
        super.dispose ();

        final Executor executor = this.executor;

        if ( executor instanceof ExecutorService )
        {
            ( (ExecutorService)executor ).shutdown ();
        }
    }

    @Override
    public synchronized NotifyFuture<DataNode> readNode ( final String nodeId )
    {
        try
        {
            final FutureTask<DataNode> task = new FutureTask<DataNode> ( new Callable<DataNode> () {

                public DataNode call () throws Exception
                {
                    return StorageImpl.this.storage.readNode ( nodeId );
                }
            } );

            this.executor.execute ( task );
            return task;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to load data node", e );
            return new InstantErrorFuture<DataNode> ( e );
        }
    }

    public synchronized NotifyFuture<Void> writeNode ( final DataNode node )
    {
        try
        {
            final FutureTask<Void> task = new FutureTask<Void> ( new Callable<Void> () {

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

            this.executor.execute ( task );
            return task;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to write data node", e );
            return new InstantErrorFuture<Void> ( e );
        }
    }

    public synchronized NotifyFuture<Void> deleteNode ( final String nodeId )
    {
        try
        {
            final FutureTask<Void> task = new FutureTask<Void> ( new Callable<Void> () {

                public Void call () throws Exception
                {
                    StorageImpl.this.storage.deleteNode ( nodeId );
                    return null;
                }
            } );

            this.executor.execute ( task );
            return task;
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to write data node", e );
            return new InstantErrorFuture<Void> ( e );
        }
    }
}
