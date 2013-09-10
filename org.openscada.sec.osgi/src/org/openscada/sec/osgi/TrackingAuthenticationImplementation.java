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
import org.openscada.sec.AuthenticationImplementation;
import org.openscada.sec.StatusCodes;
import org.openscada.sec.UserInformation;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;

/**
 * @since 1.1
 */
public class TrackingAuthenticationImplementation implements AuthenticationImplementation
{
    private final SingleServiceTracker<AuthenticationImplementation> tracker;

    public TrackingAuthenticationImplementation ( final BundleContext context )
    {
        this.tracker = new SingleServiceTracker<AuthenticationImplementation> ( context, AuthenticationImplementation.class, null );
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
    public UserInformation getUser ( final String user )
    {
        final AuthenticationImplementation service = this.tracker.getService ();
        if ( service == null )
        {
            return null;
        }
        else
        {
            return service.getUser ( user );
        }
    }

    @Override
    public NotifyFuture<UserInformation> authenticate ( final CallbackHandler callbackHandler )
    {
        final AuthenticationImplementation service = this.tracker.getService ();
        if ( service == null )
        {
            return new InstantErrorFuture<UserInformation> ( new AuthenticationException ( StatusCodes.AUTHORIZATION_FAILED, Messages.getString ( "TrackingAuthenticationImplementation.DefaultMessage" ) ) );
        }

        return service.authenticate ( callbackHandler );
    }

}
