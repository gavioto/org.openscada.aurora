/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ca.jdbc;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.FreezableConfigurationAdministrator;
import org.openscada.ca.jdbc.internal.ConfigurationAdministratorImpl;
import org.openscada.ca.jdbc.internal.JdbcStorageDAO;
import org.openscada.ca.jdbc.internal.JdbcStorageDAOImpl;
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

    private ConfigurationAdministratorImpl configAdmin;

    private ServiceRegistration<?> serviceHandle;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        final String driver = System.getProperty ( "org.openscada.ca.jdbc.driver", System.getProperty ( "org.openscada.jdbc.driver", "" ) );

        this.dataSourceFactoryTracker = new DataSourceFactoryTracker ( context, driver, new SingleServiceListener<DataSourceFactory> () {

            @Override
            public void serviceChange ( final ServiceReference<DataSourceFactory> reference, final DataSourceFactory service )
            {
                unregister ();
                if ( service != null )
                {
                    try
                    {
                        register ( service, context );
                    }
                    catch ( final Exception e )
                    {
                        logger.error ( "Failed to start configuration administrator", e );
                    }
                }
            }
        } );
        this.dataSourceFactoryTracker.open ( true );
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

    protected void register ( final DataSourceFactory service, final BundleContext context ) throws Exception
    {
        final JdbcStorageDAO storage = new JdbcStorageDAOImpl ( service, getDataSourceProperties () );

        final ConfigurationAdministratorImpl configAdmin = new ConfigurationAdministratorImpl ( context, storage );
        configAdmin.start ();

        // started ... now announce
        this.configAdmin = configAdmin;

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ( 2 );
        properties.put ( Constants.SERVICE_VENDOR, "TH4 SYSTEMS GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "A JDBC based configuration administrator" );

        this.serviceHandle = context.registerService ( new String[] { ConfigurationAdministrator.class.getName (), FreezableConfigurationAdministrator.class.getName () }, this.configAdmin, properties );
    }

    private static Properties getDataSourceProperties ()
    {
        return DataSourceHelper.getDataSourceProperties ( "org.openscada.ca.jdbc", "org.openscada.jdbc" );
    }

    protected void unregister ()
    {
        logger.info ( "Unregistering services" );

        if ( this.serviceHandle != null )
        {
            logger.info ( "Unregistering CA" );
            this.serviceHandle.unregister ();
            this.serviceHandle = null;
        }

        if ( this.configAdmin != null )
        {
            logger.info ( "Disposing CA" );
            this.configAdmin.dispose ();
            this.configAdmin = null;
        }
    }

}
