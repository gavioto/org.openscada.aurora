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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class JdbcStorageDAOImpl extends JdbcTemplate implements JdbcStorageDAO
{
    private static final String defaultOrder = " ORDER BY instance_id, factory_id, configuration_id, chunk_seq";

    private String tableName = "ca_data";

    private int chunkSize = 0;

    private boolean fixNull = false;

    private String instanceId = "default";

    private RowMapper mapper = new RowMapper () {
        public Object mapRow ( ResultSet rs, int rowNum ) throws SQLException
        {
            Entry entry = new Entry ();
            entry.setInstance ( rs.getString ( "instance_id" ) );
            entry.setFactoryId ( rs.getString ( "factory_id" ) );
            entry.setConfigurationId ( rs.getString ( "configuration_id" ) );
            entry.setKey ( rs.getString ( "ca_key" ) );
            entry.setValue ( rs.getString ( "ca_value" ) );
            entry.setSeq ( rs.getInt ( "chunk_seq" ) );
            return entry;
        }
    };

    @SuppressWarnings ( { "unchecked" } )
    public List<Entry> loadAll ()
    {
        List result = query ( String.format ( "SELECT * FROM %s WHERE instance_id = ? %s", tableName, defaultOrder ), new Object[] { instanceId }, mapper );
        return deChunk ( fixNulls ( result ) );
    }

    @SuppressWarnings ( "unchecked" )
    public List<Entry> loadFactory ( final String factoryId )
    {
        List result = query ( String.format ( "SELECT * FROM %s WHERE instance_id = ? AND factory_id = ? %s", tableName, defaultOrder ), new Object[] { instanceId, factoryId }, mapper );
        return deChunk ( fixNulls ( result ) );
    }

    @SuppressWarnings ( "unchecked" )
    public List<Entry> loadConfiguration ( final String factoryId, final String configurationId )
    {
        List result = query ( String.format ( "SELECT * FROM %s WHERE instance_id = ?  AND factory_id = ? AND configuration_id = ? %s", tableName, defaultOrder ), new Object[] { instanceId, factoryId, configurationId }, mapper );
        return deChunk ( fixNulls ( result ) );
    }

    public Map<String, String> storeConfiguration ( final String factoryId, final String configurationId, final Map<String, String> properties, final boolean fullSet )
    {
        if ( fullSet )
        {
            deleteConfiguration ( factoryId, configurationId );
        }
        List<Entry> toStore = new ArrayList<Entry> ();
        for ( final Map.Entry<String, String> entry : properties.entrySet () )
        {
            final Entry dataEntry = new Entry ();
            dataEntry.setInstance ( instanceId );
            dataEntry.setFactoryId ( factoryId );
            dataEntry.setConfigurationId ( configurationId );
            dataEntry.setKey ( entry.getKey () );
            dataEntry.setValue ( entry.getValue () );
            toStore.add ( dataEntry );
        }

        for ( Entry entry : chunk ( toStore ) )
        {
            storeEntry ( entry );
        }

        // map result
        final Map<String, String> result = new HashMap<String, String> ();
        for ( final Entry entry : deChunk ( fixNulls ( loadConfiguration ( factoryId, configurationId ) ) ) )
        {
            result.put ( entry.getKey (), entry.getValue () );
        }
        return result;
    }

    private void storeEntry ( Entry entry )
    {
        final Object[] params = new Object[] { entry.getInstance (), entry.getFactoryId (), entry.getConfigurationId (), entry.getKey (), entry.getValue (), entry.getSeq () };
        update ( String.format ( "INSERT INTO %s (instance_id, factory_id, configuration_id, ca_key, ca_value, chunk_seq) VALUES (?, ?, ?, ?, ?, ?)", tableName ), params );
    }

    public List<Entry> purgeFactory ( final String factoryId )
    {
        final List<Entry> entries = fixNulls ( loadFactory ( factoryId ) );
        update ( String.format ( "DELETE FROM %s WHERE instance_id = ? AND factory_id = ?", tableName ), new Object[] { instanceId, factoryId } );
        return entries;
    }

    public void deleteConfiguration ( final String factoryId, final String configurationId )
    {
        update ( String.format ( "DELETE FROM %s WHERE instance_id = ? AND factory_id = ? AND configuration_id = ?", tableName ), new Object[] { instanceId, factoryId, configurationId } );
    }

    protected List<Entry> fixNulls ( final List<Entry> data )
    {
        if ( !fixNull )
        {
            return data;
        }

        for ( final Entry entry : data )
        {
            if ( entry.getValue () == null )
            {
                entry.setValue ( "" );
            }
        }

        return data;
    }

    protected List<Entry> chunk ( final List<Entry> data )
    {
        // shortcut: we don't need to chunk
        if ( chunkSize == 0 )
        {
            return data;
        }
        List<Entry> result = new ArrayList<Entry> ();
        for ( Entry entry : data )
        {
            // shortcut: we don't need to chunk
            if ( entry.getValue () == null || entry.getValue ().length () <= chunkSize )
            {
                result.add ( entry );
                continue;
            }
            // loop over string
            int from = 0;
            int to = chunkSize;
            int seq = 1;
            do
            {
                // special case last chunk
                if ( to > entry.getValue ().length () )
                {
                    to = entry.getValue ().length ();
                }
                // create new entry and add to result
                Entry newEntry = new Entry ( entry );
                newEntry.setValue ( entry.getValue ().substring ( from, to ) );
                newEntry.setSeq ( seq );
                result.add ( newEntry );
                // runtime variables
                from += chunkSize;
                to += chunkSize;
                seq += 1;
            } while ( from < entry.getValue ().length () );
        }
        return result;
    }

    protected List<Entry> deChunk ( final List<Entry> data )
    {
        List<Entry> result = new ArrayList<Entry> ();
        Entry newEntry = new Entry ();
        for ( Entry entry : data )
        {
            // if there is only one, add it to result and continue
            if ( entry.getSeq () == 0 )
            {
                // but add last chunked one first
                if ( newEntry.getSeq () > 0 )
                {
                    newEntry.setSeq ( 0 );
                    result.add ( newEntry );
                    newEntry = new Entry ();
                }
                result.add ( entry );
                continue;
            }
            newEntry.setInstance ( entry.getInstance () );
            newEntry.setFactoryId ( entry.getFactoryId () );
            newEntry.setConfigurationId ( entry.getConfigurationId () );
            newEntry.setKey ( entry.getKey () );
            newEntry.setValue ( ( newEntry.getValue () == null ? "" : newEntry.getValue () ) + entry.getValue () );
            newEntry.setSeq ( entry.getSeq () );
        }
        // if the last one is also chunked, it is not already added, so do it now
        if ( newEntry.getSeq () > 0 )
        {
            newEntry.setSeq ( 0 );
            result.add ( newEntry );
        }
        return result;
    }

    public String getTableName ()
    {
        return tableName;
    }

    public void setTableName ( String tableName )
    {
        this.tableName = tableName;
    }

    public int getChunkSize ()
    {
        return chunkSize;
    }

    public void setChunkSize ( int chunkSize )
    {
        this.chunkSize = chunkSize;
    }

    public boolean isFixNull ()
    {
        return fixNull;
    }

    public void setFixNull ( boolean fixNull )
    {
        this.fixNull = fixNull;
    }

    public String getInstanceId ()
    {
        return instanceId;
    }

    public void setInstanceId ( String instanceId )
    {
        this.instanceId = instanceId;
    }
}
