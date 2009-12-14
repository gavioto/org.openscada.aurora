package org.openscada.ca.servelt.axis;

import org.openscada.ca.FactoryState;

public class Factory
{
    private String id;

    private String description;

    private FactoryState state;

    private Configuration[] configurations;

    public Factory ( final org.openscada.ca.Factory factory )
    {
        this.id = factory.getId ();
        this.description = factory.getDescription ();
        this.state = factory.getState ();
    }

    public Factory ()
    {
    }

    public void setDescription ( final String description )
    {
        this.description = description;
    }

    public String getDescription ()
    {
        return this.description;
    }

    public void setId ( final String id )
    {
        this.id = id;
    }

    public String getId ()
    {
        return this.id;
    }

    public void setState ( final FactoryState state )
    {
        this.state = state;
    }

    public FactoryState getState ()
    {
        return this.state;
    }

    public void setConfigurations ( final Configuration[] configurations )
    {
        this.configurations = configurations;
    }

    public Configuration[] getConfigurations ()
    {
        return this.configurations;
    }

}
