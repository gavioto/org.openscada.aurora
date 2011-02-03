/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.util.Collections;
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
    protected static final AuthorizationResult DEFAULT_RESULT = AuthorizationResult.create ( StatusCodes.AUTHORIZATION_FAILED, "No authentication provider voted. Rejecting request!" );

    private final static Logger logger = LoggerFactory.getLogger ( AuthorizationHelper.class );

    private final ServiceTracker tracker;

    public AuthorizationHelper ( final BundleContext context )
    {
        this.tracker = new ServiceTracker ( context, AuthorizationService.class.getName (), null );
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
     * This method calls {@link #authorize(String, String, String, UserInformation, AuthorizationResult)}
     * with a default failure if there is not authorization provider or none voted.
     * </p>
     * @param objectId the id of the object to check for
     * @param objectType the object type
     * @param action the action to perform
     * @param userInformation the user information or <code>null</code> if there is none
     * @return always returns a result, never returns <code>null</code>
     */
    public AuthorizationResult authorize ( final String objectId, final String objectType, final String action, final UserInformation userInformation, final Map<String, Object> context )
    {
        return authorize ( objectId, objectType, action, userInformation, context, DEFAULT_RESULT );
    }

    public AuthorizationResult authorize ( final String objectId, final String objectType, final String action, final UserInformation userInformation, final Map<String, Object> context, final AuthorizationResult defaultResult )
    {
        logger.debug ( "Authorizing - objectType: {}, objectId: {}, action: {}, userInformation: {}, context: {}", new Object[] { objectType, objectId, action, userInformation, context } );

        final Object[] s = this.tracker.getServices ();

        if ( s == null )
        {
            logger.debug ( "No authencation services" );
            return defaultResult;
        }

        final Map<String, Object> unmodiContext;
        if ( context != null )
        {
            unmodiContext = Collections.unmodifiableMap ( context );
        }
        else
        {
            unmodiContext = null;
        }

        for ( final Object service : s )
        {
            if ( ! ( service instanceof AuthorizationService ) )
            {
                logger.info ( "Service does not implement AuthorizationService" );
                continue;
            }
            final AuthorizationResult result = ( (AuthorizationService)service ).authorize ( objectId, objectType, action, userInformation, unmodiContext );
            if ( result != null )
            {
                logger.debug ( "Got result ({}). Returning ... ", result );
                return result;
            }
        }

        return defaultResult;
    }
}
