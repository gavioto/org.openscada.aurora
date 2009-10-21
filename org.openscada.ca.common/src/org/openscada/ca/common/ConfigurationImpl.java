package org.openscada.ca.common;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.Configuration;
import org.openscada.ca.ConfigurationState;

public class ConfigurationImpl implements Configuration
{

    private final String id;

    private Map<String, String> data;

    private final String factoryId;

    private ConfigurationState state;

    private Throwable error;

    public ConfigurationImpl ( final String id, final String factoryId, final Map<String, String> data )
    {
        this.id = id;
        this.factoryId = factoryId;
        this.data = new HashMap<String, String> ( data );
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public Map<String, String> getData ()
    {
        return this.data;
    }

    public Throwable getErrorInformation ()
    {
        return this.error;
    }

    public String getId ()
    {
        return this.id;
    }

    public ConfigurationState getState ()
    {
        return this.state;
    }

    public void setData ( final Map<String, String> data )
    {
        this.data = data;
    }

    public void setState ( final ConfigurationState state, final Throwable e )
    {
        this.state = state;
        this.error = e;
    }

}
