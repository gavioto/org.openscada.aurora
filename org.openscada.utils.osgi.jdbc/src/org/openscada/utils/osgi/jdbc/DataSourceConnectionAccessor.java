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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceConnectionAccessor extends CommonConnectionAccessor
{

    private final static Logger logger = LoggerFactory.getLogger ( DataSourceConnectionAccessor.class );

    private final DataSource dataSource;

    public DataSourceConnectionAccessor ( final DataSourceFactory dataSourceFactory, final Properties paramProperties ) throws SQLException
    {
        logger.debug ( "Creating default data source accessor" );
        this.dataSource = dataSourceFactory.createDataSource ( paramProperties );
    }

    @Override
    public Connection getConnection () throws SQLException
    {
        return this.dataSource.getConnection ();
    }
}
