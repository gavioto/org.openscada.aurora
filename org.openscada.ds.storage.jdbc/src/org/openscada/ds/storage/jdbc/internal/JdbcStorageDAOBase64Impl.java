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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.openscada.ds.DataNode;
import org.openscada.utils.codec.Base64;
import org.openscada.utils.osgi.jdbc.DataSourceConnectionAccessor;
import org.openscada.utils.osgi.jdbc.task.CommonConnectionTask;
import org.openscada.utils.osgi.jdbc.task.ConnectionContext;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcStorageDAOBase64Impl implements JdbcStorageDAO
{

    private final static Logger logger = LoggerFactory.getLogger ( JdbcStorageDAOBase64Impl.class );

    private final String instanceId = System.getProperty ( "org.openscada.ds.storage.jdbc.instance", "default" );

    private int chunkSize = Integer.getInteger ( "org.openscada.ds.storage.jdbc.chunkSize", 0 );

    private final String tableName = System.getProperty ( "org.openscada.ds.storage.jdbc.table", "datastore" );

    private final DataSourceConnectionAccessor accessor;

    public JdbcStorageDAOBase64Impl ( final DataSourceFactory dataSourceFactory, final Properties paramProperties ) throws SQLException
    {
        this.accessor = new DataSourceConnectionAccessor ( dataSourceFactory, paramProperties );
        if ( this.chunkSize <= 0 )
        {
            this.chunkSize = Integer.MAX_VALUE;
        }
    }

    @Override
    public void dispose ()
    {
        this.accessor.dispose ();
    }

    @Override
    public DataNode readNode ( final String nodeId )
    {
        final List<String> result = findAll ( nodeId );

        if ( result.isEmpty () )
        {
            return null;
        }
        else
        {
            // merge node
            final StringBuilder sb = new StringBuilder ();
            for ( final String entry : result )
            {
                sb.append ( entry );
            }
            try
            {
                final String data = sb.toString ();
                logger.debug ( "Read: {}", data );
                return new DataNode ( nodeId, Base64.decode ( data ) );
            }
            catch ( final IOException e )
            {
                logger.warn ( "Failed to decode data node", e );
                return null;
            }
        }
    }

    private List<String> findAll ( final String nodeId )
    {
        logger.debug ( "Find node: {}", nodeId );

        final String sql = String.format ( "select data from %s where node_id=? and instance_id=? order by sequence_nr", dataStoreName () );

        return this.accessor.doWithConnection ( new CommonConnectionTask<List<String>> () {
            @Override
            protected List<String> performTask ( final ConnectionContext connectionContext ) throws SQLException
            {
                return connectionContext.queryForList ( String.class, sql, nodeId, JdbcStorageDAOBase64Impl.this.instanceId );
            }
        } );
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
                connectionContext.setAutoCommit ( false );
                deleteNode ( connectionContext, nodeId );
                connectionContext.commit ();
                return null;
            }
        } );
    }

    protected void deleteNode ( final ConnectionContext context, final String nodeId ) throws SQLException
    {
        context.update ( String.format ( "delete from %s where node_id=? and instance_id=?", dataStoreName () ), nodeId, JdbcStorageDAOBase64Impl.this.instanceId );
    }

    @Override
    public void writeNode ( final DataNode node )
    {
        final String data;

        if ( node != null && node.getData () != null )
        {
            data = Base64.encodeBytes ( node.getData () );
        }
        else
        {
            data = null;
        }

        logger.debug ( "Write data node: {} -> {}", node, data );

        this.accessor.doWithConnection ( new CommonConnectionTask<Void> () {

            @Override
            protected Void performTask ( final ConnectionContext connectionContext ) throws Exception
            {
                connectionContext.setAutoCommit ( false );

                deleteNode ( connectionContext, node.getId () );
                insertNode ( connectionContext, node, data );

                connectionContext.commit ();
                return null;
            }
        } );

    }

    protected void insertNode ( final ConnectionContext connectionContext, final DataNode node, final String data ) throws SQLException
    {
        // TODO: re-use SQL statement
        if ( data != null )
        {
            final int len = data.length ();

            for ( int i = 0; i <= len / this.chunkSize; i++ )
            {
                int end = ( i + 1 ) * this.chunkSize;
                if ( end > len )
                {
                    end = len;
                }

                final String chunk = data.substring ( i * this.chunkSize, end );

                connectionContext.update ( String.format ( "insert into %s ( node_id, instance_id, sequence_nr, data ) values ( ? , ?, ?, ? )", dataStoreName () ), node.getId (), this.instanceId, i, chunk );
            }
        }
    }
}
