package org.openscada.ca.common;

import java.util.Map;

public interface StorageManager
{
    public void changeConfiguration ( String configurationId, Map<String, String> properties, ConfigurationFuture future );
}
