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

package org.openscada.utils.str;

import java.util.Collection;
import java.util.Iterator;

import org.openscada.utils.lang.Apply;

/**
 * A helper class for string operations
 * @author Jens Reimann
 */
public class StringHelper
{
    /**
     * Join a collection of objects to a string using a delimiter.
     * 
     * The method will take the {@link Object#toString()} result of each collection
     * member and add it to the result string. Delimiters are only placed between
     * elements
     * @param items The items to convert
     * @param delimiter the delimiter to use
     * @return the result string
     */
    public static String join ( final Collection<?> items, final String delimiter )
    {
        final StringBuilder buffer = new StringBuilder ();
        final Iterator<?> iter = items.iterator ();

        while ( iter.hasNext () )
        {
            buffer.append ( iter.next () );
            if ( iter.hasNext () )
            {
                buffer.append ( delimiter );
            }
        }

        return buffer.toString ();
    }

    public static String join ( final Collection<?> items, final String delimiter, final Apply<String> toApply )
    {
        final StringBuilder buffer = new StringBuilder ();
        final Iterator<?> iter = items.iterator ();

        while ( iter.hasNext () )
        {
            if ( toApply != null )
            {
                buffer.append ( toApply.apply ( String.valueOf ( iter.next () ) ) );
            }
            else
            {
                buffer.append ( iter.next () );
            }
            if ( iter.hasNext () )
            {
                buffer.append ( delimiter );
            }
        }

        return buffer.toString ();
    }

    /**
     * Join a collection of objects to a string using a delimiter.
     * 
     * The method will take the {@link Object#toString()} result of each collection
     * member and add it to the result string. Delimiters are only placed between
     * elements
     * @param items The items to convert
     * @param delimiter the delimiter to use
     * @return the result string
     */
    public static String join ( final Object[] items, final String delimiter )
    {
        final StringBuilder buffer = new StringBuilder ();

        for ( int i = 0; i < items.length; i++ )
        {
            if ( i != 0 )
            {
                buffer.append ( delimiter );
            }

            buffer.append ( items[i] );
        }

        return buffer.toString ();
    }
}
