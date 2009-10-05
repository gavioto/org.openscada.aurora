package org.openscada.ca;

import java.util.Map;

import org.openscada.utils.concurrent.NotifyFuture;

/**
 * A self managed configuration factory
 * <p>
 * The configuration factory must guarantee that the order of events
 * is correct when calling the listeners. The listener must be called
 * without any locks held.
 * </p>
 * <p>
 * The factory also applies to configuration to itself. So the configuration
 * information has to include the configuration state and the error information
 * if available.
 * </p>
 * @author Jens Reimann
 *
 */
public interface SelfManagedConfigurationFactory
{
    /**
     * Add a new configuration listener to the factory
     * <p>
     * If the listener is already registered the method has no effect.
     * </p>
     * <p>
     * Configurations that are already known must be sent to the listener
     * once before any other notification is made to the listener.
     * </p>
     * @param listener The new listener to add
     */
    public void addConfigurationListener ( StorageListener listener );

    /**
     * Remove a listener from the factory
     * <p>
     * If the listener is not currently attache to the factory the call has no effect.
     * </p>
     * @param listener
     */
    public void removeConfigurationListener ( StorageListener listener );

    /**
     * Create a configuration
     * <p>
     * The call must also send out the changed or created configuration to the listeners
     * </p>
     * <p>
     * The call must process the future it returns after the configuration is stored and applied.
     * </p>
     * @param configurationId the id of the configuration
     * @param properties the updated or initial properties
     * @return the configuration future
     */

    public NotifyFuture<Configuration> create ( String configurationId, Map<String, String> properties );

    /**
     * Update a configuration
     * <p>
     * The call must also send out the changed or created configuration to the listeners
     * </p>
     * <p>
     * The call must process the future it returns after the configuration is stored and applied.
     * </p>
     * @param configurationId the id of the configuration
     * @param properties the updated or initial properties
     * @return the configuration future
     */
    public NotifyFuture<Configuration> update ( String configurationId, Map<String, String> properties );

    /**
     * Delete a configuration
     * <p>
     * The call must also send out the deleted configuration to the listeners
     * </p>
     * <p>
     * The call must finish the future it returns after the configuration is stored and applied.
     * </p>
     * @param configurationId the configuration id to delete
     * @return the configuration future
     */
    public NotifyFuture<Configuration> delete ( String configurationId );

}
