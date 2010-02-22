/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.ca.jdbc.internal;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.lob.ClobImpl;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class JdbcStorageDAOImpl extends HibernateTemplate implements JdbcStorageDAO
{
    @SuppressWarnings ( { "unchecked" } )
    public List<Entry> loadAll ()
    {
        return loadAll ( Entry.class );
    }

    @SuppressWarnings ( "unchecked" )
    public List<Entry> loadFactory ( final String factoryId )
    {
        return find ( String.format ( "from %s where factoryId=?", "Entry" ), factoryId );
    }

    public List<Entry> purgeFactory ( final String factoryId )
    {
        final List<Entry> entries = loadFactory ( factoryId );
        deleteAll ( entries );
        return entries;
    }

    public void storeConfiguration ( final String factoryId, final String configurationId, final Map<String, String> properties )
    {
        executeWithNativeSession ( new HibernateCallback () {

            public Object doInHibernate ( final Session session ) throws HibernateException, SQLException
            {
                performDeleteConfiguration ( session, factoryId, configurationId );

                for ( final Map.Entry<String, String> entry : properties.entrySet () )
                {
                    final Entry dataEntry = new Entry ();
                    dataEntry.setFactoryId ( factoryId );
                    dataEntry.setConfigurationId ( configurationId );
                    dataEntry.setKey ( entry.getKey () );
                    dataEntry.setValue ( new ClobImpl ( entry.getValue () ) );
                    session.save ( dataEntry );
                }
                return null;
            }
        } );

    }

    public void deleteConfiguration ( final String factoryId, final String configurationId )
    {
        executeWithNativeSession ( new HibernateCallback () {

            public Object doInHibernate ( final Session session ) throws HibernateException, SQLException
            {
                performDeleteConfiguration ( session, factoryId, configurationId );
                return null;
            }
        } );
    }

    protected void performDeleteConfiguration ( final Session session, final String factoryId, final String configurationId )
    {
        final Query q = session.createQuery ( String.format ( "delete %s where factoryId=:factoryId and configurationId=:configurationId", "Entry" ) );
        prepareQuery ( q );
        q.setString ( "factoryId", factoryId );
        q.setString ( "configurationId", configurationId );
        q.executeUpdate ();
    }
}
