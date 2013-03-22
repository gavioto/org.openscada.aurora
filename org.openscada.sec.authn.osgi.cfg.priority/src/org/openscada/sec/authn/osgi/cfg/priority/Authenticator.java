/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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
import java.util.concurrent.Future;

import org.openscada.sec.AuthenticationException;
import org.openscada.sec.AuthenticationImplementation;
import org.openscada.sec.AuthenticationService;
import org.openscada.sec.StatusCodes;
import org.openscada.sec.UserInformation;
import org.openscada.sec.authn.CredentialsRequest;
import org.openscada.sec.callback.Callback;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.sec.callback.Callbacks;
import org.openscada.utils.concurrent.InstantErrorFuture;
import org.openscada.utils.concurrent.IteratingFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public class Authenticator implements AuthenticationImplementation
{

    private final class GroupIteratorFuture extends IteratingFuture<UserInformation, ConfigurationGroup>
    {
        private final CallbackHandler callbackHandler;

        private int counter = 1;

        public GroupIteratorFuture ( final Iterable<ConfigurationGroup> iterable, final CallbackHandler callbackHandler )
        {
            super ( iterable );
            this.callbackHandler = callbackHandler;
        }

        @Override
        protected void handleComplete ( final Future<UserInformation> future, final ConfigurationGroup group ) throws Exception
        {
            logger.debug ( "Handle complete - future: {}, group: {}", future, group );

            final UserInformation userInformation = future.get ();
            if ( userInformation != null )
            {
                setResult ( userInformation );
            }
            else
            {
                if ( this.counter < group.getRetries () )
                {
                    logger.debug ( "Retry current group - retry #{} of {}", this.counter, group.getRetries () );
                    this.counter++;
                    processCurrent ();
                }
                else
                {
                    logger.debug ( "Try next authorization group" );
                    this.counter = 1;
                    processNext ();
                }
            }
        }

        @Override
        protected NotifyFuture<UserInformation> perform ( final ConfigurationGroup group )
        {
            logger.debug ( "Process group: {}", group );
            return authenticate ( this.callbackHandler, group );
        }

        @Override
        protected NotifyFuture<UserInformation> last ()
        {
            return new InstantErrorFuture<UserInformation> ( new AuthenticationException ( StatusCodes.INVALID_USER_OR_PASSWORD ) );
        }
    }

    private final static Logger logger = LoggerFactory.getLogger ( Authenticator.class );

    private ConfigurationManagerImpl manager;

    public void activate ( final BundleContext context )
    {
        this.manager = new ConfigurationManagerImpl ( context );
    }

    public void deactivate ()
    {
        this.manager.dispose ();
    }

    @Override
    public UserInformation getUser ( final String user )
    {
        logger.debug ( "Getting user information - {}", user );

        final Configuration cfg = this.manager.getConfiguration ();

        for ( final ConfigurationGroup group : cfg.getGroups () )
        {
            for ( final AuthenticationService service : group.getServices () )
            {
                final UserInformation result = service.getUser ( user );
                if ( result != null )
                {
                    logger.debug ( "Found user information from service - service: {}, user: {}", service, result );
                    return result;
                }
            }
        }

        logger.debug ( "None found" );
        return null;
    }

    @Override
    public NotifyFuture<UserInformation> authenticate ( final CallbackHandler callbackHandler )
    {
        logger.debug ( "Start authenticating - callbackHandler: {}", callbackHandler );

        final Configuration configuration;
        try
        {
            configuration = this.manager.getConfiguration ();
            if ( configuration == null )
            {
                return new InstantErrorFuture<UserInformation> ( new AuthenticationException ( StatusCodes.AUTHENTICATION_FAILED, "No authentication configuration available" ) );
            }
        }
        catch ( final Exception e )
        {
            return new InstantErrorFuture<UserInformation> ( new AuthenticationException ( StatusCodes.AUTHENTICATION_FAILED, e ) );
        }

        return new GroupIteratorFuture ( configuration.getGroups (), callbackHandler ).startIterating ();
    }

    protected NotifyFuture<UserInformation> authenticate ( final CallbackHandler callbackHandler, final ConfigurationGroup group )
    {
        final CredentialsRequest request = new CredentialsRequest ();

        for ( final AuthenticationService service : group.getServices () )
        {
            logger.debug ( "Let service join: {}", service );
            service.joinRequest ( request );
        }

        final NotifyFuture<Callback[]> future = Callbacks.callback ( callbackHandler, request.buildCallbacks () );

        return new ProcessCallbacksFuture ( future ) {
            @Override
            protected CredentialsRequest getRequest ()
            {
                return request;
            }

            @Override
            protected List<AuthenticationService> getServices ()
            {
                return group.getServices ();
            }
        };
    }
}
