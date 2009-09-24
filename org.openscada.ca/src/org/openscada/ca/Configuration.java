package org.openscada.ca;

import java.util.Map;

public interface Configuration
{
    public Factory getFactory ();

    public String getId ();

    public ConfigurationState getState ();

    public Throwable getErrorInformation ();

    public void delete ();

    public void update ( Map<String, String> properties );

    public Map<String, String> getData ();
}
