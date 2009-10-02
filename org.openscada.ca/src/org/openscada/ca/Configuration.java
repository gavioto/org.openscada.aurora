package org.openscada.ca;


public interface Configuration extends ConfigurationData
{
    public String getFactoryId ();

    public ConfigurationState getState ();

    public Throwable getErrorInformation ();

}
