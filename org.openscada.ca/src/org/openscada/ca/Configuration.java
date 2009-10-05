package org.openscada.ca;

import java.util.Map;

public interface Configuration
{
    public String getFactoryId ();

    public String getId ();

    public ConfigurationState getState ();

    public Throwable getErrorInformation ();

    public Map<String, String> getData ();
}
