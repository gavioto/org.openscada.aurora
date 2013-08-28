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

package org.openscada.sec.osgi;

import org.openscada.sec.AuthorizationReply;
import org.openscada.sec.AuthorizationRequest;
import org.openscada.sec.audit.AuditLogService;
import org.openscada.sec.authz.AuthorizationContext;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @since 1.1
 */
public class TrackingAuditLogImplementation implements AuditLogService
{
    private final SingleServiceTracker<AuditLogService> tracker;

    private volatile AuditLogService service;

    private final SingleServiceListener<AuditLogService> listener = new SingleServiceListener<AuditLogService> () {

        @Override
        public void serviceChange ( final ServiceReference<AuditLogService> reference, final AuditLogService service )
        {
            setService ( service );
        }
    };

    public TrackingAuditLogImplementation ( final BundleContext context )
    {
        this.tracker = new SingleServiceTracker<AuditLogService> ( context, AuditLogService.class, this.listener );
    }

    protected void setService ( final AuditLogService service )
    {
        this.service = service;
    }

    public void open ()
    {
        this.tracker.open ();
    }

    public void close ()
    {
        this.tracker.close ();
    }

    @Override
    public void info ( final String message, final Object... arguments )
    {
        checkService ().info ( message, arguments );
    }

    @Override
    public void debug ( final String message, final Object... arguments )
    {
        checkService ().debug ( message, arguments );
    }

    @Override
    public void info ( final String message, final Throwable e, final Object... arguments )
    {
        checkService ().info ( message, e, arguments );
    }

    @Override
    public void debug ( final String message, final Throwable e, final Object... arguments )
    {
        checkService ().debug ( message, e, arguments );
    }

    @Override
    public void authorizationRequested ( final AuthorizationRequest request )
    {
        checkService ().authorizationRequested ( request );
    }

    @Override
    public void authorizationFailed ( final AuthorizationContext context, final AuthorizationRequest request, final Throwable error )
    {
        checkService ().authorizationFailed ( context, request, error );
    }

    @Override
    public void authorizationDone ( final AuthorizationContext context, final AuthorizationRequest request, final AuthorizationReply reply )
    {
        checkService ().authorizationDone ( context, request, reply );
    }

    protected AuditLogService checkService ()
    {
        final AuditLogService service = this.service;
        if ( service != null )
        {
            return service;
        }
        throw new IllegalStateException ( String.format ( "No audit log service found" ) );
    }

}
