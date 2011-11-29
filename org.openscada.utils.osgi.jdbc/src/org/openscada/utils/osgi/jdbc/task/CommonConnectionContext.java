/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.osgi.jdbc.task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openscada.utils.osgi.jdbc.data.RowMapper;
import org.openscada.utils.osgi.jdbc.data.RowMapperException;
import org.openscada.utils.osgi.jdbc.data.SingleColumnRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommonConnectionContext implements ConnectionContext
{

    private final static Logger logger = LoggerFactory.getLogger ( CommonConnectionContext.class );

    @Override
    public void setAutoCommit ( final boolean autoCommit ) throws SQLException
    {
        getConnection ().setAutoCommit ( autoCommit );
    }

    @Override
    public void commit () throws SQLException
    {
        getConnection ().commit ();
    }

    @Override
    public void rollback () throws SQLException
    {
        getConnection ().rollback ();
    }

    private static class CaptureMappedResultSetProcessor<T> implements ResultSetProcessor
    {
        private List<T> result;

        private final RowMapper<T> mapper;

        public CaptureMappedResultSetProcessor ( final RowMapper<T> mapper )
        {
            this.mapper = mapper;
        }

        @Override
        public void processResult ( final ResultSet resultSet ) throws SQLException, RowMapperException
        {
            this.result = new ArrayList<T> ();

            this.mapper.validate ( resultSet );

            while ( resultSet.next () )
            {
                final T mapped = this.mapper.mapRow ( resultSet );
                if ( mapped != null )
                {
                    this.result.add ( mapped );
                }
            }
        }

        public List<T> getResult ()
        {
            return this.result;
        }
    }

    // ==== QUERY

    @Override
    public <T> List<T> queryForList ( final Class<T> clazz, final String sql, final Object... parameters ) throws SQLException
    {
        final CaptureMappedResultSetProcessor<T> crsp = new CaptureMappedResultSetProcessor<T> ( new SingleColumnRowMapper<T> ( clazz ) );
        query ( crsp, sql, parameters );
        return crsp.getResult ();
    }

    @Override
    public void query ( final RowCallback callback, final String sql, final Object... parameters ) throws SQLException
    {
        query ( new ResultSetProcessor () {

            @Override
            public void processResult ( final ResultSet resultSet ) throws SQLException
            {
                while ( resultSet.next () )
                {
                    callback.processRow ( resultSet );
                }
            }
        }, sql, parameters );
    }

    @Override
    public void query ( final ResultSetProcessor resultSetProcessor, final String sql, final Object... parameters ) throws SQLException
    {
        final PreparedStatement stmt = getConnection ().prepareStatement ( sql );
        try
        {
            applyParameters ( stmt, parameters );

            final ResultSet rs = stmt.executeQuery ();

            try
            {
                resultSetProcessor.processResult ( rs );
            }
            finally
            {
                if ( rs != null )
                {
                    rs.close ();
                }
            }
        }
        finally
        {
            if ( stmt != null )
            {
                stmt.close ();
            }
        }
    }

    protected void applyParameters ( final PreparedStatement stmt, final Object... parameters ) throws SQLException
    {
        if ( parameters != null )
        {
            for ( int i = 0; i < parameters.length; i++ )
            {
                logger.trace ( "Set parameter #{} - {}", i + 1, parameters[i] );
                stmt.setObject ( i + 1, parameters[i] );
            }
        }
    }

    // === UPDATE

    @Override
    public int update ( final String sql, final Object... parameters ) throws SQLException
    {
        logger.trace ( "Preparing SQL - {}", sql );

        final PreparedStatement stmt = getConnection ().prepareStatement ( sql );
        try
        {
            applyParameters ( stmt, parameters );
            return stmt.executeUpdate ();
        }
        finally
        {
            if ( stmt != null )
            {
                stmt.close ();
            }
        }
    }
}
