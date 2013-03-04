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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.openscada.ds.DataNode;
import org.openscada.utils.osgi.jdbc.CommonConnectionAccessor;
import org.openscada.utils.osgi.jdbc.DataSourceConnectionAccessor;
import org.openscada.utils.osgi.jdbc.pool.PoolConnectionAccessor;
import org.openscada.utils.osgi.jdbc.task.CommonConnectionTask;
import org.openscada.utils.osgi.jdbc.task.ConnectionContext;
import org.openscada.utils.osgi.jdbc.task.RowCallback;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcStorageDaoBlobImpl implements JdbcStorageDao
{
    public static class CollectRows implements RowCallback
    {
        private final List<DataNode> result;

        public CollectRows ( final List<DataNode> result )
        {
            this.result = result;
        }

        @Override
        public void processRow ( final ResultSet resultSet ) throws SQLException
        {
            this.result.add ( new DataNode ( resultSet.getString ( "node_id" ), resultSet.getBytes ( "data" ) ) );
        }

        public List<DataNode> getResult ()
        {
            return this.result;
        }
    }

    private final static Logger logger = LoggerFactory.getLogger ( JdbcStorageDaoBlobImpl.class );

    private final static String TABLE_NAME = System.getProperty ( "org.openscada.ds.storage.jdbc.table", "datastore" );

    private final static String SQL_SELECT = String.format ( "select node_id, data from %s where node_id=? and instance_id=?", TABLE_NAME );

    private final static String SQL_SELECT_ALL = String.format ( "select node_id, data from %s where instance_id=?", TABLE_NAME );

    private static final String SQL_INSERT = String.format ( "insert into %s ( node_id, instance_id, data ) values ( ? , ?, ? )", TABLE_NAME );

    private static final String SQL_DELETE = String.format ( "delete from %s where node_id=? and instance_id=?", TABLE_NAME );

    private final String instanceId = System.getProperty ( "org.openscada.ds.storage.jdbc.instance", "default" );

    private final CommonConnectionAccessor accessor;

    public JdbcStorageDaoBlobImpl ( final DataSourceFactory dataSourceFactory, final Properties paramProperties, final boolean usePool ) throws SQLException
    {
        this.accessor = usePool ? new PoolConnectionAccessor ( dataSourceFactory, paramProperties ) : new DataSourceConnectionAccessor ( dataSourceFactory, paramProperties );
    }

    @Override
    public DataNode readNode ( final String nodeId )
    {
        final List<DataNode> result = this.accessor.doWithConnection ( new CommonConnectionTask<List<DataNode>> () {
            @Override
            protected List<DataNode> performTask ( final ConnectionContext connectionContext ) throws Exception
            {
                final CollectRows innerResult = new CollectRows ( new LinkedList<DataNode> () );
                connectionContext.query ( innerResult, SQL_SELECT, nodeId, JdbcStorageDaoBlobImpl.this.instanceId );

                return innerResult.getResult ();
            }
        } );

        if ( result.isEmpty () )
        {
            return null;
        }
        else
        {
            return result.get ( 0 );
        }
    }

    @Override
    public Collection<DataNode> readAllNodes ()
    {
        return this.accessor.doWithConnection ( new CommonConnectionTask<List<DataNode>> () {
            @Override
            protected List<DataNode> performTask ( final ConnectionContext connectionContext ) throws Exception
            {
                final CollectRows innerResult = new CollectRows ( new LinkedList<DataNode> () );
                connectionContext.query ( innerResult, SQL_SELECT_ALL, JdbcStorageDaoBlobImpl.this.instanceId );

                return innerResult.getResult ();
            }
        } );
    }

    @Override
    public void deleteNode ( final String nodeId )
    {
        this.accessor.doWithConnection ( new CommonConnectionTask<Void> () {
            @Override
            protected Void performTask ( final ConnectionContext connectionContext ) throws Exception
            {
                // using auto commit
                deleteNode ( nodeId );
                return null;
            }
        } );
    }

    protected void deleteNode ( final ConnectionContext connectionContext, final String nodeId ) throws SQLException
    {
        connectionContext.update ( SQL_DELETE, nodeId, this.instanceId );
    }

    @Override
    public void writeNode ( final DataNode node )
    {
        logger.debug ( "Write data node: {}", node );

        this.accessor.doWithConnection ( new CommonConnectionTask<Void> () {
            @Override
            protected Void performTask ( final ConnectionContext connectionContext ) throws Exception
            {
                connectionContext.getConnection ().setAutoCommit ( false );

                deleteNode ( connectionContext, node.getId () );
                connectionContext.update ( SQL_INSERT, node.getId (), JdbcStorageDaoBlobImpl.this.instanceId, node.getData () );

                connectionContext.commit ();
                return null;
            }
        } );
    }

    @Override
    public void dispose ()
    {
        this.accessor.dispose ();
    }
}
