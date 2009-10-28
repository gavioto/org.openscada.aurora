package org.openscada.utils.filter;

/**
 * @author jrose
 */
public class FilterParseException extends RuntimeException
{

    private static final long serialVersionUID = -1979082977928327370L;

    public FilterParseException ()
    {
        super ();
    }

    public FilterParseException ( String message, Throwable cause )
    {
        super ( message, cause );
    }

    public FilterParseException ( String message )
    {
        super ( message );
    }

    public FilterParseException ( Throwable cause )
    {
        super ( cause );
    }
}
