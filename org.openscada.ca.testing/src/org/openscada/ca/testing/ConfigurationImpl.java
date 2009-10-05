package org.openscada.ca.testing;

import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationState;

public class ConfigurationImpl implements Configuration
{
    private final String id;

    private final String factoryId;

    private Map<String, String> data;

    private final ConfigurationState state;

    private final Throwable errorInformation;

    public ConfigurationImpl ( final String id, final String factoryId, final Map<String, String> data, final ConfigurationState state, final Throwable errorInformation )
    {
        this.id = id;
        this.factoryId = factoryId;
        this.data = data;
        this.state = state;
        this.errorInformation = errorInformation;
    }

    public Map<String, String> getData ()
    {
        return this.data;
    }

    public Throwable getErrorInformation ()
    {
        return this.errorInformation;
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public String getId ()
    {
        return this.id;
    }

    public ConfigurationState getState ()
    {
        return this.state;
    }

    public void setData ( final Map<String, String> properties )
    {
        this.data = properties;
    }
}
