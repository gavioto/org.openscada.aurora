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

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.openscada.ds.DataNode;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class JdbcStorageDAOImpl extends HibernateTemplate implements JdbcStorageDAO
{
    private static final String ENT_ENTRY = Entry.class.getName ();

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
            return (DataNode)result.get ( 0 );
        }
    }

    public void deleteNode ( final String nodeId )
    {
        executeWithNativeSession ( new HibernateCallback () {

            public Object doInHibernate ( final Session session ) throws HibernateException, SQLException
            {
                performDeleteNode ( session, nodeId );
                return null;
            }
        } );
    }

    protected void performDeleteNode ( final Session session, final String nodeId )
    {
        final Query q = session.createQuery ( String.format ( "delete %s where nodeId=:nodeId and instance=:instance", ENT_ENTRY ) );
        prepareQuery ( q );
        q.setString ( "nodeId", nodeId );
        q.setString ( "instance", INSTANCE_ID );
        q.executeUpdate ();
    }

    public void writeNode ( final DataNode node )
    {
        saveOrUpdate ( node );
    }
}
