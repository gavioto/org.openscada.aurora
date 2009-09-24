package org.openscada.ca;

public enum ConfigurationState
{
    /**
     * The configuration is available but not applied to a service
     */
    AVAILABLE,
    /**
     * The configuration was available and successfully applied
     */
    APPLIED,
    /**
     * The configuration was available but could not be applied to the service
     */
    ERROR
}
