package org.openscada.ca;

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

}
