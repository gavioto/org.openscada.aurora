/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec;

import org.openscada.utils.statuscodes.CodedException;
import org.openscada.utils.statuscodes.StatusCode;

public class AuthenticationException extends CodedException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1320807611229839462L;

    public AuthenticationException ( final StatusCode statusCode, final String message, final Throwable cause )
    {
        super ( statusCode, message, cause );
    }

    public AuthenticationException ( final StatusCode statusCode, final String message )
    {
        super ( statusCode, message );
    }

    public AuthenticationException ( final StatusCode statusCode, final Throwable cause )
    {
        super ( statusCode, cause );
    }

    public AuthenticationException ( final StatusCode statusCode )
    {
        super ( statusCode );
    }

}
