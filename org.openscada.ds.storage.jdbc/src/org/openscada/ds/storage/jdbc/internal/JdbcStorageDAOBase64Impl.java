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

import java.io.IOException;
import java.util.List;

import org.openscada.ds.DataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

public class JdbcStorageDAOBase64Impl extends JdbcTemplate implements JdbcStorageDAO, InitializingBean
{

    private final static Logger logger = LoggerFactory.getLogger ( JdbcStorageDAOBase64Impl.class );

    private String instanceId = "default";

    public void setInstanceId ( final String instanceId )
    {
        this.instanceId = instanceId;
    }

    private int chunkSize = 0;

    public void setChunkSize ( final int chunkSize )
    {
        if ( chunkSize <= 0 )
        {
            this.chunkSize = Integer.MAX_VALUE;
        }
        else
        {
            this.chunkSize = chunkSize;
        }
    }

    private String tableName = "datastore";

    public void setTableName ( final String tableName )
    {
        this.tableName = tableName;
    }

    @Override
    public void afterPropertiesSet ()
    {
        Assert.hasText ( this.tableName, "'tableName' must be set" );

        super.afterPropertiesSet ();
    }

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

    @SuppressWarnings ( "unchecked" )
    private List<String> findAll ( final String nodeId )
    {
        logger.debug ( "Find node: {}", nodeId );
        final List result = queryForList ( String.format ( "select data from %s where node_id=? and instance_id=? order by sequence_nr", dataStoreName () ), new Object[] { nodeId, this.instanceId }, String.class );
        return result;
    }

    protected String dataStoreName ()
    {
        return this.tableName;
    }

    public void deleteNode ( final String nodeId )
    {
        update ( String.format ( "delete from %s where node_id=? and instance_id=?", dataStoreName () ), new Object[] { nodeId, this.instanceId } );
    }

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

        deleteNode ( node.getId () );

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
                update ( String.format ( "insert into %s ( node_id, instance_id, sequence_nr, data ) values ( ? , ?, ?, ? )", dataStoreName () ), new Object[] { node.getId (), this.instanceId, i, chunk } );
            }
        }
    }
}
