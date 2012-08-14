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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

    public CachingStorageDao ( final JdbcStorageDao targetDao, final long expireTime )
    {
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

            @Override
            public Map<String, DataNode> loadAll ( final Iterable<? extends String> nodeIds ) throws Exception
            {
                logger.trace ( "load all nodes" );
                final Map<String, DataNode> result = new HashMap<String, DataNode> ();
                for ( final DataNode node : targetDao.readAllNodes () )
                {
                    result.put ( node.getId (), node );
                }
                return result;
            }
        } );
        // preload cache
        try
        {
            this.cache.getAll ( null );
        }
        catch ( final ExecutionException e )
        {
            throw new RuntimeException ( e );
        }
    }

    @Override
    public Collection<DataNode> readAllNodes ()
    {
        try
        {
            return this.cache.getAll ( Collections.<String>emptyList () ).values ();
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException ( e );
        }
    }

    @Override
    public DataNode readNode ( final String nodeId )
    {
        try
        {
            return this.cache.get ( nodeId );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException ( e );
        }
    }

    @Override
    public void writeNode ( final DataNode node )
    {
        try
        {
            this.cache.get ( node.getId (), new Callable<DataNode> () {
                @Override
                public DataNode call () throws Exception
                {
                    logger.trace ( "write node with id {}", node.getId () );
                    targetDao.writeNode ( node );
                    return node;
                }
            } );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException ( e );
        }
    }

    @Override
    public void deleteNode ( final String nodeId )
    {
        try
        {
            this.cache.get ( nodeId, new Callable<DataNode> () {
                @Override
                public DataNode call () throws Exception
                {
                    logger.trace ( "delete node with id {}", nodeId );
                    targetDao.deleteNode ( nodeId );
                    return null;
                }
            } );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException ( e );
        }
        this.cache.invalidate ( nodeId );
    }

    @Override
    public void dispose ()
    {
        this.targetDao.dispose ();
    }
}
