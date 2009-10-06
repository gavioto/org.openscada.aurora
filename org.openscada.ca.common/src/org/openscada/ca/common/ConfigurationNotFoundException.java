package org.openscada.ca.common;

public class ConfigurationNotFoundException extends Exception
{

    private static final long serialVersionUID = -2963015883731497468L;

    private final String factoryId;

    private final String configurationId;

    public ConfigurationNotFoundException ( final String factoryId, final String configurationId )
    {
        this.factoryId = factoryId;
        this.configurationId = configurationId;
    }

    public String getConfigurationId ()
    {
        return this.configurationId;
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

}
