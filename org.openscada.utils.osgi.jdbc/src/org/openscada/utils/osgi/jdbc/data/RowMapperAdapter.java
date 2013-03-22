/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jürgen Rose (cptmauli@googlemail.com)
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

package org.openscada.utils.osgi.jdbc.data;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * just a convenience class which provides a default implementation for
 * validate, which might be empty in most cases, and nullObject which would be
 * null if thats OK in context of caller
 * 
 * @param <T>
 */
public abstract class RowMapperAdapter<T> implements RowMapper<T>
{
    @Override
    public void validate ( final ResultSet resultSet ) throws SQLException, RowMapperValidationException
    {
        // do nothing
    }

    @Override
    public T nullObject ()
    {
        return null;
    }
}
