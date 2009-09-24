package org.openscada.ca;

import java.util.Map;

public interface ConfigurationAdministrator
{
    public static final String FACTORY_ID = "factoryId";

    public Configuration createConfiguration ( String factoryId, String configurationId, Map<String, String> initialProperties );

    public Factory getFactory ( String factoryId );

    public Factory[] listKnownFactories ();

    public Configuration[] getConfigurations ( String factoryId );
}
