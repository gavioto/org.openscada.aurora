package org.openscada.hsdb.backend;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.hsdb.StorageChannel;
import org.openscada.hsdb.StorageChannelMetaData;

/**
 * This interface provides methods for storage channel backend implementations.
 * @author Ludwig Straub
 */
public interface BackEnd extends StorageChannel
{
    /**
     * This method initializes the backend storage channel.
     * It has to be assured that the method is called before any methods of the StorageChannel interface all triggered.
     * @param storageChannelMetaData metadata describing the backend of the storage channel
     * @throws Exception in case of any problem
     */
    public abstract void initialize ( final StorageChannelMetaData storageChannelMetaData ) throws Exception;

    /**
     * This method returns whether the time span that is defined via the passed start time and end time is identical to the data that is received via the method getMetaData or not.
     * The result specifier further, whether multiple calls to the method getMetaData will always return the same result or not.
     * The information of this flag can be used for optimization while handling storage channel backend objects.
     * @return true, if time span does not change while processing data, otherwise false
     */
    public abstract boolean isTimeSpanConstant ();

    /**
     * This method performs cleanup operations at the end of the storage channel backend's lifecycle.
     * @throws Exception in case of any problem
     */
    public abstract void deinitialize () throws Exception;

    /**
     * This method assigns the synchronization object that should be used when reading or writing data.
     * If no lock is passed then no synchronization will be performed.
     * @param lock synchronization object that should be used when reading or writing data
     */
    public abstract void setLock ( ReentrantReadWriteLock lock );

    /**
     * This method returns the synchronization object that should be used when reading or writing data.
     * @return synchronization object that should be used when reading or writing data
     */
    public abstract ReentrantReadWriteLock getLock ();
}
