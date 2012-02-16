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

import org.openscada.utils.osgi.jdbc.task.ConnectionTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommonConnectionAccessor implements ConnectionAccessor
{

    private final static Logger logger = LoggerFactory.getLogger ( CommonConnectionAccessor.class );

    /**
     * Get a new connection that must be closed by the caller
     * @return a new open connection
     * @throws SQLException if anything goes wrong
     */
    public abstract Connection getConnection () throws SQLException;

    @Override
    public <R> R doWithConnection ( final ConnectionTask<R> connectionTask )
    {
        try
        {
            final Connection connection = getConnection ();
            try
            {
                return connectionTask.performTask ( connection );
            }
            finally
            {
                if ( connection != null )
                {
                    try
                    {
                        connection.close ();
                    }
                    catch ( final SQLException e )
                    {
                        logger.warn ( "Failed to close connection", e );
                    }
                }
            }
        }
        catch ( final Exception e )
        {
            throw new RuntimeException ( e );
        }
    }

    @Override
    public void dispose ()
    {
    }

}
