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
    CONFIGURED,
    /**
     * The factory was found but not configuration was available
     */
    FOUND
}
