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

import org.openscada.utils.lang.Immutable;
import org.openscada.utils.statuscodes.CodedExceptionBase;
import org.openscada.utils.statuscodes.StatusCode;

@Immutable
public class AuthorizationResult
{

    public static AuthorizationResult GRANTED = new AuthorizationResult ();

    private final StatusCode errorCode;

    private final String message;

    public static AuthorizationResult create ()
    {
        return GRANTED;
    }

    public static AuthorizationResult create ( final StatusCode statusCode, final String message )
    {
        if ( statusCode == null )
        {
            return GRANTED;
        }
        else
        {
            return new AuthorizationResult ( statusCode, message );
        }
    }

    public static AuthorizationResult create ( final Throwable error )
    {
        if ( error == null )
        {
            return GRANTED;
        }
        else if ( error instanceof CodedExceptionBase )
        {
            return new AuthorizationResult ( ( (CodedExceptionBase)error ).getStatus (), error.getMessage () );
        }
        else
        {
            return new AuthorizationResult ( StatusCodes.UNKNOWN_STATUS_CODE, error.getMessage () );
        }
    }

    protected AuthorizationResult ()
    {
        this.errorCode = null;
        this.message = null;
    }

    protected AuthorizationResult ( final StatusCode errorCode, final String message )
    {
        this.errorCode = errorCode;
        this.message = message;
    }

    public boolean isGranted ()
    {
        return this.errorCode == null;
    }

    public StatusCode getErrorCode ()
    {
        return this.errorCode;
    }

    public String getMessage ()
    {
        return this.message;
    }
}
