/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2009-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
