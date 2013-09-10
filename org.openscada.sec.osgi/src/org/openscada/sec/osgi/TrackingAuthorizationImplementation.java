/*******************************************************************************
 * Copyright (c) 2013 Jens Reimann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/
package org.openscada.sec.osgi;

import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthorizationImplementation;
import org.openscada.sec.AuthorizationReply;
import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.StatusCodes;
import org.openscada.sec.authz.AuthorizationContext;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public class TrackingAuthorizationImplementation implements AuthorizationImplementation
{
    private final static Logger logger = LoggerFactory.getLogger ( TrackingAuthorizationImplementation.class );

    private final SingleServiceTracker<AuthorizationManager> tracker;

    public TrackingAuthorizationImplementation ( final BundleContext context )
    {
        this.tracker = new SingleServiceTracker<AuthorizationManager> ( context, AuthorizationManager.class, null );
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
    public NotifyFuture<AuthorizationReply> authorize ( final AuthorizationContext context, final AuthorizationResult defaultResult )
    {
        logger.trace ( "Authorizing - {}", context );

        final AuthorizationManager service = this.tracker.getService ();
        if ( service == null )
        {
            logger.info ( "We don't have an authorization manager" );
            return new InstantErrorFuture<AuthorizationReply> ( new AuthenticationException ( StatusCodes.AUTHORIZATION_FAILED, Messages.getString ( "TrackingAuthenticationImplementation.DefaultMessage" ) ) );
        }

        return service.authorize ( context, defaultResult );
    }

}
