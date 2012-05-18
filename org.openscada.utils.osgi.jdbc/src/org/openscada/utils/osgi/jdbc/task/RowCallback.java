/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.osgi.jdbc.task;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A row callback
 * 
 * @author Jens Reimann
 */
public interface RowCallback
{
    /**
     * Gets called for every row
     * <p>
     * {@link ResultSet#next()} was already called and must not be called by the callback.
     * </p>
     * 
     * @param resultSet
     *            The result holding the data
     * @throws SQLException
     *             the callback may throw an {@link SQLException}
     */
    public void processRow ( ResultSet resultSet ) throws SQLException;
}
