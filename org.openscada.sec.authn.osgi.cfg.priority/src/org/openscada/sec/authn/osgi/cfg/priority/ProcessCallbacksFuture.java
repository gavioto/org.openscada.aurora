/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassid.de)
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

package org.openscada.sec.authn.osgi.cfg.priority;

import java.util.List;

import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthenticationService;
import org.openscada.sec.UserInformation;
import org.openscada.sec.authn.CredentialsRequest;
import org.openscada.sec.callback.Callback;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.concurrent.TransformResultFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public abstract class ProcessCallbacksFuture extends TransformResultFuture<Callback[], UserInformation>
{
    private final static Logger logger = LoggerFactory.getLogger ( ProcessCallbacksFuture.class );

    public ProcessCallbacksFuture ( final NotifyFuture<Callback[]> fromFuture )
    {
        super ( fromFuture );
    }

    protected abstract CredentialsRequest getRequest ();

    protected abstract List<AuthenticationService> getServices ();

    @Override
    protected UserInformation transform ( final Callback[] from ) throws Exception
    {
        logger.debug ( "Processing reply - callbacks: {}", new Object[] { from } );

        return processCallbacks ( getRequest (), getServices () );
    }

    protected UserInformation processCallbacks ( final CredentialsRequest request, final List<AuthenticationService> services ) throws AuthenticationException
    {
        for ( final AuthenticationService service : services )
        {
            final UserInformation userInformation = service.authenticate ( request );
            if ( userInformation != null )
            {
                return userInformation;
            }
        }

        // if we don't find a user ... return nothing ... outer shell needs to fail for us
        return null;
    }
}