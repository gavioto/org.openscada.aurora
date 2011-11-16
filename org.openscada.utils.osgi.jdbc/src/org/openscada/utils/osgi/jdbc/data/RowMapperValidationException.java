package org.openscada.utils.osgi.jdbc.data;

public class RowMapperValidationException extends RowMapperException
{
    private static final long serialVersionUID = 1L;

    public RowMapperValidationException ()
    {
    }

    public RowMapperValidationException ( final String message )
    {
        super ( message );
    }
}