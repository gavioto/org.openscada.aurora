package org.openscada.hsdb.calculation;

import org.openscada.hsdb.StorageChannelMetaData;

/**
 * This interface provides methods for creating calculation logic provider objects.
 * @author Ludwig Straub
 */
public interface CalculationLogicProviderFactory
{
    /**
     * This method creates and returns a calculation logic provider instance that supports the specified configuration.
     * @param metaData configuration that is used when creating the calculation logic provider instance
     * @return created logic provider instance
     */
    public abstract CalculationLogicProvider getCalculationLogicProvider ( StorageChannelMetaData metaData );
}
