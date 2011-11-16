package org.openscada.utils.osgi.jdbc.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface RowMapper<T>
{
    public void validate ( ResultSet resultSet ) throws SQLException, RowMapperValidationException;

    public T mapRow ( ResultSet resultSet ) throws SQLException, RowMapperMappingException;
}