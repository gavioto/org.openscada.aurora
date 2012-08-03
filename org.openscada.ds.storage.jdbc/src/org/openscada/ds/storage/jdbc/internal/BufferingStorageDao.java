/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.openscada.ds.DataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BufferingStorageDao implements JdbcStorageDao
{

    private final static Logger logger = LoggerFactory.getLogger ( BufferingStorageDao.class );

    private final JdbcStorageDao targetDao;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock ();

    private final ReadLock readLock = this.lock.readLock ();

    private final WriteLock writeLock = this.lock.writeLock ();

    private final Map<String, DataNode> queueMap = new HashMap<String, DataNode> ();

    private final Map<String, DataNode> writeMap = new HashMap<String, DataNode> ();

    private final Condition writeCondition = this.writeLock.newCondition ();

    private boolean disposed;

    private final Thread writerThread;

    /**
     * Create a new buffering storage DAO
     * <p>
     * Requests will be passed on to the target DAO as necessary. The target DOA will be disposed by this class.
     * </p>
     * 
     * @param targetDao
     *            the target DAO
     */
    public BufferingStorageDao ( final JdbcStorageDao targetDao )
    {
        this.targetDao = targetDao;
        this.writerThread = new Thread ( "BufferingStorageDao" ) {
            @Override
            public void run ()
            {
                writer ();
            }
        };
    }

    @Override
    public DataNode readNode ( final String nodeId )
    {
        try
        {
            this.readLock.lock ();

            if ( this.disposed )
            {
                return null;
            }

            if ( this.queueMap.containsKey ( nodeId ) )
            {
                return this.queueMap.get ( nodeId );
            }
            else if ( this.writeMap.containsKey ( nodeId ) )
            {
                return this.writeMap.get ( nodeId );
            }
            else
            {
                return this.targetDao.readNode ( nodeId );
            }
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    @Override
    public void writeNode ( final DataNode node )
    {
        try
        {
            this.writeLock.lock ();

            if ( this.disposed )
            {
                return;
            }

            this.queueMap.put ( node.getId (), node );

            this.writeCondition.signal ();
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    @Override
    public void deleteNode ( final String nodeId )
    {
        try
        {
            this.writeLock.lock ();
            if ( this.disposed )
            {
                return;
            }

            this.queueMap.put ( nodeId, null );

            this.writeCondition.signal ();
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    protected void writer ()
    {
        logger.info ( "Starting writer" );
        while ( true )
        {
            // transfer from queue to write map
            try
            {
                this.writeLock.lock ();

                // timed wait in order to process re-tries
                this.writeCondition.await ( 1, TimeUnit.MINUTES );

                this.writeMap.putAll ( this.queueMap );
                this.queueMap.clear ();
            }
            catch ( final InterruptedException e )
            {
                // just checking
            }
            finally
            {
                this.writeLock.unlock ();
            }

            // process writes
            final Map<String, DataNode> failMap = performWrites ();

            // remove from write queue
            try
            {
                this.writeLock.lock ();
                this.writeMap.clear ();

                // re-add all failed entries
                this.writeMap.putAll ( failMap );
            }
            finally
            {
                this.writeLock.unlock ();
            }

            // check exit condition
            try
            {
                this.readLock.lock ();

                if ( !this.writeMap.isEmpty () )
                {
                    logger.error ( "Write map still contains {} entries but we are exiting!", this.writeMap.size () );
                }

                if ( this.disposed )
                {
                    logger.info ( "Detected shutdown signal" );
                    // dispose target
                    this.targetDao.dispose ();
                    // exit loop
                    return;
                }
            }
            finally
            {
                this.readLock.unlock ();
            }
        }
    }

    private Map<String, DataNode> performWrites ()
    {
        final Map<String, DataNode> failMap = new HashMap<String, DataNode> ( this.writeMap.size () );

        for ( final Map.Entry<String, DataNode> entry : this.writeMap.entrySet () )
        {
            try
            {
                if ( entry.getValue () == null )
                {
                    this.targetDao.deleteNode ( entry.getKey () );
                }
                else
                {
                    this.targetDao.writeNode ( entry.getValue () );
                }

            }
            catch ( final Exception e )
            {
                failMap.put ( entry.getKey (), entry.getValue () );
                logger.warn ( "Failed to store data node", e );
            }
        }

        return failMap;
    }

    /**
     * This will dispose ourself and the target dao
     */
    @Override
    public void dispose ()
    {
        shutdown ();

        // at the moment we do not wait for the end
        try
        {
            this.writerThread.join ( 5000 );
            if ( this.writerThread.isAlive () )
            {
                logger.warn ( "Writer thread is still alive after 5000ms" );
            }
        }
        catch ( final InterruptedException e )
        {
            // consuming interruption
            logger.warn ( "Failed to wait for end of writer", e );
        }
    }

    private void shutdown ()
    {
        try
        {
            this.writeLock.lock ();
            this.disposed = true;
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }
}
