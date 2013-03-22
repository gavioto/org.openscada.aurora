/*
 * This file is part of the OpenSCADA project
 * 
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

package org.openscada.sec.callback;

import java.util.Map;
import java.util.Properties;

import org.openscada.utils.concurrent.InstantFuture;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A callback handler which will act on the provided properties
 * 
 * @since 1.1
 * @author Jens Reimann
 */
public class PropertiesCredentialsCallback implements CallbackHandler
{

    private static final String PROP_USER = "user";

    private static final String PROP_PASSWORD = "password";

    private final static Logger logger = LoggerFactory.getLogger ( PropertiesCredentialsCallback.class );

    private final Properties props;

    public PropertiesCredentialsCallback ( final String username, final String password )
    {
        this.props = new Properties ();
        this.props.put ( PROP_USER, username );
        this.props.put ( PROP_PASSWORD, password );
    }

    public PropertiesCredentialsCallback ( final Properties props )
    {
        this.props = props;
    }

    public PropertiesCredentialsCallback ( final Map<String, String> properties )
    {
        this.props = new Properties ();
        this.props.putAll ( properties );
    }

    @Override
    public NotifyFuture<Callback[]> performCallback ( final Callback[] callbacks )
    {
        logger.debug ( "Processing callbacks based on properties" );

        for ( final Callback cb : callbacks )
        {
            if ( cb instanceof PasswordCallback && this.props.contains ( PROP_PASSWORD ) )
            {
                logger.debug ( "Answering password: ***" );
                ( (PasswordCallback)cb ).setPassword ( this.props.getProperty ( PROP_PASSWORD ) );
            }
            else if ( cb instanceof UserNameCallback && this.props.contains ( PROP_USER ) )
            {
                logger.debug ( "Answering user: {}", this.props.getProperty ( PROP_USER ) );
                ( (UserNameCallback)cb ).setValue ( this.props.getProperty ( PROP_USER ) );
            }
            else
            {
                cb.cancel ();
            }
        }

        return new InstantFuture<Callback[]> ( callbacks );
    }
}
