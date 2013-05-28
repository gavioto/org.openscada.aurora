/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.lang.Immutable;
import org.openscada.utils.statuscodes.CodedExceptionBase;
import org.openscada.utils.statuscodes.StatusCode;

/**
 * The result of an authentication request
 * 
 * @author Jens Reimann
 * @since 0.1.0
 */
@Immutable
public class AuthorizationResult
{

    private final StatusCode errorCode;

    private final String message;

    public static final AuthorizationResult GRANTED = new AuthorizationResult ();

    public static final AuthorizationResult ABSTAIN = null;

    public static AuthorizationResult createGranted ()
    {
        return GRANTED;
    }

    public static AuthorizationResult createAbstain ()
    {
        return ABSTAIN;
    }

    public static AuthorizationResult createReject ( final StatusCode statusCode, final String message )
    {
        if ( statusCode == null )
        {
            return createGranted ();
        }
        else
        {
            return new AuthorizationResult ( statusCode, message );
        }
    }

    public static AuthorizationResult createReject ( final Throwable error )
    {
        if ( error == null )
        {
            return createGranted ();
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
        return this.errorCode == null && this.message == null;
    }

    public StatusCode getErrorCode ()
    {
        return this.errorCode;
    }

    public String getMessage ()
    {
        return this.message;
    }

    public <T> NotifyFuture<T> asFuture ()
    {
        final PermissionDeniedException e = asException ();
        if ( e == null )
        {
            return null;
        }
        else
        {
            return new InstantErrorFuture<T> ( e.fillInStackTrace () );
        }
    }

    /**
     * @since 1.1
     */
    public PermissionDeniedException asException ()
    {
        if ( isGranted () )
        {
            return null;
        }
        else
        {
            return new PermissionDeniedException ( this.errorCode, this.message );
        }
    }

    @Override
    public String toString ()
    {
        final StringBuilder sb = new StringBuilder ( "[" );

        if ( this.errorCode == null )
        {
            sb.append ( "GRANTED" );
        }
        else
        {
            sb.append ( String.format ( "%s: %s", this.errorCode, this.message ) );
        }

        sb.append ( "]" );

        return sb.toString ();
    }

}
