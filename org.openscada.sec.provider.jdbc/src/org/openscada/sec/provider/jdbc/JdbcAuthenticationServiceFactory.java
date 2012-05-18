/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.sec.provider.jdbc;

import java.util.Map;

import org.openscada.sec.UserInformation;
import org.openscada.utils.osgi.ca.factory.AbstractServiceConfigurationFactory;
import org.osgi.framework.BundleContext;

public class JdbcAuthenticationServiceFactory extends AbstractServiceConfigurationFactory<JdbcAuthenticationService>
{

    public JdbcAuthenticationServiceFactory ( final BundleContext context )
    {
        super ( context );
    }

    @Override
    protected Entry<JdbcAuthenticationService> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        final JdbcAuthenticationService service = new JdbcAuthenticationService ( context, configurationId );
        service.update ( parameters );
        return new Entry<JdbcAuthenticationService> ( configurationId, service );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String configurationId, final JdbcAuthenticationService service )
    {
        service.dispose ();
    }

    @Override
    protected Entry<JdbcAuthenticationService> updateService ( final UserInformation userInformation, final String configurationId, final Entry<JdbcAuthenticationService> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
