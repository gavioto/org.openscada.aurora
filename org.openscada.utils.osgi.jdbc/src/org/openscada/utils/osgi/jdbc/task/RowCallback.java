package org.openscada.utils.osgi.jdbc.task;

import java.sql.ResultSet;

public interface RowCallback
{
    public void processRow ( ResultSet resultSet );
}
