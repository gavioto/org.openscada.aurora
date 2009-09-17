package org.openscada.utils.osgi;

import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class FilterUtil
{
    public static Filter createAndFilter ( final String clazz, final Map<String, String> parameters ) throws InvalidSyntaxException
    {
        final StringBuilder sb = new StringBuilder ();

        sb.append ( "(&" );

        addPair ( sb, Constants.OBJECTCLASS, clazz );
        for ( final Map.Entry<String, String> entry : parameters.entrySet () )
        {
            addPair ( sb, entry.getKey (), entry.getValue () );
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
