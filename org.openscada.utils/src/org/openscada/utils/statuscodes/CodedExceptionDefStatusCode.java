/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.statuscodes;

public abstract class CodedExceptionDefStatusCode extends Exception implements CodedExceptionBase

{

    private static final long serialVersionUID = 2893453354487364387L;

    private StatusCode statusCode;

    public CodedExceptionDefStatusCode ()
    {
        super ();
        setStatusCode ( generateStatusCode () );
    }

    public CodedExceptionDefStatusCode ( final String message )
    {
        super ( message );
        setStatusCode ( generateStatusCode () );
    }

    public CodedExceptionDefStatusCode ( final Throwable cause )
    {
        super ( cause );
        setStatusCode ( generateStatusCode () );
    }

    public CodedExceptionDefStatusCode ( final String message, final Throwable cause )
    {
        super ( message, cause );
        setStatusCode ( generateStatusCode () );
    }

    /**
     * the implementation of setStatusCode must provide the statuscode you want your exception to have.
     * either get it from the local statusCode file (StatusCodes) or create a new statusCode (new StatusCode()).
     */
    protected abstract StatusCode generateStatusCode ();

    private void setStatusCode ( final StatusCode status )
    {
        this.statusCode = status;
    }

    public StatusCode getStatus ()
    {
        return this.statusCode;
    }

    @Override
    public String getMessage ()
    {
        final String message = this.statusCode + ": " + super.getMessage ();
        return message;
    }

    public String getOriginalMessage ()
    {
        return super.getMessage ();
    }

}
