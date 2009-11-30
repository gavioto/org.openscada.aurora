/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
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
