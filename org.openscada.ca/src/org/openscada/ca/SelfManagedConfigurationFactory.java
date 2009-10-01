package org.openscada.ca;

import java.util.Map;

public interface SelfManagedConfigurationFactory
{
    /**
     * Add a new configuration listener to the factory
     * <p>
     * If the listener is already registered the method has no effect.
     * </p>
     * <p>
     * The listener is added to the factory and will receive updates from now on.
     * Before the method return it will call the listener once with the already
     * known configuration element using an "add" notification.
     * </p> 
     * @param listener The new listener to add
     */
    public void addConfigurationListener ( ConfigurationListener listener );

    /**
     * Remove a listener from the factory
     * <p>
     * If the listener is not currently attache to the factory the call has no effect.
     * </p>
     * @param listener
     */
    public void removeConfigurationListener ( ConfigurationListener listener );

    public void update ( String configurationId, Map<String, String> properties ) throws Exception;

    public void delete ( String configurationId );
}
