package org.openscada.utils.osgi.pool;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ObjectPoolHelper
{
    public static ServiceRegistration registerObjectPool ( final BundleContext context, final ObjectPool pool, final String poolClass )
    {
        final Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();
        properties.put ( "object.pool.class", poolClass );
        return context.registerService ( ObjectPool.class.getName (), pool, properties );
    }
}
