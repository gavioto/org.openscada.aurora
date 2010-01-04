package org.openscada.utils.toggle;

public class ToggleError extends RuntimeException
{
    private static final long serialVersionUID = 5657944778147860794L;

    public ToggleError ()
    {
        super ();
    }

    public ToggleError ( String message, Throwable cause )
    {
        super ( message, cause );
    }

    public ToggleError ( String message )
    {
        super ( message );
    }

    public ToggleError ( Throwable cause )
    {
        super ( cause );
    }
}
