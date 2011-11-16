package org.openscada.utils.osgi.jdbc.data;

public class RowMapperMappingException extends RowMapperException
{
    private static final long serialVersionUID = 1L;

    public RowMapperMappingException ()
    {
    }

    public RowMapperMappingException ( final String message )
    {
        super ( message );
    }
}