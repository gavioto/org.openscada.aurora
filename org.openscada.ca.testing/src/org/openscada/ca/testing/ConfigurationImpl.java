package org.openscada.ca.testing;

import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationState;

public class ConfigurationImpl implements Configuration
{
    private final String id;

    private final String factoryId;

    private Map<String, String> data;

    public ConfigurationImpl ( final String id, final String factoryId, final Map<String, String> data )
    {
        this.id = id;
        this.factoryId = factoryId;
        this.data = data;
    }

    public Map<String, String> getData ()
    {
        return this.data;
    }

    public Throwable getErrorInformation ()
    {
        return null;
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
        return null;
    }

    public void setData ( final Map<String, String> properties )
    {
        this.data = properties;
    }
}
