package org.openscada.utils.osgi.pool;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class ObjectPoolImpl implements ObjectPool
{

    private final static Logger logger = LoggerFactory.getLogger ( ObjectPoolImpl.class );

    private final Multimap<String, ObjectPoolListener> listeners = HashMultimap.create ();

    private final Map<String, Map<Object, Dictionary<?, ?>>> services = new HashMap<String, Map<Object, Dictionary<?, ?>>> ();

    public synchronized void addService ( final String id, final Object service, final Dictionary<?, ?> properties )
    {
        logger.debug ( "Adding service: {} -> {} -> {}", new Object[] { id, service, properties } );

        Map<Object, Dictionary<?, ?>> serviceMap = this.services.get ( id );
        if ( serviceMap == null )
        {
            serviceMap = new HashMap<Object, Dictionary<?, ?>> ();
            this.services.put ( id, serviceMap );
        }
        serviceMap.put ( service, properties );
        fireAddedService ( id, service, properties );
    }

    public synchronized void modifyService ( final String id, final Object service, final Dictionary<?, ?> properties )
    {
        logger.debug ( "Modifing service: {} -> {} -> {}", new Object[] { id, service, properties } );

        final Map<Object, Dictionary<?, ?>> serviceMap = this.services.get ( id );
        if ( serviceMap != null )
        {
            serviceMap.put ( service, properties );
            fireModifiedService ( id, service, properties );
        }
    }

    public synchronized void removeService ( final String id, final Object service )
    {
        logger.debug ( "Removing service: {} -> {}", new Object[] { id, service } );

        final Map<Object, Dictionary<?, ?>> serviceMap = this.services.get ( id );
        if ( serviceMap == null )
        {
            return;
        }
        final Dictionary<?, ?> properties = serviceMap.remove ( service );
        if ( properties != null )
        {
            if ( serviceMap.isEmpty () )
            {
                this.services.remove ( id );
            }
            fireRemoveService ( id, service, properties );
        }
    }

    private void fireAddedService ( final String id, final Object service, final Dictionary<?, ?> properties )
    {
        for ( final ObjectPoolListener listener : this.listeners.get ( id ) )
        {
            listener.serviceAdded ( service, properties );
        }
    }

    private void fireModifiedService ( final String id, final Object service, final Dictionary<?, ?> properties )
    {
        for ( final ObjectPoolListener listener : this.listeners.get ( id ) )
        {
            listener.serviceModified ( service, properties );
        }
    }

    private void fireRemoveService ( final String id, final Object service, final Dictionary<?, ?> properties )
    {
        for ( final ObjectPoolListener listener : this.listeners.get ( id ) )
        {
            listener.serviceRemoved ( service, properties );
        }
    }

    public synchronized void dispose ()
    {
        for ( final Map.Entry<String, ObjectPoolListener> entry : this.listeners.entries () )
        {
            final Map<Object, Dictionary<?, ?>> serviceMap = this.services.get ( entry.getKey () );
            for ( final Map.Entry<Object, Dictionary<?, ?>> serviceEntry : serviceMap.entrySet () )
            {
                entry.getValue ().serviceRemoved ( serviceEntry.getKey (), serviceEntry.getValue () );
            }
        }
    }

    /* (non-Javadoc)
     * @see org.openscada.da.datasource.ObjectPool#addListener(java.lang.String, org.openscada.da.datasource.ObjectPoolListener)
     */
    public synchronized void addListener ( final String id, final ObjectPoolListener listener )
    {
        logger.debug ( "Adding listener" );

        if ( this.listeners.put ( id, listener ) )
        {
            final Map<Object, Dictionary<?, ?>> serviceMap = this.services.get ( id );
            if ( serviceMap != null )
            {
                for ( final Map.Entry<Object, Dictionary<?, ?>> entry : serviceMap.entrySet () )
                {
                    // logger.debug ( "Late reporting: {} -> {}", new Object[] { entry.getKey (), entry.getValue () } );
                    listener.serviceAdded ( entry.getKey (), entry.getValue () );
                }
            }
        }

    }

    /* (non-Javadoc)
     * @see org.openscada.da.datasource.ObjectPool#removeListener(java.lang.String, org.openscada.da.datasource.ObjectPoolListener)
     */
    public synchronized void removeListener ( final String id, final ObjectPoolListener listener )
    {
        this.listeners.remove ( id, listener );
    }
}
