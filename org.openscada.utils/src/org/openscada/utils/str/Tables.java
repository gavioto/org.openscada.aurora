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

package org.openscada.utils.str;

import java.io.PrintStream;
import java.util.List;

/**
 * @since 1.1
 */
public final class Tables
{
    private Tables ()
    {
    }

    public static void showTable ( final PrintStream out, final List<String> header, final List<List<String>> data, int gap )
    {
        if ( gap < 0 )
        {
            gap = 0;
        }

        int max = header.size ();
        for ( final List<String> row : data )
        {
            max = Math.max ( row.size (), max );
        }

        final int[] lens = new int[max];

        {
            int i = 0;
            for ( final String cell : header )
            {
                if ( cell != null )
                {
                    lens[i] = Math.max ( lens[i], cell.length () );
                }
                i++;
            }
        }

        for ( final List<String> row : data )
        {
            int i = 0;
            for ( final String cell : row )
            {
                if ( cell != null )
                {
                    lens[i] = Math.max ( lens[i], cell.length () );
                }
                i++;
            }
        }

        final String[] formats = new String[max];

        int totalLen = 0;
        for ( int i = 0; i < formats.length; i++ )
        {
            totalLen += lens[i] + gap;
            formats[i] = String.format ( "%%-%ds", lens[i] + gap );
        }

        {
            int i = 0;
            for ( final String cell : header )
            {
                out.print ( String.format ( formats[i], cell ) );
                i++;
            }
            out.println ();
        }

        // header line
        for ( int i = 0; i < totalLen; i++ )
        {
            out.print ( '=' );
        }
        out.println ();

        // data
        for ( final List<String> row : data )
        {
            int i = 0;
            for ( final String cell : row )
            {
                out.print ( String.format ( formats[i], cell == null ? "" : cell ) );
                i++;
            }
            out.println ();
        }
    }

}
