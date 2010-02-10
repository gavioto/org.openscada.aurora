/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.ca.servelt.jaxws;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService ( endpointInterface = "org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator" )
public class ConfigurationAdministratorService implements RemoteConfigurationAdministrator
{
    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationAdministratorService.class );

    private final SingleServiceTracker tracker;

    private volatile ConfigurationAdministrator service;

    public ConfigurationAdministratorService ( final BundleContext context )
    {
        this.tracker = new SingleServiceTracker ( context, ConfigurationAdministrator.class.getName (), new SingleServiceListener () {

            public void serviceChange ( final ServiceReference reference, final Object service )
            {
                ConfigurationAdministratorService.this.setService ( (ConfigurationAdministrator)service );
            }
        } );
        this.tracker.open ();
    }

    protected void setService ( final ConfigurationAdministrator service )
    {
        this.service = service;
    }

    @WebMethod ( exclude = true )
    public void dispose ()
    {
        this.tracker.close ();
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator#hasService()
     */
    public boolean hasService ()
    {
        return this.service != null;
    }

    @Override
    public Factory getConfiguration ( final String factoryId )
    {
        return convertFactory ( this.service.getFactory ( factoryId ), true );
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator#getFactories()
     */
    public Factory[] getFactories ()
    {
        return getFactories ( false );
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator#getCompleteConfiguration()
     */
    public Factory[] getCompleteConfiguration ()
    {
        return getFactories ( true );
    }

    private Factory[] getFactories ( final boolean withConfiguration )
    {
        return convert ( this.service.getKnownFactories (), withConfiguration );
    }

    private Factory[] convert ( final org.openscada.ca.Factory[] knownFactories, final boolean withConfiguration )
    {
        final Factory[] result = new Factory[knownFactories.length];

        for ( int i = 0; i < knownFactories.length; i++ )
        {
            result[i] = convertFactory ( knownFactories[i], withConfiguration );
        }

        return result;
    }

    private Factory convertFactory ( final org.openscada.ca.Factory knownFactory, final boolean withConfiguration )
    {
        if ( knownFactory == null )
        {
            return null;
        }

        final Factory factory = new Factory ( knownFactory );

        if ( withConfiguration )
        {
            factory.setConfigurations ( convert ( factory, this.service.getConfigurations ( knownFactory.getId () ) ) );
        }
        return factory;
    }

    private Configuration[] convert ( final Factory factory, final org.openscada.ca.Configuration[] configurations )
    {
        final Configuration[] result = new Configuration[configurations.length];

        for ( int i = 0; i < configurations.length; i++ )
        {
            result[i] = new Configuration ( factory, configurations[i] );
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator#purge(java.lang.String, int)
     */
    public void purge ( final String factoryId, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException
    {
        logger.info ( "Request purge: {}", factoryId );

        final Collection<Future<?>> jobs = new LinkedList<Future<?>> ();

        jobs.add ( this.service.purgeFactory ( factoryId ) );

        complete ( timeout, jobs );
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator#delete(java.lang.String, java.lang.String[], int)
     */
    public void delete ( final String factoryId, final String[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException
    {
        final Collection<Future<?>> jobs = new LinkedList<Future<?>> ();

        for ( final String id : configurations )
        {
            jobs.add ( this.service.deleteConfiguration ( factoryId, id ) );
        }

        complete ( timeout, jobs );
    }

    private void complete ( final int timeout, final Collection<Future<?>> jobs ) throws InterruptedException, ExecutionException, TimeoutException
    {
        for ( final Future<?> future : jobs )
        {
            if ( timeout > 0 )
            {
                future.get ( timeout, TimeUnit.MILLISECONDS );
            }
            else
            {
                future.get ();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator#update(java.lang.String, org.openscada.ca.servelt.jaxws.Configuration[], int)
     */
    public void update ( final String factoryId, final Configuration[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException
    {
        final Collection<Future<?>> jobs = new LinkedList<Future<?>> ();

        for ( final Configuration cfg : configurations )
        {
            jobs.add ( this.service.updateConfiguration ( factoryId, cfg.getId (), cfg.getData (), true ) );
        }

        complete ( timeout, jobs );
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servelt.jaxws.RemoteConfigurationAdministrator#create(java.lang.String, org.openscada.ca.servelt.jaxws.Configuration[], int)
     */
    public void create ( final String factoryId, final Configuration[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException
    {
        final Collection<Future<?>> jobs = new LinkedList<Future<?>> ();

        for ( final Configuration cfg : configurations )
        {
            jobs.add ( this.service.createConfiguration ( factoryId, cfg.getId (), cfg.getData () ) );
        }

        complete ( timeout, jobs );
    }

}
