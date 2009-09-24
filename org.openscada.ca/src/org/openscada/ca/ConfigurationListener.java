package org.openscada.ca;

public interface ConfigurationListener
{
    public void factoryEvent ( FactoryEvent event );

    public void configurationEvent ( ConfigurationEvent event );
}
