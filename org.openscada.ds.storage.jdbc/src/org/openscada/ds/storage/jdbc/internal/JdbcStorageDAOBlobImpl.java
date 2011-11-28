/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.openscada.ds.DataNode;
import org.openscada.utils.osgi.jdbc.DataSourceConnectionAccessor;
import org.openscada.utils.osgi.jdbc.task.CommonConnectionTask;
import org.openscada.utils.osgi.jdbc.task.ConnectionContext;
import org.openscada.utils.osgi.jdbc.task.ResultSetProcessor;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcStorageDAOBlobImpl implements JdbcStorageDAO
{
    private final static Logger logger = LoggerFactory.getLogger ( JdbcStorageDAOBlobImpl.class );

    private final String tableName = System.getProperty ( "org.openscada.ds.storage.jdbc.table", "datastore" );

    private final String instanceId = System.getProperty ( "org.openscada.ds.storage.jdbc.instance", "default" );

    private final DataSourceConnectionAccessor accessor;

    public JdbcStorageDAOBlobImpl ( final DataSourceFactory dataSourceFactory, final Properties paramProperties ) throws SQLException
    {
        this.accessor = new DataSourceConnectionAccessor ( dataSourceFactory, paramProperties );
    }

    @Override
    public DataNode readNode ( final String nodeId )
    {
        final List<DataNode> result = new LinkedList<DataNode> ();

        final String sql = String.format ( "select node_id,data from %s where node_id=? and instance_id=?", dataStoreName () );

        this.accessor.doWithConnection ( new CommonConnectionTask<List<DataNode>> () {
            @Override
            protected List<DataNode> performTask ( final ConnectionContext connectionContext ) throws Exception
            {
                connectionContext.query ( new ResultSetProcessor () {

                    @Override
                    public void processResult ( final ResultSet resultSet ) throws SQLException
                    {
                        result.add ( new DataNode ( resultSet.getString ( "node_id" ), resultSet.getBytes ( "data" ) ) );
                    }
                }, sql, nodeId, JdbcStorageDAOBlobImpl.this.instanceId );

                return null;
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

    protected String dataStoreName ()
    {
        return this.tableName;
    }

    @Override
    public void deleteNode ( final String nodeId )
    {
        this.accessor.doWithConnection ( new CommonConnectionTask<Void> () {
            @Override
            protected Void performTask ( final ConnectionContext connectionContext ) throws Exception
            {
                deleteNode ( nodeId );
                return null;
            }
        } );
    }

    protected void deleteNode ( final ConnectionContext connectionContext, final String nodeId ) throws SQLException
    {
        connectionContext.update ( String.format ( "delete from %s where node_id=? and instance_id=?", dataStoreName () ), nodeId, this.instanceId );
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
                connectionContext.update ( String.format ( "insert into %s ( node_id, instance_id, data ) values ( ? , ?, ? )", dataStoreName () ), node.getId (), JdbcStorageDAOBlobImpl.this.instanceId, node.getData () );

                connectionContext.commit ();
                return null;
            }
        } );
    }

    @Override
    public void dispose ()
    {
    }
}
