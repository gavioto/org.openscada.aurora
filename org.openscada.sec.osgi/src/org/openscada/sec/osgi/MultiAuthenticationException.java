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

package org.openscada.sec.osgi;

import org.openscada.sec.AuthenticationException;

public class MultiAuthenticationException extends AuthenticationException
{
    /**
     * 
     */
    private static final long serialVersionUID = 6694894598825160903L;

    private final AuthenticationException[] causes;

    public MultiAuthenticationException ( final String message, final AuthenticationException[] causes )
    {
        super ( StatusCodes.AUTHENTICATION_FAILED, message );
        this.causes = causes;

    }

    public AuthenticationException[] getCauses ()
    {
        return this.causes;
    }

}
