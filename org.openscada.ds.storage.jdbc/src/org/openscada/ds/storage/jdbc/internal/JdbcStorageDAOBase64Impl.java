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
import org.springframework.orm.hibernate3.HibernateTemplate;

public class JdbcStorageDAOBase64Impl extends HibernateTemplate implements JdbcStorageDAO
{

    private final static int CHUNK_SIZE = Integer.getInteger ( "org.openscada.ds.storage.jdbc.chunkSize", 10 );

    private final static Logger logger = LoggerFactory.getLogger ( JdbcStorageDAOBase64Impl.class );

    private static final String ENT_ENTRY = EntryBase64.class.getName ();

    private static final String INSTANCE_ID = System.getProperty ( "org.openscada.ds.storage.jdbc.instance", "default" );

    @SuppressWarnings ( "unchecked" )
    public DataNode readNode ( final String nodeId )
    {
        final List result = findAll ( nodeId );
        if ( result.isEmpty () )
        {
            return null;
        }
        else
        {
            // merge node
            final StringBuilder sb = new StringBuilder ();
            for ( final EntryBase64 entry : (List<EntryBase64>)result )
            {
                sb.append ( entry.getData () );
            }
            try
            {
                return new DataNode ( nodeId, Base64.decode ( sb.toString () ) );
            }
            catch ( final IOException e )
            {
                logger.warn ( "Failed to decode data node", e );
                return null;
            }
        }
    }

    private List<?> findAll ( final String nodeId )
    {
        logger.debug ( "Find node: {}", nodeId );
        final List result = find ( String.format ( "from %s where nodeId=? and instance=? order by sequence", ENT_ENTRY ), new Object[] { nodeId, INSTANCE_ID } );
        logger.debug ( "Found entries: {}", result.size () );
        return result;
    }

    public void deleteNode ( final String nodeId )
    {
        deleteAll ( findAll ( nodeId ) );
    }

    public void writeNode ( final DataNode node )
    {
        final String data = Base64.encodeBytes ( node.getData () );

        final int len = data.length ();

        deleteNode ( node.getId () );
        flush ();
        clear ();

        for ( int i = 0; i < len / CHUNK_SIZE; i++ )
        {
            final EntryBase64 entry = new EntryBase64 ();
            entry.setInstance ( INSTANCE_ID );
            entry.setNodeId ( node.getId () );

            int end = ( i + 1 ) * CHUNK_SIZE;
            if ( end > len )
            {
                end = len;
            }
            entry.setData ( data.substring ( i * CHUNK_SIZE, end ) );
            save ( entry );
        }
        flush ();
    }
}
