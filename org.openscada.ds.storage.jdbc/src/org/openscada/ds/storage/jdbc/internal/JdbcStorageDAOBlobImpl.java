/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.openscada.ds.DataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class JdbcStorageDAOBlobImpl extends JdbcTemplate implements JdbcStorageDAO
{

    private final static Logger logger = LoggerFactory.getLogger ( JdbcStorageDAOBlobImpl.class );

    private static final String INSTANCE_ID = System.getProperty ( "org.openscada.ds.storage.jdbc.instance", "default" );

    private static final String TABLE = System.getProperty ( "org.openscada.ds.storage.jdbc.table", "datastore" );

    public DataNode readNode ( final String nodeId )
    {
        final List<DataNode> result = new LinkedList<DataNode> ();
        query ( String.format ( "select node_id,data from %s where node_id=? and instance_id=?", dataStoreName () ), new Object[] { nodeId, INSTANCE_ID }, new RowCallbackHandler () {

            public void processRow ( final ResultSet rs ) throws SQLException
            {
                result.add ( new DataNode ( rs.getString ( "node_id" ), rs.getBytes ( "data" ) ) );
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
        return TABLE;
    }

    public void deleteNode ( final String nodeId )
    {
        update ( String.format ( "delete from %s where node_id=? and instance_id=?", dataStoreName () ), new Object[] { nodeId, INSTANCE_ID } );
    }

    public void writeNode ( final DataNode node )
    {
        logger.debug ( "Write data node: {}", node );

        deleteNode ( node.getId () );
        update ( String.format ( "insert into %s ( node_id, instance_id, data ) values ( ? , ?, ? )", dataStoreName () ), new Object[] { node.getId (), INSTANCE_ID, node.getData () } );
    }
}
