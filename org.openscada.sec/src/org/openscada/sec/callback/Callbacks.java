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

package org.openscada.sec.callback;

import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public final class Callbacks
{

    private final static Logger logger = LoggerFactory.getLogger ( Callbacks.class );

    private Callbacks ()
    {
    }

    /**
     * Process the callbacks, even if the callbackHandler is <code>null</code>
     * <p>
     * If the provided callback handler is <code>null</code> then all callbacks
     * will automatically be canceled and an {@link InstantFuture} is returned.
     * </p>
     * 
     * @param callbackHandler
     *            the callback handler to use (optional)
     * @param callbacks
     *            the callbacks to process
     * @return the future
     */
    public static NotifyFuture<Callback[]> callback ( final CallbackHandler callbackHandler, final Callback[] callbacks )
    {
        if ( callbacks == null )
        {
            return new InstantFuture<Callback[]> ( callbacks );
        }

        if ( callbackHandler == null )
        {
            for ( final Callback callback : callbacks )
            {
                logger.debug ( "Cancelling callback: {}", callback );
                if ( callback != null )
                {
                    callback.cancel ();
                }
            }
            return new InstantFuture<Callback[]> ( callbacks );
        }
        else
        {
            return callbackHandler.performCallback ( callbacks );
        }
    }

    public static NotifyFuture<Callback[]> callback ( final CallbackHandler callbackHandler, final Callback callback )
    {
        return callback ( callbackHandler, new Callback[] { callback } );
    }

    /**
     * Cancel all callbacks
     * 
     * @param callbacks
     *            the callbacks to cancel
     * @return the future
     */
    public static NotifyFuture<Callback[]> cancelAll ( final Callback[] callbacks )
    {
        if ( callbacks == null )
        {
            return new InstantFuture<Callback[]> ( callbacks );
        }

        for ( final Callback callback : callbacks )
        {
            logger.debug ( "Cancelling callback: {}", callback );
            callback.cancel ();
        }

        return new InstantFuture<Callback[]> ( callbacks );
    }
}
