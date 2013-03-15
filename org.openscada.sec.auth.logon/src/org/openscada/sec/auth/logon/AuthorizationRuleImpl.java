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

package org.openscada.sec.auth.logon;

import java.util.concurrent.Future;

import org.openscada.sec.AuthenticationImplementation;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.UserInformation;
import org.openscada.sec.authz.AbstractBaseRule;
import org.openscada.sec.authz.AuthorizationContext;
import org.openscada.utils.concurrent.CallingFuture;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthorizationRuleImpl extends AbstractBaseRule
{

    private final static Logger logger = LoggerFactory.getLogger ( AuthorizationRuleImpl.class );

    private final AuthenticationImplementation authenticator;

    public AuthorizationRuleImpl ( final AuthenticationImplementation authenticator )
    {
        this.authenticator = authenticator;
    }

    @Override
    protected NotifyFuture<AuthorizationResult> procesAuthorize ( final AuthorizationContext context )
    {
        logger.debug ( "Authorizing - request: {}", context.getRequest () );

        final UserInformation userInformation = context.getRequest ().getUserInformation ();
        if ( userInformation != null && !userInformation.isAnonymous () )
        {
            // no need to log on anything
            return new InstantFuture<AuthorizationResult> ( null );
        }

        final NotifyFuture<UserInformation> future = this.authenticator.authenticate ( context.getCallbackHandler () );
        return new CallingFuture<UserInformation, AuthorizationResult> ( future ) {

            @Override
            public AuthorizationResult call ( final Future<UserInformation> future ) throws Exception
            {
                final AuthorizationResult result = process ( context, future );
                logger.debug ( "Result of authorize call: {}", result );
                return result;
            }
        };
    }

    protected AuthorizationResult process ( final AuthorizationContext context, final Future<UserInformation> future ) throws Exception
    {
        try
        {
            context.changeUserInformation ( future.get () );
            return null;
        }
        catch ( final Exception e )
        {
            logger.debug ( "Failed to authorize logon", e );
            return AuthorizationResult.createReject ( e );
        }
    }

}