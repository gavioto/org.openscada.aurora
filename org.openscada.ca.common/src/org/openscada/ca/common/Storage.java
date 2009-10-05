package org.openscada.ca.common;

import java.util.Map;


public interface Storage
{
    public void store ( String configurationId, Map<String, String> properties, ConfigurationFuture future );

    public void delete ( String configurationId, ConfigurationFuture future );

    public void setStorageManager ( StorageManager manager );
}
