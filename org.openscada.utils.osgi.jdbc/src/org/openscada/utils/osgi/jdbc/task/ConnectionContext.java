/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jürgen Rose (cptmauli@googlemail.com)
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.openscada.utils.osgi.jdbc.data.RowMapper;

public interface ConnectionContext
{
    public Connection getConnection ();

    public void setAutoCommit ( boolean autoCommit ) throws SQLException;

    public void commit () throws SQLException;

    public void rollback () throws SQLException;

    public <T> List<T> queryForList ( Class<T> clazz, String sql, Object... parameters ) throws SQLException;

    public <T> List<T> queryForList ( Class<T> clazz, String sql, Map<String, Object> parameters ) throws SQLException;

    public void query ( ResultSetProcessor resultSetProcessor, String sql, Object... parameters ) throws SQLException;

    public void query ( ResultSetProcessor resultSetProcessor, String sql, Map<String, Object> parameters ) throws SQLException;

    public int update ( String sql, Object... parameters ) throws SQLException;

    public int update ( String sql, Map<String, Object> parameters ) throws SQLException;

    public void query ( RowCallback callback, String sql, Object... parameters ) throws SQLException;

    public void query ( RowCallback callback, String sql, Map<String, Object> parameters ) throws SQLException;

    public <T> List<T> query ( RowMapper<T> rowMapper, String sql, Object... parameters ) throws SQLException;

    public <T> List<T> query ( RowMapper<T> rowMapper, String sql, Map<String, Object> parameters ) throws SQLException;

    public <T> T queryForObject ( RowMapper<T> rowMapper, String sql, Object... parameters ) throws SQLException;

    public <T> T queryForObject ( RowMapper<T> rowMapper, String sql, Map<String, Object> parameters ) throws SQLException;

}
