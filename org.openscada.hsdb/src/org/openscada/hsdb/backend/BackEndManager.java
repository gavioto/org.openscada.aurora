package org.openscada.hsdb.backend;

import java.util.Map;

import org.openscada.hsdb.CalculatingStorageChannel;
import org.openscada.hsdb.ExtendedStorageChannel;
import org.openscada.hsdb.calculation.CalculationLogicProvider;
import org.openscada.hsdb.calculation.CalculationMethod;
import org.openscada.hsdb.configuration.Configuration;

/**
 * This class provides methods for handling and distributing back end objects within an application.
 * @param <B> sub class of BackEnd that will be managed by the instance
 * @author Ludwig Straub
 */
public interface BackEndManager<B extends BackEnd>
{
    /**
     * This method initializes the manager object.
     * @throws Exception if the instance could not be initialized and therefore cannot be used.
     */
    public abstract void initialize () throws Exception;

    /**
     * This method returns the configuration of the manager instance.
     * @return configuration of the manager instance
     */
    public abstract Configuration getConfiguration ();

    /**
     * This method returns the factory that has been used to create this instance.
     * @return factory that has been used to create this instance
     */
    public abstract BackEndManagerFactory getBackEndManagerFactory ();

    /**
     * This method returns the factory that can be used to create new back end objects.
     * @return factory that can be used to create new back end objects
     */
    public abstract BackEndFactory getBackEndFactory ();

    /**
     * This method returns the currently available calculation methods except the NATIVE calculation method.
     * @return currently available calculation methods except the NATIVE calculation method
     */
    public abstract CalculationMethod[] getCalculationMethods ();

    /**
     * This method returns the currently maximum compression level.
     * @return currently maximum compression level
     */
    public abstract long getMaximumCompressionLevel ();

    /**
     * This method returns the backend matching the specified criteria.
     * If no back end exactly matches the criteria then a new back end will be created.
     * @param user object requesting the back end object
     * @param detailLevelId detail level of the stored data
     * @param calculationMethod method that is used to calculate the data that is stored in the channel
     * @param timestamp time stamp the back end is responsible for
     * @return backend matching the specified criteria
     * @throws Exception if back end could not be retrieved
     */
    public abstract B getBackEndForInsert ( Object user, long detailLevelId, CalculationMethod calculationMethod, long timestamp ) throws Exception;

    /**
     * This method returns the existing backend objects matching the specified criteria.
     * If no value exists in the last returned back end object with a time stamp previous to the specified start time
     * then the next oldest not empty back end will be returned if one exists.
     * @param user object requesting the back end object
     * @param detailLevelId detail level of the stored data
     * @param calculationMethod method that is used to calculate the data that is stored in the channel
     * @param startTime start time of the requested back end objects
     * @param endTime end time of the requested back end objects
     * @return array of back end objects matching the specified criteria. never null
     * @throws Exception if back end could not be retrieved
     */
    public abstract B[] getExistingBackEnds ( Object user, long detailLevelId, CalculationMethod calculationMethod, long startTime, long endTime ) throws Exception;

    /**
     * This method deletes all back ends that have an end time that is not newer than the passed value.
     * @param detailLevelId detail level of the stored data
     * @param calculationMethod method that is used to calculate the data that is stored in the channel
     * @param endTime end time of the requested back end objects
     */
    public abstract void deleteOldBackEnds ( long detailLevelId, CalculationMethod calculationMethod, long endTime );

    /**
     * This method marks the back end matching the specified criteria as currupt.
     * This information is important since the manager then can try to repair the corrupt back end.
     * @param detailLevelId detail level of the stored data
     * @param calculationMethod method that is used to calculate the data that is stored in the channel
     * @param timestamp time stamp the back end is responsible for
     */
    public abstract void markBackEndAsCorrupt ( long detailLevelId, CalculationMethod calculationMethod, long timestamp );

    /**
     * This method checks the consistency of the back end files and tries to repair corrupt files.
     * @param abortNotificator object that will be used to check whether the operation should be aborted or not
     * @return true if all files are supposed to be ok, otherwise false
     */
    public abstract boolean repairBackEndFragmentsIfRequired ( AbortNotificator abortNotificator );

    /**
     * This method returns the storage channel that can be used as root for the current hierarchy.
     * The method can only be called if no corrupt back end files exist.
     * Otherwise null will be returned.
     * @return storage channel that can be used as root for the current hierarchy or null if at least one corruppt file exists
     */
    public abstract CalculatingStorageChannel buildStorageChannelTree ();

    /**
     * This method builds a map containing all available storage channels mapped by detail level id and calculation method including the calculation logic provider objects
     * @return map containing all available storage channels mapped by detail level id and calculation method including the calculation logic provider objects
     */
    public abstract Map<Long, Map<CalculationMethod, Map<ExtendedStorageChannel, CalculationLogicProvider>>> buildStorageChannelStructure ();

    /**
     * This method releases all resources that have been allocated in order to build the storage channel tree.
     */
    public abstract void releaseStorageChannelTree ();

    /**
     * This method deletes all files that currently exist for the current configuration including the configuration control file.
     */
    public abstract void delete ();
}
