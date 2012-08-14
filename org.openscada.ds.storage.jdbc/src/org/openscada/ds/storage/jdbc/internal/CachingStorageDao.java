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

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.ds.DataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class CachingStorageDao implements JdbcStorageDao
{

    private final static Logger logger = LoggerFactory.getLogger ( CachingStorageDao.class );

    private final JdbcStorageDao targetDao;

    private final LoadingCache<String, DataNode> cache;

    private final ReadWriteLock lock = new ReentrantReadWriteLock ();

    private final Lock readLock = this.lock.readLock ();

    private final Lock writeLock = this.lock.writeLock ();

    public CachingStorageDao ( final JdbcStorageDao targetDao, final long expireTime, final ScheduledExecutorService scheduler )
    {
        logger.trace ( "cache expiry set to {} seconds", expireTime );
        this.targetDao = targetDao;
        this.cache = CacheBuilder.newBuilder ().expireAfterAccess ( expireTime, TimeUnit.SECONDS ).removalListener ( new RemovalListener<String, DataNode> () {
            @Override
            public void onRemoval ( final RemovalNotification<String, DataNode> notification )
            {
                logger.trace ( "remove from cache node with id {}", notification.getKey () );
            }
        } ).build ( new CacheLoader<String, DataNode> () {
            @Override
            public DataNode load ( final String nodeId ) throws Exception
            {
                logger.trace ( "load single node with id {}", nodeId );
                return targetDao.readNode ( nodeId );
            }
        } );
        // preload cache
        logger.trace ( "preload cache" );
        fillCache ();
        scheduler.schedule ( new Runnable() {
            @Override
            public void run ()
            {
                cache.cleanUp ();
            }
        }, 1l, TimeUnit.MINUTES );
    }

    private void fillCache ()
    {
        logger.trace ( "fill cache" );
        this.cache.invalidateAll ();
        for ( final DataNode node : targetDao.readAllNodes () )
        {
            this.cache.put ( node.getId (), node );
        }
    }

    @Override
    public Collection<DataNode> readAllNodes ()
    {
        logger.trace ( "read all nodes" );
        try
        {
            this.readLock.lock ();
            fillCache ();
            return cache.asMap ().values ();
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    @Override
    public DataNode readNode ( final String nodeId )
    {
        logger.trace ( "read single node with id {}", nodeId );
        try
        {
            this.readLock.lock ();
            return this.cache.get ( nodeId );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException ( e );
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    @Override
    public void writeNode ( final DataNode node )
    {
        logger.trace ( "write node with id {}", node.getId () );
        try
        {
            this.writeLock.lock ();
            targetDao.writeNode ( node );
            this.cache.put ( node.getId (), node );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    @Override
    public void deleteNode ( final String nodeId )
    {
        logger.trace ( "delete node with id {}", nodeId );
        try
        {
            this.writeLock.lock ();
            targetDao.deleteNode ( nodeId );
            this.cache.invalidate ( nodeId );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    @Override
    public void dispose ()
    {
        this.targetDao.dispose ();
    }
}
