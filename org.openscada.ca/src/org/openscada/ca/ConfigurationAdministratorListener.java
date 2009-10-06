package org.openscada.ca;

public interface ConfigurationAdministratorListener
{
    public void factoryEvent ( FactoryEvent event );

    public void configurationEvent ( ConfigurationEvent event );
}
