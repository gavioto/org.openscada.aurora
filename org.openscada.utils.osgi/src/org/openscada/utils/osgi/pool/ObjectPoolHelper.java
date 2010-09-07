/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

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
