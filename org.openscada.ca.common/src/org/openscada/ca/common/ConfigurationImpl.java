package org.openscada.ca.common;

import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationState;

public class ConfigurationImpl implements Configuration
{

    private final String id;

    private final String factoryId;

    private final Map<String, String> data;

    private final ConfigurationState state;

    private final Throwable errorInformation;

    public ConfigurationImpl ( final Configuration cfg )
    {
        this.id = cfg.getId ();
        this.factoryId = cfg.getFactoryId ();
        this.data = cfg.getData ();
        this.state = cfg.getState ();
        this.errorInformation = cfg.getErrorInformation ();
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

}
