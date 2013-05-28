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

import java.util.concurrent.Future;

import org.openscada.sec.AuthorizationReply;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.StatusCodes;
import org.openscada.sec.authz.AuthorizationContext;
import org.openscada.sec.authz.AuthorizationRule;
import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.IteratingFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.concurrent.TransformResultFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationHelper
{
    public static final class IteratingAuthorizer extends IteratingFuture<AuthorizationResult, AuthorizationRule>
    {
        private final AuthorizationResult defaultResult;

        private final AuthorizationContext context;

        public IteratingAuthorizer ( final Iterable<? extends AuthorizationRule> iterable, final AuthorizationResult defaultResult, final AuthorizationContext context )
        {
            super ( iterable );
            this.defaultResult = defaultResult;
            this.context = context;
        }

        @Override
        protected void handleComplete ( final Future<AuthorizationResult> future, final AuthorizationRule current ) throws Exception
        {
            final AuthorizationResult reply = future.get ();
            if ( reply == null )
            {
                // abstain

                logger.debug ( "We got an abstain" );
                processNext ();
            }
            else
            {
                // rejected or granted

                logger.debug ( "Somebody voted: {}", reply );
                setResult ( reply );
            }
        }

        @Override
        protected NotifyFuture<AuthorizationResult> perform ( final AuthorizationRule s )
        {
            logger.debug ( "Try next service: {}", s );
            return s.authorize ( this.context );
        }

        @Override
        protected NotifyFuture<AuthorizationResult> last ()
        {
            return new InstantFuture<AuthorizationResult> ( this.defaultResult );
        }
    }

    public static final AuthorizationResult DEFAULT_RESULT = AuthorizationResult.createReject ( StatusCodes.AUTHORIZATION_FAILED, Messages.getString ( "AuthorizationHelper.DefaultMessage" ) ); //$NON-NLS-1$

    private final static Logger logger = LoggerFactory.getLogger ( AuthorizationHelper.class );

    public static NotifyFuture<AuthorizationReply> authorize ( final Iterable<? extends AuthorizationRule> services, final AuthorizationContext context, final AuthorizationResult defaultResult )
    {
        logger.debug ( "Iterating authorize - {}", context.getRequest () );

        final IteratingFuture<AuthorizationResult, AuthorizationRule> future = new IteratingAuthorizer ( services, defaultResult, context );

        future.startIterating ();

        return new TransformResultFuture<AuthorizationResult, AuthorizationReply> ( future ) {

            @Override
            protected AuthorizationReply transform ( final AuthorizationResult from ) throws Exception
            {
                logger.debug ( "Transforming result: {}", from );
                if ( from == null )
                {
                    return AuthorizationReply.create ( defaultResult, context );
                }
                else
                {
                    return AuthorizationReply.create ( from, context );
                }
            }

        };
    }

}
