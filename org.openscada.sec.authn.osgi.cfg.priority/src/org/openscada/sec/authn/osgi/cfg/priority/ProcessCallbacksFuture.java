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