package org.openscada.sec;

import org.openscada.utils.statuscodes.CodedRuntimeException;
import org.openscada.utils.statuscodes.StatusCode;

public class PermissionDeniedException extends CodedRuntimeException
{

    private static final long serialVersionUID = 1L;

    public PermissionDeniedException ( final StatusCode code, final String message )
    {
        super ( code, message );
    }
}
