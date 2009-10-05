package org.openscada.ca;

import java.util.Map;
import java.util.concurrent.Future;

public interface ConfigurationAdministrator
{
    public static final String FACTORY_ID = "factoryId";

    /* modifiers */

    public Future<Configuration> createConfiguration ( String factoryId, String configurationId, Map<String, String> initialProperties );

    public Future<Configuration> updateConfiguration ( String factoryId, String configurationId, Map<String, String> newProperties );

    public Future<Configuration> deleteConfiguration ( String factoryId, String configurationId );

    /* readers */

    public Factory getFactory ( String factoryId );

    public Factory[] getKnownFactories ();

    public Configuration[] getConfigurations ( String factoryId );

    public Configuration getConfiguration ( String factoryId, String configurationId );
}
