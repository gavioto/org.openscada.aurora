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

package org.openscada.utils.osgi;

import java.util.Map;
import java.util.Set;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class FilterUtil
{

    protected static Filter createFilter ( final String operand, final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( "(" );
        sb.append ( operand );

        for ( final Map.Entry<String, String> entry : parameters.entrySet () )
        {
            addPair ( sb, entry.getKey (), entry.getValue () );
        }
        sb.append ( ")" );

        return FrameworkUtil.createFilter ( sb.toString () );
    }

    public static Filter createClassFilter ( final String clazz ) throws InvalidSyntaxException
    {
        return FrameworkUtil.createFilter ( String.format ( "(%s=%s)", Constants.OBJECTCLASS, clazz ) );
    }

    public static Filter createAndFilter ( final Filter... filters ) throws InvalidSyntaxException
    {
        return createFilter ( "&", filters );
    }

    public static Filter createOrFilter ( final Filter... filters ) throws InvalidSyntaxException
    {
        return createFilter ( "|", filters );
    }

    protected static Filter createFilter ( final String operand, final Filter... filters ) throws InvalidSyntaxException
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( "(" );
        sb.append ( operand );

        for ( final Filter filter : filters )
        {
            sb.append ( filter.toString () );
        }

        sb.append ( ")" );

        return FrameworkUtil.createFilter ( sb.toString () );
    }

    public static Filter createAndFilter ( final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        return createFilter ( "&", parameters );
    }

    public static Filter createOrFilter ( final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        return createFilter ( "|", parameters );
    }

    public static Filter createAndFilter ( final String clazz, final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        return createAndFilter ( createClassFilter ( clazz ), createAndFilter ( parameters ) );
    }

    public static Filter createSimpleOr ( final String attribute, final Set<String> values ) throws InvalidSyntaxException
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( "(|" );

        for ( final String value : values )
        {
            addPair ( sb, attribute, value );
        }

        sb.append ( ")" );

        return FrameworkUtil.createFilter ( sb.toString () );
    }

    private static void addPair ( final StringBuilder stringBuilder, final String key, final String value )
    {
        stringBuilder.append ( "(" );
        stringBuilder.append ( key );
        stringBuilder.append ( "=" );
        stringBuilder.append ( value );
        stringBuilder.append ( ")" );
    }
}
