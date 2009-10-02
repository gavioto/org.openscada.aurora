package org.openscada.ca;

import java.util.Map;

public interface Configuration
{
    public String getId ();

    public Map<String, String> getData ();

    public String getFactoryId ();

    public ConfigurationState getState ();

    public Throwable getErrorInformation ();
}
