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

package org.openscada.utils.osgi.jdbc;

import java.util.Map;
import java.util.Properties;

public final class DataSourceHelper
{
    private DataSourceHelper ()
    {
    }

    public static Properties getDataSourceProperties ( final String specificPrefix, final String defaultPrefix )
    {
        final Properties p = new Properties ();

        String prefix;
        if ( System.getProperties ().containsKey ( specificPrefix + ".driver" ) )
        {
            prefix = specificPrefix + ".";
        }
        else
        {
            prefix = defaultPrefix + ".";
        }

        for ( final Map.Entry<Object, Object> entry : System.getProperties ().entrySet () )
        {
            if ( entry.getKey () == null )
            {
                continue;
            }
            final String key = entry.getKey ().toString ();
            if ( key.startsWith ( prefix ) )
            {
                // remove prefix and add as property
                p.put ( key.substring ( prefix.length () ), entry.getValue () );
            }
        }

        return p;
    }

}
