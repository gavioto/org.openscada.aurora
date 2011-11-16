package org.openscada.utils.osgi.jdbc.data;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SingleColumnRowMapper<T> implements RowMapper<T>
{
    private final Class<T> clazz;

    public SingleColumnRowMapper ( final Class<T> clazz )
    {
        this.clazz = clazz;
    }

    @Override
    public void validate ( final ResultSet resultSet ) throws RowMapperValidationException, SQLException
    {
        final ResultSetMetaData md = resultSet.getMetaData ();
        if ( md.getColumnCount () != 1 )
        {
            throw new RowMapperValidationException ( "Column count must be exactly one" );
        }
    }

    @Override
    public T mapRow ( final ResultSet resultSet ) throws RowMapperMappingException, SQLException
    {
        final Object result = resultSet.getObject ( 1 );

        if ( this.clazz.isAssignableFrom ( result.getClass () ) )
        {
            return this.clazz.cast ( result );
        }
        else
        {
            throw new RowMapperMappingException ( String.format ( "Failed to map from data type %s to %s", result.getClass (), this.clazz ) );
        }
    }
}