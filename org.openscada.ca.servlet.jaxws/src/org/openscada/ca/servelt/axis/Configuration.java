package org.openscada.ca.servelt.axis;

import java.util.Map;

import org.openscada.ca.ConfigurationState;

public class Configuration
{
    private String factoryId;

    private String id;

    private ConfigurationState state;

    private Map<String, String> data;

    public Configuration ()
    {
    }

    public Configuration ( final Factory factory, final org.openscada.ca.Configuration configuration )
    {
        this.factoryId = factory.getId ();
        this.id = configuration.getId ();
        this.state = configuration.getState ();
        this.data = configuration.getData ();
    }

    public void setData ( final Map<String, String> data )
    {
        this.data = data;
    }

    public Map<String, String> getData ()
    {
        return this.data;
    }

    public void setState ( final ConfigurationState state )
    {
        this.state = state;
    }

    public ConfigurationState getState ()
    {
        return this.state;
    }

    public void setFactoryId ( final String factoryId )
    {
        this.factoryId = factoryId;
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public void setId ( final String id )
    {
        this.id = id;
    }

    public String getId ()
    {
        return this.id;
    }
}
