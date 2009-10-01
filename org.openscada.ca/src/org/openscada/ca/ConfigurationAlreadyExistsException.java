package org.openscada.ca;

public class ConfigurationAlreadyExistsException extends Exception
{
    private static final long serialVersionUID = -79878467326178140L;

    private final String factoryId;

    private final String configurationId;

    public ConfigurationAlreadyExistsException ( final String factoryId, final String configurationId )
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
