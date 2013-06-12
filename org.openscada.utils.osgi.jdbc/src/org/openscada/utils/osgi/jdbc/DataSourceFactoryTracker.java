/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.osgi.jdbc;

import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.jdbc.DataSourceFactory;

public class DataSourceFactoryTracker extends SingleServiceTracker<DataSourceFactory>
{

    private final String driver;

    public DataSourceFactoryTracker ( final BundleContext context, final String driver, final SingleServiceListener<DataSourceFactory> listener ) throws InvalidSyntaxException
    {
        super ( context, context.createFilter ( "(&(objectClass=" + DataSourceFactory.class.getName () + ")(" + DataSourceFactory.OSGI_JDBC_DRIVER_CLASS + "=" + driver + "))" ), listener );
        this.driver = driver;
    }

    public DataSourceFactoryTracker ( final BundleContext context, final String driver, final SingleServiceListener<DataSourceFactory> listener, final boolean isConnectionPool ) throws InvalidSyntaxException
    {
        super ( context, context.createFilter ( "(&(objectClass=" + DataSourceFactory.class.getName () + ")(" + DataSourceFactory.OSGI_JDBC_DRIVER_CLASS + "=" + driver + ")(isConnectionPool=" + isConnectionPool + "))" ), listener );
        this.driver = driver;
    }

    public String getDriver ()
    {
        return this.driver;
    }

}
