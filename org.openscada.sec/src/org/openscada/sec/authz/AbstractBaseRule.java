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

package org.openscada.sec.authz;

import java.util.Map;
import java.util.regex.Pattern;

import org.openscada.sec.AuthorizationRequest;
import org.openscada.sec.AuthorizationResult;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public abstract class AbstractBaseRule implements AuthorizationRule
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractBaseRule.class );

    private Pattern objectId;

    private Pattern objectType;

    private Pattern action;

    @Override
    public void dispose ()
    {
        // no-op
    }

    public void setPreFilter ( final Map<String, String> properties )
    {
        if ( properties != null )
        {
            setPreFilter ( properties.get ( "for.id" ), properties.get ( "for.type" ), properties.get ( "for.action" ) );
        }
    }

    public void setPreFilter ( final String idFilter, final String typeFilter, final String actionFilter )
    {
        if ( idFilter != null )
        {
            this.objectId = Pattern.compile ( idFilter );
        }
        else
        {
            this.objectId = null;
        }

        if ( typeFilter != null )
        {
            this.objectType = Pattern.compile ( typeFilter );
        }
        else
        {
            this.objectType = null;
        }

        if ( actionFilter != null )
        {
            this.action = Pattern.compile ( actionFilter );
        }
        else
        {
            this.action = null;
        }
    }

    @Override
    public NotifyFuture<AuthorizationResult> authorize ( final AuthorizationContext context )
    {
        final AuthorizationRequest request = context.getRequest ();
        logger.debug ( "Checking authentication - objectType: {}, objectId: {}, action: {}, user: {}, context: {}", new Object[] { request.getObjectType (), request.getObjectId (), request.getAction (), request.getUserInformation (), request.getContext () } ); //$NON-NLS-1$
        logger.debug ( "Pre-Filter - objectType: {}, objectId: {}, action: {}", new Object[] { this.objectType, this.objectId, this.action } ); //$NON-NLS-1$

        if ( this.objectId != null && !this.objectId.matcher ( request.getObjectId () ).matches () )
        {
            return new InstantFuture<AuthorizationResult> ( null );
        }

        if ( this.objectType != null && !this.objectType.matcher ( request.getObjectType () ).matches () )
        {
            return new InstantFuture<AuthorizationResult> ( null );
        }

        if ( this.action != null && !this.action.matcher ( request.getAction () ).matches () )
        {
            return new InstantFuture<AuthorizationResult> ( null );
        }

        return procesAuthorize ( context );
    }

    protected abstract NotifyFuture<AuthorizationResult> procesAuthorize ( AuthorizationContext context );

}
