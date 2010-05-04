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

package org.openscada.ca.jdbc.internal;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class JdbcStorageDAOImpl extends HibernateTemplate implements JdbcStorageDAO
{
    private static final String ENT_ENTRY = Entry.class.getName ();

    private static final String INSTANCE_ID = System.getProperty ( "org.openscada.ca.jdbc.instance", "default" );

    @SuppressWarnings ( { "unchecked" } )
    public List<Entry> loadAll ()
    {
        return find ( String.format ( "from %s where instance=?", ENT_ENTRY ), INSTANCE_ID );
    }

    @SuppressWarnings ( "unchecked" )
    public List<Entry> loadFactory ( final String factoryId )
    {
        return find ( String.format ( "from %s where factoryId=? and instance=?", ENT_ENTRY ), new Object[] { factoryId, INSTANCE_ID } );
    }

    public List<Entry> purgeFactory ( final String factoryId )
    {
        final List<Entry> entries = loadFactory ( factoryId );
        deleteAll ( entries );
        return entries;
    }

    @SuppressWarnings ( "unchecked" )
    public Map<String, String> storeConfiguration ( final String factoryId, final String configurationId, final Map<String, String> properties, final boolean fullSet )
    {
        final List<Entry> entries = (List<Entry>)executeWithNativeSession ( new HibernateCallback () {

            public Object doInHibernate ( final Session session ) throws HibernateException, SQLException
            {
                if ( fullSet )
                {
                    performDeleteConfiguration ( session, factoryId, configurationId );
                }

                for ( final Map.Entry<String, String> entry : properties.entrySet () )
                {
                    final String key = entry.getKey ();
                    final String value = entry.getValue ();

                    final Entry dataEntry = new Entry ();
                    dataEntry.setFactoryId ( factoryId );
                    dataEntry.setConfigurationId ( configurationId );
                    dataEntry.setKey ( key );
                    dataEntry.setInstance ( INSTANCE_ID );

                    if ( value != null )
                    {
                        dataEntry.setValue ( value );
                        session.saveOrUpdate ( dataEntry );
                    }
                    else
                    {
                        session.delete ( dataEntry );
                    }
                }
                session.flush ();
                return session.createQuery ( String.format ( "from %s where factoryId=:factoryId and configurationId=:configurationId and instance=:instance", ENT_ENTRY ) ).setString ( "factoryId", factoryId ).setString ( "configurationId", configurationId ).setString ( "instance", INSTANCE_ID ).list ();
            }
        } );

        // map result
        final Map<String, String> result = new HashMap<String, String> ();
        for ( final Entry entry : entries )
        {
            result.put ( entry.getKey (), entry.getValue () );
        }
        return result;
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
        final Query q = session.createQuery ( String.format ( "delete %s where factoryId=:factoryId and configurationId=:configurationId and instance=:instance", ENT_ENTRY ) );
        prepareQuery ( q );
        q.setString ( "factoryId", factoryId );
        q.setString ( "configurationId", configurationId );
        q.setString ( "instance", INSTANCE_ID );
        q.executeUpdate ();
    }
}
