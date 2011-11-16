package org.openscada.utils.osgi.jdbc.task;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetProcessor
{
    public void processResult ( ResultSet resultSet ) throws SQLException;
}
