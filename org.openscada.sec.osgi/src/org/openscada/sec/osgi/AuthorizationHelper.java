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

package org.openscada.sec.osgi;

import java.util.Collection;
import java.util.Map;

import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.AuthorizationService;
import org.openscada.sec.StatusCodes;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationHelper
{
    protected static final AuthorizationResult DEFAULT_RESULT = AuthorizationResult.create ( StatusCodes.AUTHORIZATION_FAILED, Messages.getString ( "AuthorizationHelper.DefaultMessage" ) ); //$NON-NLS-1$

    private final static Logger logger = LoggerFactory.getLogger ( AuthorizationHelper.class );

    private final ServiceTracker<AuthorizationService, AuthorizationService> tracker;

    public AuthorizationHelper ( final BundleContext context )
    {
        this.tracker = new ServiceTracker<AuthorizationService, AuthorizationService> ( context, AuthorizationService.class, null );
    }

    public void open ()
    {
        this.tracker.open ();
    }

    public void close ()
    {
        this.tracker.close ();
    }

    /**
     * Check all authentication services for authorization.
     * <p>
     * This method calls
     * {@link #authorize(String, String, String, UserInformation, AuthorizationResult)}
     * with a default failure if there is not authorization provider or none
     * voted.
     * </p>
     * 
     * @param objectId
     *            the id of the object to check for
     * @param objectType
     *            the object type
     * @param action
     *            the action to perform
     * @param userInformation
     *            the user information or <code>null</code> if there is none
     * @return always returns a result, never returns <code>null</code>
     */
    public AuthorizationResult authorize ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context )
    {
        return authorize ( objectType, objectId, action, userInformation, context, DEFAULT_RESULT );
    }

    public AuthorizationResult authorize ( final AuthorizationRequest request, final AuthorizationResult defaultResult )
    {
        logger.debug ( "Authorizing - {}", request );
        final AuthorizationResult result = authorize ( this.tracker.getTracked ().values (), request, defaultResult );
        logger.debug ( "Authorizing - {} -> {}", request, result );
        return result;
    }

    public AuthorizationResult authorize ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context, final AuthorizationResult defaultResult )
    {
        return authorize ( new AuthorizationRequest ( objectType, objectId, action, userInformation, context ), defaultResult );
    }

    public static AuthorizationResult authorize ( final Collection<? extends AuthorizationService> services, final AuthorizationRequest request, final AuthorizationResult defaultResult )
    {
        if ( services == null )
        {
            return defaultResult;
        }

        for ( final AuthorizationService service : services )
        {
            final AuthorizationResult result = service.authorize ( request.getObjectType (), request.getObjectId (), request.getAction (), request.getUserInformation (), request.getContext () );
            if ( result != null )
            {
                // service did not abstain .. so use the result
                logger.debug ( "Got result ({}). Returning ... ", result ); //$NON-NLS-1$
                return result;
            }
        }

        return defaultResult;
    }
}
