package org.openscada.hsdb.backend;

import org.openscada.hsdb.configuration.Configuration;

/**
 * This interface defines methods for accessing back end manager implementations.
 * @author Ludwig Straub
 */
public interface BackEndManagerFactory
{
    /**
     * This method returns all currently available back end manager instances.
     * @return all currently available back end manager instances
     */
    public abstract BackEndManager<?>[] getBackEndManagers ();

    /**
     * This method returns the back end manager that is reliable for the configuration with the specified id.
     * @param configuration configuration the requested back end manager is reliable for
     * @param createIfNotExists flag indicating whether a new back end manager object should be created if it not one already exists for the passed configuration
     * @return back end manager that is reliable for the configuration with the specified id
     */
    public abstract BackEndManager<?> getBackEndManager ( Configuration configuration, boolean createIfNotExists );

    /**
     * This method saves the passed configuration object.
     * @param configuration configuration object that has to be saved
     */
    public abstract void save ( Configuration configuration );

    /**
     * This method deletes the back end manager configuration.
     * @param configuration configuration that has to be deleted
     */
    public abstract void delete ( Configuration configuration );
}
