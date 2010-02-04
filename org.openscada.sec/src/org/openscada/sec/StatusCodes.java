package org.openscada.sec;

import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;

public interface StatusCodes
{
    public static final StatusCode UNKNOWN_STATUS_CODE = new StatusCode ( "OS", "SEC", 1, SeverityLevel.ERROR );

}
