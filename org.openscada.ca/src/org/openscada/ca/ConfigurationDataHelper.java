package org.openscada.ca;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigurationDataHelper
{
    private final Map<String, String> data;

    public ConfigurationDataHelper ( final Map<String, String> parameters )
    {
        this.data = parameters;
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

    /**
     * Get an integer from the configuration data
     * <p>
     * If the parameter is not set a {@link IllegalArgumentException} will be thrown. If
     * the data can not be parsed to an integer a {@link NumberFormatException} will
     * be thrown.
     * </p>
     * @param name the name of the parameter to get
     * @param errorMessage the error message if the string is not set
     * @return the value as integer
     * @throws NumberFormatException
     * @throws IllegalArgumentException
     */
    public int getIntegerChecked ( final String name, final String errorMessage ) throws IllegalArgumentException, NumberFormatException
    {
        final String str = this.data.get ( name );
        if ( str == null )
        {
            throw new IllegalArgumentException ( errorMessage );
        }
        return Integer.parseInt ( str );
    }

    /**
     * Get a long from the data or <code>null</code> if
     * the parameter is not set or not a long
     * @param name the name of the parameter
     * @return the long or <code>null</code>
     */
    public Long getLong ( final String name )
    {
        final String str = this.data.get ( name );
        if ( str == null )
        {
            return null;
        }
        try
        {
            return Long.parseLong ( str );
        }
        catch ( final NumberFormatException e )
        {
            return null;
        }
    }

    /**
     * Get a long from the data or the default value if
     * the parameter is not set or not a long
     * @param name the name of the parameter
     * @param defaultValue the default value
     * @return the long or the default value
     */
    public long getLong ( final String name, final long defaultValue )
    {
        final Long result = getLong ( name );
        if ( result == null )
        {
            return defaultValue;
        }
        return result;
    }

    /**
     * Get a long from the configuration data
     * <p>
     * If the parameter is not set a {@link IllegalArgumentException} will be thrown. If
     * the data can not be parsed to a long a {@link NumberFormatException} will
     * be thrown.
     * </p>
     * @param name the name of the parameter to get
     * @param errorMessage the error message if the string is not set
     * @return the value as long
     * @throws NumberFormatException
     * @throws IllegalArgumentException
     */
    public long getLongChecked ( final String name, final String errorMessage ) throws IllegalArgumentException, NumberFormatException
    {
        final String str = this.data.get ( name );
        if ( str == null )
        {
            throw new IllegalArgumentException ( errorMessage );
        }
        return Long.parseLong ( str );
    }

    /**
     * Get a non-empty string from the configuration.
     * @param name the name of the parameters to get
     * @return returns <code>null</code> if the parameter is not set or is an empty string.
     * Otherwise the string itself will be returned.
     */
    public String getStringNonEmpty ( final String name )
    {
        final String result = this.data.get ( name );
        if ( result == null )
        {
            return null;
        }
        if ( "".equals ( result ) )
        {
            return null;
        }
        return result;
    }

    /**
     * Get a string from the configuration
     * @param name the name of the string to get
     * @return the string data or <code>null</code> if the string was not set
     */
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

    /**
     * Get a string from the configuration data
     * @param name the name of the parameter to get
     * @param errorMessage the error message if the string is not set
     * @return the string data
     * @throws IllegalArgumentException if the string is not set
     */
    public String getStringChecked ( final String name, final String errorMessage ) throws IllegalArgumentException
    {
        final String str = this.data.get ( name );
        if ( str == null )
        {
            throw new IllegalArgumentException ( errorMessage );
        }
        else
        {
            return str;
        }
    }

    /**
     * Get an integer from the data or <code>null</code> if
     * the parameter is not set or not an integer
     * @param name the name of the parameter
     * @return the integer or <code>null</code>
     */
    public Double getDouble ( final String name )
    {
        final String str = this.data.get ( name );
        if ( str == null )
        {
            return null;
        }
        try
        {
            return Double.parseDouble ( str );
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
    public double getDouble ( final String name, final double defaultValue )
    {
        final Double result = getDouble ( name );
        if ( result == null )
        {
            return defaultValue;
        }
        return result;
    }

    /**
     * Get an integer from the configuration data
     * <p>
     * If the parameter is not set a {@link IllegalArgumentException} will be thrown. If
     * the data can not be parsed to an integer a {@link NumberFormatException} will
     * be thrown.
     * </p>
     * @param name the name of the parameter to get
     * @param errorMessage the error message if the string is not set
     * @return the value as integer
     * @throws NumberFormatException
     * @throws IllegalArgumentException
     */
    public double getDoubleChecked ( final String name, final String errorMessage ) throws IllegalArgumentException, NumberFormatException
    {
        final String str = this.data.get ( name );
        if ( str == null )
        {
            throw new IllegalArgumentException ( errorMessage );
        }
        return Double.parseDouble ( str );
    }

    /**
     * Get a boolean from the data or <code>null</code> if
     * the parameter is not set or not a boolean
     * @param name the name of the parameter
     * @return the boolean or <code>null</code>
     */
    public Boolean getBoolean ( final String name )
    {
        final String str = this.data.get ( name );
        if ( str == null )
        {
            return null;
        }
        try
        {
            return Boolean.parseBoolean ( str );
        }
        catch ( final NumberFormatException e )
        {
            return null;
        }
    }

    /**
     * Get a boolean from the data or the default value if
     * the parameter is not set or not a boolean
     * @param name the name of the parameter
     * @param defaultValue the default value
     * @return the boolean or the default value
     */
    public boolean getBoolean ( final String name, final boolean defaultValue )
    {
        final Boolean result = getBoolean ( name );
        if ( result == null )
        {
            return defaultValue;
        }
        return result;
    }

    /**
     * Extracts attributes that start with a prefix and return the matching entries
     * only with the key truncated by the prefix.
     * @param prefix the prefix to use
     * @return the result map
     */
    public Map<String, String> getPrefixed ( final String prefix )
    {
        if ( prefix == null )
        {
            return Collections.emptyMap ();
        }

        final int prefixLen = prefix.length ();

        final Map<String, String> result = new HashMap<String, String> ( 8 );

        for ( final Map.Entry<String, String> entry : this.data.entrySet () )
        {
            final String key = entry.getKey ();
            if ( key == null || !key.startsWith ( prefix ) )
            {
                continue;
            }

            final String subKey = key.substring ( prefixLen );
            result.put ( subKey, entry.getValue () );
        }

        return result;
    }
}
