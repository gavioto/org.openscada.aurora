package org.openscada.ca;

import java.util.Map;

public interface ConfigurationFactory
{
    public void update ( String configurationId, Map<String, String> properties ) throws Exception;

    public void delete ( String configurationId ) throws Exception;
}
