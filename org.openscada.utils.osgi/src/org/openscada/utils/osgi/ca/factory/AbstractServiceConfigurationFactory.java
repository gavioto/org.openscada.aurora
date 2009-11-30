/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.utils.osgi.ca.factory;

import java.util.HashMap;
import java.util.Map;

import org.openscada.ca.ConfigurationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public abstract class AbstractServiceConfigurationFactory<T> implements ConfigurationFactory
{

    private final Map<String, Entry<T>> services = new HashMap<String, Entry<T>> ();

    private final BundleContext context;

    protected static class Entry<T>
    {
        private final T service;

        private final ServiceRegistration handle;

        /**
         * Create a new service entry that is registered with OSGi
         * @param service the service
         * @param handle the service registration
         */
        public Entry ( final T service, final ServiceRegistration handle )
        {
            this.service = service;
            this.handle = handle;
        }

        /**
         * Create a new service entry that is not registered with OSGi
         * @param service the service
         */
        public Entry ( final T service )
        {
            this.service = service;
            this.handle = null;
        }

        public ServiceRegistration getHandle ()
        {
            return this.handle;
        }

        public T getService ()
        {
            return this.service;
        }
    }

    public AbstractServiceConfigurationFactory ( final BundleContext context )
    {
        this.context = context;
    }

    public void dispose ()
    {
        for ( final Entry<T> entry : this.services.values () )
        {
            disposeService ( entry.getService () );
            unregisterService ( entry );
        }
    }

    /**
     * Unregister the service entry with OSGi
     * @param entry the entry to unregister
     */
    protected void unregisterService ( final Entry<T> entry )
    {
        final ServiceRegistration handle = entry.getHandle ();
        if ( handle != null )
        {
            handle.unregister ();
        }
    }

    public void delete ( final String configurationId ) throws Exception
    {
        final Entry<T> entry = this.services.remove ( configurationId );
        if ( entry != null )
        {
            disposeService ( entry.getService () );
            unregisterService ( entry );
        }
    }

    public void update ( final String configurationId, final Map<String, String> parameters ) throws Exception
    {
        Entry<T> entry = this.services.get ( configurationId );
        if ( entry != null )
        {
            updateService ( entry, parameters );
        }
        else
        {
            entry = createService ( configurationId, this.context, parameters );
            if ( entry != null )
            {
                this.services.put ( configurationId, entry );
            }
        }
    }

    /**
     * Create a new service instance
     * <p>
     * The method must also register the service with the OSGi bundle context if needed. The service
     * registration must then be places into the result that is returned. This is an optional step.
     * There is no need to register the created service.
     * </p>
     * @param configurationId the configuration id for which the service should be created
     * @param context the bundle context
     * @param parameters the initial parameters
     * @return a new entry instance which holds the service
     * @throws Exception if anything goes wrong
     */
    protected abstract Entry<T> createService ( String configurationId, BundleContext context, final Map<String, String> parameters ) throws Exception;

    protected abstract void disposeService ( T service );

    protected abstract void updateService ( Entry<T> entry, Map<String, String> parameters ) throws Exception;
}
