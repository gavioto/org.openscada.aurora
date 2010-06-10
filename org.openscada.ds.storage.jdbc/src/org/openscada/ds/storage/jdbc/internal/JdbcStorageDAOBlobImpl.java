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

import java.util.List;

import org.openscada.ds.DataNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class JdbcStorageDAOBlobImpl extends HibernateTemplate implements JdbcStorageDAO
{

    private final static Logger logger = LoggerFactory.getLogger ( JdbcStorageDAOBlobImpl.class );

    private static final String ENT_ENTRY = EntryBlob.class.getName ();

    private static final String INSTANCE_ID = System.getProperty ( "org.openscada.ds.storage.jdbc.instance", "default" );

    @SuppressWarnings ( "unchecked" )
    public DataNode readNode ( final String nodeId )
    {
        final List result = find ( String.format ( "from %s where nodeId=? and instance=?", ENT_ENTRY ), new Object[] { nodeId, INSTANCE_ID } );
        if ( result.isEmpty () )
        {
            return null;
        }
        else
        {
            final EntryBlob entry = (EntryBlob)result.get ( 0 );
            final DataNode node = new DataNode ( entry.getNodeId (), entry.getData () );
            return node;
        }
    }

    public void deleteNode ( final String nodeId )
    {
        delete ( readNode ( nodeId ) );
    }

    public void writeNode ( final DataNode node )
    {
        logger.debug ( "Write data node: {}", node );

        final EntryBlob entry = new EntryBlob ();
        entry.setNodeId ( node.getId () );
        entry.setInstance ( INSTANCE_ID );
        entry.setData ( node.getData () );
        saveOrUpdate ( entry );
    }
}
