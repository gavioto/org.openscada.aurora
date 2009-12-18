package org.openscada.utils.osgi.pool;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.openscada.utils.osgi.pool.ObjectPoolTracker.ObjectPoolServiceListener;

public class ObjectPoolServiceTracker
{
    private final ObjectPoolTracker poolTracker;

    private final ObjectPoolServiceListener poolListener;

    private final String serviceId;

    private final ObjectPoolListener clientListener;

    private final Map<ObjectPool, PoolHandler> poolMap = new HashMap<ObjectPool, PoolHandler> ();

    private class PoolHandler implements ObjectPoolListener
    {
        private final ObjectPool pool;

        private final String serviceId;

        private final Map<Object, Dictionary<?, ?>> services = new HashMap<Object, Dictionary<?, ?>> ();

        public PoolHandler ( final ObjectPool pool, final String serviceId )
        {
            this.pool = pool;
            this.serviceId = serviceId;

            synchronized ( this )
            {
                this.pool.addListener ( this.serviceId, this );
            }
        }

        public synchronized void dispose ()
        {
            this.pool.removeListener ( this.serviceId, this );

            for ( final Map.Entry<Object, Dictionary<?, ?>> entry : this.services.entrySet () )
            {
                fireServiceRemoved ( entry.getKey (), entry.getValue () );
            }
            this.services.clear ();
        }

        public synchronized void serviceAdded ( final Object service, final Dictionary<?, ?> properties )
        {
            this.services.put ( service, properties );
            fireServiceAdded ( service, properties );
        }

        private void fireServiceAdded ( final Object service, final Dictionary<?, ?> properties )
        {
            ObjectPoolServiceTracker.this.handleServiceAdded ( service, properties );
        }

        public synchronized void serviceModified ( final Object service, final Dictionary<?, ?> properties )
        {
            this.services.put ( service, properties );
            fireServiceModified ( service, properties );
        }

        private void fireServiceModified ( final Object service, final Dictionary<?, ?> properties )
        {
            ObjectPoolServiceTracker.this.handleServiceModified ( service, properties );
        }

        public synchronized void serviceRemoved ( final Object service, final Dictionary<?, ?> properties )
        {
            final Dictionary<?, ?> oldProperties = this.services.remove ( service );
            if ( oldProperties != null )
            {
                fireServiceRemoved ( service, properties );
            }
        }

        private void fireServiceRemoved ( final Object service, final Dictionary<?, ?> properties )
        {
            ObjectPoolServiceTracker.this.handleServiceRemoved ( service, properties );
        }
    }

    public ObjectPoolServiceTracker ( final ObjectPoolTracker poolTracker, final String serviceId, final ObjectPoolListener clientListener )
    {
        this.serviceId = serviceId;
        this.poolTracker = poolTracker;
        this.clientListener = clientListener;
        this.poolListener = new ObjectPoolServiceListener () {

            public void poolRemoved ( final ObjectPool objectPool )
            {
                ObjectPoolServiceTracker.this.handlePoolRemove ( objectPool );
            }

            public void poolModified ( final ObjectPool objectPool, final int newPriority )
            {
                ObjectPoolServiceTracker.this.handlePoolModified ( objectPool, newPriority );
            }

            public void poolAdded ( final ObjectPool objectPool, final int priority )
            {
                ObjectPoolServiceTracker.this.handlePoolAdd ( objectPool, priority );
            }
        };
    }

    public void handleServiceAdded ( final Object service, final Dictionary<?, ?> properties )
    {
        fireServiceAdded ( service, properties );
    }

    private void fireServiceAdded ( final Object service, final Dictionary<?, ?> properties )
    {
        this.clientListener.serviceAdded ( service, properties );
    }

    public void handleServiceModified ( final Object service, final Dictionary<?, ?> properties )
    {
        fireServiceModified ( service, properties );
    }

    private void fireServiceModified ( final Object service, final Dictionary<?, ?> properties )
    {
        this.clientListener.serviceModified ( service, properties );
    }

    public void handleServiceRemoved ( final Object service, final Dictionary<?, ?> properties )
    {
        fireServiceRemoved ( service, properties );
    }

    private void fireServiceRemoved ( final Object service, final Dictionary<?, ?> properties )
    {
        this.clientListener.serviceRemoved ( service, properties );
    }

    protected synchronized void handlePoolAdd ( final ObjectPool objectPool, final int priority )
    {
        this.poolMap.put ( objectPool, new PoolHandler ( objectPool, this.serviceId ) );
    }

    protected synchronized void handlePoolModified ( final ObjectPool objectPool, final int newPriority )
    {
        // we don't care
    }

    protected synchronized void handlePoolRemove ( final ObjectPool objectPool )
    {
        final PoolHandler handler = this.poolMap.get ( objectPool );
        if ( handler != null )
        {
            handler.dispose ();
        }
    }

    public synchronized void open ()
    {
        this.poolTracker.addListener ( this.poolListener );
    }

    public synchronized void close ()
    {
        this.poolTracker.removeListener ( this.poolListener );
    }
}
