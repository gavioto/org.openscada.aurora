package org.openscada.utils.osgi.pool;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ObjectPoolHelper
{
    /**
     * Register the object pool with the OSGi service bus
     * <p>
     * The caller must ensure that the service is unregistered using the
     * service registration object returned.
     * </p>
     * @param context the context used for registering
     * @param pool the pool to register
     * @param poolClass the service class provided by this pool
     * @return the service registration
     */
    public static ServiceRegistration registerObjectPool ( final BundleContext context, final ObjectPool pool, final String poolClass )
    {
        final Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();
        properties.put ( ObjectPool.OBJECT_POOL_CLASS, poolClass );
        return context.registerService ( ObjectPool.class.getName (), pool, properties );
    }
}
