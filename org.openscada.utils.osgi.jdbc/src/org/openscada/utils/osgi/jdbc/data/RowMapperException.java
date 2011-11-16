package org.openscada.utils.osgi.jdbc.data;

public class RowMapperException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public RowMapperException ()
    {
        super ();
    }

    public RowMapperException ( final String message, final Throwable cause )
    {
        super ( message, cause );
    }

    public RowMapperException ( final String message )
    {
        super ( message );
    }

    public RowMapperException ( final Throwable cause )
    {
        super ( cause );
    }

}