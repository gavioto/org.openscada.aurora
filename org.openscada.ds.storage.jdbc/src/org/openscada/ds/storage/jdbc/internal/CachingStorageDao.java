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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.ds.DataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingStorageDao implements JdbcStorageDao
{

    private final static Logger logger = LoggerFactory.getLogger ( CachingStorageDao.class );

    private final JdbcStorageDao targetDao;

    private final Map<String, DataNode> cacheMap;

    private final ReadWriteLock lock = new ReentrantReadWriteLock ();

    private final Lock readLock = this.lock.readLock ();

    private final Lock writeLock = this.lock.writeLock ();

    public CachingStorageDao ( final JdbcStorageDao targetDao )
    {
        this.targetDao = targetDao;
        try
        {
            this.writeLock.lock ();

            logger.info ( "Starting cache prefill" );

            final Collection<DataNode> nodes = targetDao.readAllNodes ();
            this.cacheMap = new HashMap<String, DataNode> ( nodes.size () );

            logger.debug ( "Prefill found {} entries", nodes.size () );

            for ( final DataNode node : nodes )
            {
                this.cacheMap.put ( node.getId (), node );
            }

            logger.info ( "Prefill complete" );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    @Override
    public Collection<DataNode> readAllNodes ()
    {
        try
        {
            this.readLock.lock ();
            return new ArrayList<DataNode> ( this.cacheMap.values () );
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    @Override
    public DataNode readNode ( final String nodeId )
    {
        try
        {
            this.readLock.lock ();
            if ( this.cacheMap.containsKey ( nodeId ) )
            {
                return this.cacheMap.get ( nodeId );
            }
        }
        finally
        {
            this.readLock.unlock ();
        }

        // releasing all locks could cause multiple reads, but this is ok
        final DataNode dataNode = this.targetDao.readNode ( nodeId );

        try
        {
            this.writeLock.lock ();
            // check if entry was inserted meanwhile (might be a write)
            if ( this.cacheMap.containsKey ( nodeId ) )
            {
                return this.cacheMap.get ( nodeId );
            }
            this.cacheMap.put ( nodeId, dataNode );
            return dataNode;
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    @Override
    public void writeNode ( final DataNode node )
    {
        try
        {
            this.writeLock.lock ();
            this.cacheMap.put ( node.getId (), node );
        }
        finally
        {
            this.writeLock.unlock ();
        }
        this.targetDao.writeNode ( node );
    }

    @Override
    public void deleteNode ( final String nodeId )
    {
        try
        {
            this.writeLock.lock ();
            this.cacheMap.remove ( nodeId );
        }
        finally
        {
            this.writeLock.unlock ();
        }
        this.targetDao.deleteNode ( nodeId );
    }

    @Override
    public void dispose ()
    {
        this.targetDao.dispose ();
    }

}
