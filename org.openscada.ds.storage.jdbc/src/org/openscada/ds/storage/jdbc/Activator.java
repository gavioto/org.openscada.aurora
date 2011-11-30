/*
 * This file is part of the openSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.ds.storage.jdbc;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.openscada.ds.DataStore;
import org.openscada.ds.storage.jdbc.internal.JdbcStorageDAO;
import org.openscada.ds.storage.jdbc.internal.JdbcStorageDAOBase64Impl;
import org.openscada.ds.storage.jdbc.internal.JdbcStorageDAOBlobImpl;
import org.openscada.ds.storage.jdbc.internal.StorageImpl;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.jdbc.DataSourceFactoryTracker;
import org.openscada.utils.osgi.jdbc.DataSourceHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jdbc.DataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator
{

    private final static Logger logger = LoggerFactory.getLogger ( Activator.class );

    private DataSourceFactoryTracker dataSourceFactoryTracker;

    private ServiceRegistration<DataStore> serviceHandle;

    private StorageImpl storageImpl;

    private static enum Type
    {
        BSAE64,
        BLOB;
    }

    private static Type getType ()
    {
        final String type = System.getProperty ( "org.openscada.ds.storage.jdbc.encoder", "blob" );
        try
        {
            return Type.valueOf ( type.toUpperCase () );
        }
        catch ( final Exception e )
        {
            return Type.BLOB;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        final String driver = System.getProperty ( "org.openscada.ds.storage.jdbc.driver", System.getProperty ( "org.openscada.jdbc.driver", "" ) );

        this.dataSourceFactoryTracker = new DataSourceFactoryTracker ( context, driver, new SingleServiceListener<DataSourceFactory> () {

            @Override
            public void serviceChange ( final ServiceReference<DataSourceFactory> reference, final DataSourceFactory service )
            {
                unregister ();
                if ( service != null )
                {
                    register ( service, context );
                }
            }
        } );
        this.dataSourceFactoryTracker.open ( true );
    }

    protected void register ( final DataSourceFactory service, final BundleContext context )
    {
        JdbcStorageDAO storage = null;

        try
        {
            switch ( getType () )
            {
            case BLOB:
                logger.info ( "Registering BLOB implemenation" );
                storage = new JdbcStorageDAOBlobImpl ( service, getDataSourceProperties () );
                break;
            case BSAE64:
                logger.info ( "Registering BASE64 implemenation" );
                storage = new JdbcStorageDAOBase64Impl ( service, getDataSourceProperties () );
                break;
            }
        }
        catch ( final Exception e )
        {
            logger.error ( "Failed to create service", e );
        }

        if ( storage != null )
        {
            this.storageImpl = new StorageImpl ( storage );

            final Dictionary<String, Object> properties = new Hashtable<String, Object> ( 1 );
            properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
            this.serviceHandle = context.registerService ( org.openscada.ds.DataStore.class, this.storageImpl, properties );
        }
    }

    private static Properties getDataSourceProperties ()
    {
        return DataSourceHelper.getDataSourceProperties ( "org.openscada.ds.storage.jdbc", "org.openscada.jdbc" );
    }

    protected void unregister ()
    {
        if ( this.serviceHandle != null )
        {
            this.serviceHandle.unregister ();
            this.serviceHandle = null;
        }

        if ( this.storageImpl != null )
        {
            this.storageImpl.dispose ();
            this.storageImpl = null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        unregister ();
        this.dataSourceFactoryTracker.close ();
    }

}
