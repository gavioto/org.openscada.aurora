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

import java.util.Map;

import org.openscada.sec.AuthenticationImplementation;
import org.openscada.sec.AuthorizationService;
import org.openscada.sec.authz.AuthorizationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1
 */
public class LogonAuthorizationService implements AuthorizationService
{
    private final static Logger logger = LoggerFactory.getLogger ( LogonAuthorizationService.class );

    private AuthenticationImplementation authenticator;

    public void setAuthenticator ( final AuthenticationImplementation authenticator )
    {
        logger.info ( "Setting authenticator: {}", authenticator );
        this.authenticator = authenticator;
    }

    @Override
    public AuthorizationRule createRule ( final Map<String, String> properties )
    {
        return new AuthorizationRuleImpl ( this.authenticator );
    }

}
