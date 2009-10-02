package org.openscada.ca;

public enum FactoryState
{
    /**
     * The configuration for this factory was loaded but not applied
     */
    LOADED,
    /**
     * The configuration was loaded and applied
     */
    BOUND,
    /**
     * The configuration as loaded, the service became available and
     * the loaded configuration is currently being applied.
     */
    BINDING
}
