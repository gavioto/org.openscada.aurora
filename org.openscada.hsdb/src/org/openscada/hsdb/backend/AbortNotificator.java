package org.openscada.hsdb.backend;

/**
 * This interface provides a method that can be used to check whether the processing of a method should be aborted or not.
 * @author Ludwig Straub
 */
public interface AbortNotificator
{
    /**
     * This method returns whether the processing of a method should be aborted or not
     * @return true, if the processing should be aborted, otherwise false
     */
    public abstract boolean getAbort ();
}
