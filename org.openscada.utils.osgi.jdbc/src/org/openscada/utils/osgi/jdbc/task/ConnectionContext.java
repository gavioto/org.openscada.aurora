package org.openscada.utils.osgi.jdbc.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ConnectionContext
{
    public Connection getConnection ();

    public void setAutoCommit ( boolean autoCommit ) throws SQLException;

    public void commit () throws SQLException;

    public void rollback () throws SQLException;

    public <T> List<T> queryForList ( Class<T> clazz, String sql, Object... parameters ) throws SQLException;

    public void query ( ResultSetProcessor resultSetProcessor, String sql, Object... parameters ) throws SQLException;

    public int update ( String sql, Object... parameters ) throws SQLException;

    void query ( RowCallback callback, String sql, Object... parameters ) throws SQLException;
}
