package org.openscada.utils.osgi.pool;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.openscada.utils.osgi.pool.ObjectPoolTracker.ObjectPoolServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractObjectPoolServiceTracker
{
    private final static Logger logger = LoggerFactory.getLogger ( AbstractObjectPoolServiceTracker.class );

    private final ObjectPoolTracker poolTracker;

    private final ObjectPoolServiceListener poolListener;

    protected final String serviceId;

    private final Map<ObjectPool, PoolHandler> poolMap = new HashMap<ObjectPool, PoolHandler> ();

    protected class PoolHandler implements ObjectPoolListener
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
            logger.debug ( "Service added to pool: {} -> {}", new Object[] { this.serviceId, service } );
            AbstractObjectPoolServiceTracker.this.handleServiceAdded ( service, properties );
        }

        public synchronized void serviceModified ( final Object service, final Dictionary<?, ?> properties )
        {
            this.services.put ( service, properties );
            fireServiceModified ( service, properties );
        }

        private void fireServiceModified ( final Object service, final Dictionary<?, ?> properties )
        {
            AbstractObjectPoolServiceTracker.this.handleServiceModified ( service, properties );
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
            AbstractObjectPoolServiceTracker.this.handleServiceRemoved ( service, properties );
        }
    }

    public AbstractObjectPoolServiceTracker ( final ObjectPoolTracker poolTracker, final String serviceId )
    {
        this.serviceId = serviceId;
        this.poolTracker = poolTracker;

        this.poolListener = new ObjectPoolServiceListener () {

            public void poolRemoved ( final ObjectPool objectPool )
            {
                AbstractObjectPoolServiceTracker.this.handlePoolRemove ( objectPool );
            }

            public void poolModified ( final ObjectPool objectPool, final int newPriority )
            {
                AbstractObjectPoolServiceTracker.this.handlePoolModified ( objectPool, newPriority );
            }

            public void poolAdded ( final ObjectPool objectPool, final int priority )
            {
                AbstractObjectPoolServiceTracker.this.handlePoolAdd ( objectPool, priority );
            }
        };
    }

    protected abstract void handleServiceAdded ( final Object service, final Dictionary<?, ?> properties );

    protected abstract void handleServiceModified ( final Object service, final Dictionary<?, ?> properties );

    protected abstract void handleServiceRemoved ( final Object service, final Dictionary<?, ?> properties );

    protected synchronized void handlePoolAdd ( final ObjectPool objectPool, final int priority )
    {
        logger.debug ( "Pool added: {}/{}", new Object[] { objectPool, priority } );
        this.poolMap.put ( objectPool, new PoolHandler ( objectPool, this.serviceId ) );
    }

    protected synchronized void handlePoolModified ( final ObjectPool objectPool, final int newPriority )
    {
        // we don't care
    }

    protected synchronized void handlePoolRemove ( final ObjectPool objectPool )
    {
        logger.debug ( "Pool removed: {}", objectPool );

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
