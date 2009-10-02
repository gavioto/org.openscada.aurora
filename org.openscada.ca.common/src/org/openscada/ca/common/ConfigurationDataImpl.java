package org.openscada.ca.common;

import java.util.Map;

import org.openscada.ca.ConfigurationData;

public class ConfigurationDataImpl implements ConfigurationData
{
    private final String id;

    private final Map<String, String> data;

    public ConfigurationDataImpl ( final String id, final Map<String, String> data )
    {
        this.id = id;
        this.data = data;
    }

    public String getId ()
    {
        return this.id;
    }

    public Map<String, String> getData ()
    {
        return this.data;
    }
}
