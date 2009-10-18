package org.openscada.hsdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openscada.hsdb.datatypes.DoubleValue;
import org.openscada.hsdb.datatypes.LongValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This StorageChannel implementation provides methods for managing StorageChannel objects.
 * This can be useful when complex storage channel structures have to be created and handled.
 * @author Ludwig Straub
 */
public abstract class SimpleStorageChannelManager implements StorageChannelManager
{
    /** The default logger. */
    private final static Logger logger = LoggerFactory.getLogger ( SimpleStorageChannelManager.class );

    /** List of currently registers storage channels. */
    private final List<ExtendedStorageChannel> storageChannels;

    /**
     * Standard constructor.
     */
    public SimpleStorageChannelManager ()
    {
        storageChannels = new LinkedList<ExtendedStorageChannel> ();
    }

    /**
     * @see org.openscada.hsdb.StorageChannelManager#registerStorageChannel
     */
    public synchronized void registerStorageChannel ( final ExtendedStorageChannel storageChannel )
    {
        storageChannels.add ( storageChannel );
    }

    /**
     * @see org.openscada.hsdb.StorageChannelManager#unregisterStorageChannel
     */
    public synchronized void unregisterStorageChannel ( final ExtendedStorageChannel storageChannel )
    {
        storageChannels.remove ( storageChannel );
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#updateLong
     */
    public synchronized void updateLong ( final LongValue longValue ) throws Exception
    {
        Exception innerException = null;
        for ( final ExtendedStorageChannel storageChannel : storageChannels )
        {
            try
            {
                storageChannel.updateLong ( longValue );
            }
            catch ( final Exception e )
            {
                if ( innerException == null )
                {
                    innerException = e;
                }
            }
        }
        if ( innerException != null )
        {
            final String message = "a long value of at least one inner storage channel could not be updated";
            logger.error ( message, innerException );
            throw new Exception ( message, innerException );
        }
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#updateLongs
     */
    public synchronized void updateLongs ( final LongValue[] longValues ) throws Exception
    {
        Exception innerException = null;
        for ( final ExtendedStorageChannel storageChannel : storageChannels )
        {
            try
            {
                storageChannel.updateLongs ( longValues );
            }
            catch ( final Exception e )
            {
                if ( innerException == null )
                {
                    innerException = e;
                }
            }
        }
        if ( innerException != null )
        {
            final String message = "long values of at least one inner storage channel could not be updated";
            logger.error ( message, innerException );
            throw new Exception ( message, innerException );
        }
    }

    /**
     * @see org.openscada.hsdb.StorageChannel#getLongValues
     */
    public synchronized LongValue[] getLongValues ( final long startTime, final long endTime ) throws Exception
    {
        // optimization if exactly one storage channel is currently managed
        if ( storageChannels.size () == 1 )
        {
            return storageChannels.get ( 0 ).getLongValues ( startTime, endTime );
        }

        // default method logic
        final List<LongValue> longValues = new ArrayList<LongValue> ();
        for ( final StorageChannel storageChannel : storageChannels )
        {
            longValues.addAll ( Arrays.asList ( storageChannel.getLongValues ( startTime, endTime ) ) );
        }
        return longValues.toArray ( EMPTY_LONGVALUE_ARRAY );
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDouble
     */
    public synchronized void updateDouble ( final DoubleValue doubleValue ) throws Exception
    {
        Exception innerException = null;
        for ( final ExtendedStorageChannel storageChannel : storageChannels )
        {
            try
            {
                storageChannel.updateDouble ( doubleValue );
            }
            catch ( final Exception e )
            {
                if ( innerException == null )
                {
                    innerException = e;
                }
            }
        }
        if ( innerException != null )
        {
            final String message = "a double value of at least one inner storage channel could not be updated";
            logger.error ( message, innerException );
            throw new Exception ( message, innerException );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#updateDoubles
     */
    public synchronized void updateDoubles ( final DoubleValue[] doubleValues ) throws Exception
    {
        Exception innerException = null;
        for ( final ExtendedStorageChannel storageChannel : storageChannels )
        {
            try
            {
                storageChannel.updateDoubles ( doubleValues );
            }
            catch ( final Exception e )
            {
                if ( innerException == null )
                {
                    innerException = e;
                }
            }
        }
        if ( innerException != null )
        {
            final String message = "double values of at least one inner storage channel could not be updated";
            logger.error ( message, innerException );
            throw new Exception ( message, innerException );
        }
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#getDoubleValues
     */
    public synchronized DoubleValue[] getDoubleValues ( final long startTime, final long endTime ) throws Exception
    {
        // optimization if exactly one storage channel is currently managed
        if ( storageChannels.size () == 1 )
        {
            return storageChannels.get ( 0 ).getDoubleValues ( startTime, endTime );
        }

        // default method logic
        final List<DoubleValue> doubleValues = new ArrayList<DoubleValue> ();
        for ( final ExtendedStorageChannel storageChannel : storageChannels )
        {
            doubleValues.addAll ( Arrays.asList ( storageChannel.getDoubleValues ( startTime, endTime ) ) );
        }
        return doubleValues.toArray ( EMPTY_DOUBLEVALUE_ARRAY );
    }

    /**
     * @see org.openscada.hsdb.ExtendedStorageChannel#cleanupRelicts
     */
    public synchronized void cleanupRelicts () throws Exception
    {
        for ( final StorageChannel storageChannel : storageChannels )
        {
            storageChannel.cleanupRelicts ();
        }
    }
}
