package org.openscada.ca;

import java.util.Map;

public interface SelfManagedConfigurationFactory
{
    public void addConfigurationListener ( ConfigurationListener listener );

    public void removeConfigurationListener ( ConfigurationListener listener );

    public void update ( String configurationId, Map<String, String> properties ) throws Exception;

    public void delete ( String configurationId );
}
