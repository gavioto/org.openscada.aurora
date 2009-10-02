package org.openscada.ca.common;

import java.util.Map;
import java.util.concurrent.Future;

import org.openscada.ca.Configuration;
import org.openscada.ca.StorageListener;

public interface Storage
{
    public Future<Configuration> store ( String configurationId, Map<String, String> properties );

    public Future<Configuration> delete ( String configurationId );

    public void addConfigurationListener ( StorageListener listener );

    public void removeConfigurationListener ( StorageListener listener );
}
