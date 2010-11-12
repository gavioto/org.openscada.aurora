/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.sec.provider.plain.property;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.openscada.sec.AuthenticationService;
import org.openscada.sec.osgi.plain.AbstractPlainAuthenticationService;

/**
 * A plain authentication provider
 * <p>Authentication data is stored in the following from
 * in the system property <code>org.openscada.sec.provider.plain.property.data</code>:
 * <code>username:password:ROLE1,ROLE2|username2:password2:ROLE1,ROLE2</code>
 * </p>
 * 
 * @author Jens Reimann
 * @since 0.1.0
 */
public class PropertyAuthenticationService extends AbstractPlainAuthenticationService implements AuthenticationService
{
    protected Map<String, UserEntry> userInformation = new HashMap<String, UserEntry> ();

    public PropertyAuthenticationService ()
    {
        final String data = System.getProperty ( "org.openscada.sec.provider.plain.property.data", "" );

        final StringTokenizer tok = new StringTokenizer ( data, "|" );
        while ( tok.hasMoreElements () )
        {
            final String[] toks = tok.nextToken ().split ( ":" );
            if ( toks.length == 3 )
            {
                final String name = toks[0];
                final String password = toks[1];
                final String[] roles = toks[2].split ( "," );
                final UserEntry entry = new UserEntry ( password, Arrays.asList ( roles ) );
                this.userInformation.put ( name, entry );
            }
        }
    }

    @Override
    protected UserEntry getUserEntry ( final String name )
    {
        return this.userInformation.get ( name );
    }

}
