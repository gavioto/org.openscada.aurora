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

public class CodedException extends Exception implements CodedExceptionBase
{
    private static final long serialVersionUID = 2962144070439177464L;

    protected StatusCode status;

    public CodedException ( final StatusCode statusCode )
    {
        super ();
        this.status = statusCode;
    }

    public CodedException ( final StatusCode statusCode, final String message )
    {
        super ( message );
        this.status = statusCode;
    }

    public CodedException ( final StatusCode statusCode, final Throwable cause )
    {
        super ( cause );
        this.status = statusCode;
    }

    public CodedException ( final StatusCode statusCode, final String message, final Throwable cause )
    {
        super ( message, cause );
        this.status = statusCode;
    }

    public StatusCode getStatus ()
    {
        return this.status;
    }

    /**
     * overrides getMessage to produce a message bearing the assigned statusCode and the 
     * the default Message of this exception type
     */
    @Override
    public String getMessage ()
    {
        final String message = getStatus () + ": " + super.getMessage ();
        return message;
    }

    public String getOriginalMessage ()
    {
        return super.getMessage ();
    }
}
