/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.sec.audit;

import org.openscada.sec.AuthorizationReply;
import org.openscada.sec.AuthorizationRequest;
import org.openscada.sec.authz.AuthorizationContext;

/**
 * @since 1.1
 */
public interface AuditLogService
{

    public void info ( String message, Object... arguments );

    public void debug ( String message, Object... arguments );

    public void info ( String message, Throwable e, Object... arguments );

    public void debug ( String message, Throwable e, Object... arguments );

    public void authorizationRequested ( AuthorizationRequest request );

    public void authorizationFailed ( AuthorizationContext context, AuthorizationRequest request, Throwable error );

    public void authorizationDone ( AuthorizationContext context, AuthorizationRequest request, AuthorizationReply reply );
}
