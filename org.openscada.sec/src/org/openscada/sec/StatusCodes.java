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

package org.openscada.sec;

import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;

public interface StatusCodes
{
    public static final StatusCode UNKNOWN_STATUS_CODE = new StatusCode ( "OSSEC", "CORE", 1, SeverityLevel.ERROR );

    public static final StatusCode INVALID_USER_OR_PASSWORD = new StatusCode ( "OSSEC", "CORE", 2, SeverityLevel.ERROR );

    public static final StatusCode AUTHENTICATION_FAILED = new StatusCode ( "OSSEC", "AUTH", 1, SeverityLevel.ERROR );

    public static final StatusCode AUTHORIZATION_FAILED = new StatusCode ( "OSSEC", "AUTH", 2, SeverityLevel.ERROR );

}
