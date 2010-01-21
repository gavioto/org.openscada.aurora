package org.openscada.ca;

import java.util.Map;

public class ConfigurationDataHelper
{
    private final Map<String, String> data;

    public ConfigurationDataHelper ( final Map<String, String> data )
    {
        this.data = data;
    }

    /**
     * Get an integer from the data or <code>null</code> if
     * the parameter is not set or not an integer
     * @param name the name of the parameter
     * @return the integer or <code>null</code>
     */
    public Integer getInteger ( final String name )
    {
        final String str = this.data.get ( name );
        if ( str == null )
        {
            return null;
        }
        try
        {
            return Integer.parseInt ( str );
        }
        catch ( final NumberFormatException e )
        {
            return null;
        }
    }

    /**
     * Get an integer from the data or the default value if
     * the parameter is not set or not an integer
     * @param name the name of the parameter
     * @param defaultValue the default value
     * @return the integer or the default value
     */
    public int getInteger ( final String name, final int defaultValue )
    {
        final Integer result = getInteger ( name );
        if ( result == null )
        {
            return defaultValue;
        }
        return result;
    }

    public String getString ( final String name )
    {
        return this.data.get ( name );
    }

    public String getString ( final String name, final String defaultValue )
    {
        final String str = this.data.get ( name );
        if ( str != null )
        {
            return str;
        }
        else
        {
            return defaultValue;
        }
    }
}
