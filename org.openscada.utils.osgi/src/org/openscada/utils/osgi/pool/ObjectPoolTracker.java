package org.openscada.utils.osgi.pool;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPoolTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( ObjectPoolTracker.class );

    private final int DEFAULT_PRIORITY = Integer.getInteger ( "org.openscada.osgi.objectPool.defaultPriority", 0 );

    private final ServiceTracker poolTracker;

    private final Set<ObjectPoolServiceListener> listeners = new HashSet<ObjectPoolServiceListener> ();

    public interface ObjectPoolServiceListener
    {
        public void poolAdded ( ObjectPool objectPool, int priority );

        public void poolRemoved ( ObjectPool objectPool );

        public void poolModified ( ObjectPool objectPool, int newPriority );
    }

    private final Map<ObjectPool, Integer> poolMap = new HashMap<ObjectPool, Integer> ();

    public ObjectPoolTracker ( final BundleContext context, final String poolClass )
    {
        this.poolTracker = new ServiceTracker ( context, ObjectPool.class.getName (), new ServiceTrackerCustomizer () {

            public void removedService ( final ServiceReference reference, final Object service )
            {
                ObjectPoolTracker.this.removePool ( (ObjectPool)service );
            }

            public void modifiedService ( final ServiceReference reference, final Object service )
            {
                ObjectPoolTracker.this.modifyPool ( (ObjectPool)service, reference );
            }

            public Object addingService ( final ServiceReference reference )
            {
                final Object o = context.getService ( reference );
                if ( ! ( o instanceof ObjectPool ) )
                {
                    context.ungetService ( reference );
                    return null;
                }
                ObjectPoolTracker.this.addPool ( ( (ObjectPool)o ), reference );
                return o;
            }
        } );
    }

    protected int getPriority ( final ServiceReference reference )
    {
        return getPriority ( reference, this.DEFAULT_PRIORITY );
    }

    protected int getPriority ( final ServiceReference reference, final int defaultPriority )
    {
        final Object o = reference.getProperty ( Constants.SERVICE_RANKING );
        if ( o instanceof Number )
        {
            return ( (Number)o ).intValue ();
        }
        else
        {
            return defaultPriority;
        }
    }

    protected synchronized void addPool ( final ObjectPool objectPool, final ServiceReference reference )
    {
        logger.debug ( "Found new pool: {} -> {}", new Object[] { objectPool, reference } );

        final int priority = getPriority ( reference );
        this.poolMap.put ( objectPool, priority );
        fireAdded ( objectPool, priority );
    }

    private void fireAdded ( final ObjectPool objectPool, final int priority )
    {
        for ( final ObjectPoolServiceListener listener : this.listeners )
        {
            listener.poolAdded ( objectPool, priority );
        }
    }

    protected synchronized void modifyPool ( final ObjectPool objectPool, final ServiceReference reference )
    {
        logger.debug ( "Pool modified: {} -> {}", new Object[] { objectPool, reference } );

        final int newPriority = getPriority ( reference );
        this.poolMap.put ( objectPool, newPriority );
        fireModified ( objectPool, newPriority );
    }

    private void fireModified ( final ObjectPool objectPool, final int newPriority )
    {
        for ( final ObjectPoolServiceListener listener : this.listeners )
        {
            listener.poolModified ( objectPool, newPriority );
        }
    }

    protected synchronized void removePool ( final ObjectPool objectPool )
    {
        logger.debug ( "Pool removed: {}", new Object[] { objectPool } );
        final Integer priority = this.poolMap.remove ( objectPool );
        if ( priority != null )
        {
            fireRemoved ( objectPool );
        }
    }

    private void fireRemoved ( final ObjectPool objectPool )
    {
        for ( final ObjectPoolServiceListener listener : this.listeners )
        {
            listener.poolRemoved ( objectPool );
        }
    }

    public synchronized void open ()
    {
        this.poolTracker.open ();
    }

    public synchronized void close ()
    {
        this.poolTracker.close ();
    }

    public synchronized void addListener ( final ObjectPoolServiceListener listener )
    {
        if ( this.listeners.add ( listener ) )
        {
            for ( final Map.Entry<ObjectPool, Integer> entry : this.poolMap.entrySet () )
            {
                listener.poolAdded ( entry.getKey (), entry.getValue () );
            }
        }
    }

    public synchronized void removeListener ( final ObjectPoolServiceListener listener )
    {
        this.listeners.remove ( listener );
    }
}
