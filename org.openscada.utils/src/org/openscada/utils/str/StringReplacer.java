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

package org.openscada.utils.str;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringReplacer
{

    public static interface ReplaceSource
    {
        public String replace ( String context, String key );
    }

    public static final Pattern DEFAULT_PATTERN = Pattern.compile ( "\\$\\{(.*?)\\}" );

    public static String replace ( final String string, final Hashtable<?, ?> properties )
    {
        return replace ( string, new ReplaceSource () {

            @Override
            public String replace ( final String context, final String key )
            {
                final Object result = properties.get ( key );
                if ( result == null )
                {
                    return context;
                }
                return result.toString ();
            }
        }, DEFAULT_PATTERN );
    }

    public static String replace ( final String string, final ReplaceSource replaceSource, final Pattern pattern )
    {
        if ( string == null )
        {
            return null;
        }

        final Matcher m = pattern.matcher ( string );

        boolean result = m.find ();
        if ( result )
        {
            final StringBuffer sb = new StringBuffer ();
            do
            {

                final String key;
                if ( m.groupCount () > 0 )
                {
                    key = m.group ( 1 );
                }
                else
                {
                    key = null;
                }

                String replacement = replaceSource.replace ( m.group ( 0 ), key );
                if ( replacement == null )
                {
                    m.appendReplacement ( sb, "" );
                }
                else
                {
                    replacement = replacement.replace ( "\\", "\\\\" ).replace ( "$", "\\$" );
                    m.appendReplacement ( sb, replacement );
                }

                result = m.find ();
            } while ( result );
            m.appendTail ( sb );
            return sb.toString ();
        }
        else
        {
            return string;
        }
    }
}
