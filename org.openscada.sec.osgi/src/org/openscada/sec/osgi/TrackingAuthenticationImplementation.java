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
