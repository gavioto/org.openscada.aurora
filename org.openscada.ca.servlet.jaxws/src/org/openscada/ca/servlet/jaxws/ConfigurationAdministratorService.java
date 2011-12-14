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

package org.openscada.ca.servlet.jaxws;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationInformation;
import org.openscada.ca.DiffEntry;
import org.openscada.ca.FactoryInformation;
import org.openscada.utils.concurrent.NotifyFuture;
import org.openscada.utils.osgi.SingleServiceListener;
import org.openscada.utils.osgi.SingleServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService ( endpointInterface = "org.openscada.ca.servlet.jaxws.RemoteConfigurationAdministrator" )
public class ConfigurationAdministratorService implements RemoteConfigurationAdministrator
{
    private final static Logger logger = LoggerFactory.getLogger ( ConfigurationAdministratorService.class );

    private final SingleServiceTracker<ConfigurationAdministrator> tracker;

    private volatile ConfigurationAdministrator service;

    @Resource
    private WebServiceContext context;

    public ConfigurationAdministratorService ( final BundleContext context )
    {
        this.tracker = new SingleServiceTracker<ConfigurationAdministrator> ( context, ConfigurationAdministrator.class, new SingleServiceListener<ConfigurationAdministrator> () {

            @Override
            public void serviceChange ( final ServiceReference<ConfigurationAdministrator> reference, final ConfigurationAdministrator service )
            {
                ConfigurationAdministratorService.this.setService ( service );
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
     * @see org.openscada.ca.servlet.jaxws.RemoteConfigurationAdministrator#hasService()
     */
    @Override
    public boolean hasService ()
    {
        return this.service != null;
    }

    @Override
    public FactoryInformation getFactory ( final String factoryId )
    {
        return convertFactory ( this.service.getFactory ( factoryId ), true );
    }

    @Override
    public ConfigurationInformation getConfiguration ( final String factoryId, final String configurationId )
    {
        final org.openscada.ca.Configuration cfg = this.service.getConfiguration ( factoryId, configurationId );
        return new ConfigurationInformation ( factoryId, cfg );
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servlet.jaxws.RemoteConfigurationAdministrator#getFactories()
     */
    @Override
    public FactoryInformation[] getFactories ()
    {
        return getFactories ( false );
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servlet.jaxws.RemoteConfigurationAdministrator#getCompleteConfiguration()
     */
    @Override
    public FactoryInformation[] getCompleteConfiguration ()
    {
        return getFactories ( true );
    }

    private FactoryInformation[] getFactories ( final boolean withConfiguration )
    {
        return convert ( this.service.getKnownFactories (), withConfiguration );
    }

    private FactoryInformation[] convert ( final org.openscada.ca.Factory[] knownFactories, final boolean withConfiguration )
    {
        final FactoryInformation[] result = new FactoryInformation[knownFactories.length];

        for ( int i = 0; i < knownFactories.length; i++ )
        {
            result[i] = convertFactory ( knownFactories[i], withConfiguration );
        }

        return result;
    }

    private FactoryInformation convertFactory ( final org.openscada.ca.Factory knownFactory, final boolean withConfiguration )
    {
        if ( knownFactory == null )
        {
            return null;
        }

        final FactoryInformation factory = new FactoryInformation ( knownFactory );

        if ( withConfiguration )
        {
            factory.setConfigurations ( convert ( factory, this.service.getConfigurations ( knownFactory.getId () ) ) );
        }
        return factory;
    }

    private ConfigurationInformation[] convert ( final FactoryInformation factory, final org.openscada.ca.Configuration[] configurations )
    {
        final ConfigurationInformation[] result = new ConfigurationInformation[configurations.length];

        for ( int i = 0; i < configurations.length; i++ )
        {
            result[i] = new ConfigurationInformation ( factory, configurations[i] );
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servlet.jaxws.RemoteConfigurationAdministrator#purge(java.lang.String, int)
     */
    @Override
    public void purge ( final String factoryId, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException
    {
        logger.info ( "Request purge: {}", factoryId );

        final Collection<Future<?>> jobs = new LinkedList<Future<?>> ();

        jobs.add ( this.service.purgeFactory ( makePrincipal (), factoryId ) );

        complete ( timeout, jobs );
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servlet.jaxws.RemoteConfigurationAdministrator#delete(java.lang.String, java.lang.String[], int)
     */
    @Override
    public void delete ( final String factoryId, final String[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException
    {
        final Collection<Future<?>> jobs = new LinkedList<Future<?>> ();

        for ( final String id : configurations )
        {
            jobs.add ( this.service.deleteConfiguration ( makePrincipal (), factoryId, id ) );
        }

        complete ( timeout, jobs );
    }

    private void complete ( final int timeout, final Collection<? extends Future<?>> jobs ) throws InterruptedException, ExecutionException, TimeoutException
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
     * @see org.openscada.ca.servlet.jaxws.RemoteConfigurationAdministrator#update(java.lang.String, org.openscada.ca.servlet.jaxws.ConfigurationInformation[], int)
     */
    @Override
    public void update ( final String factoryId, final ConfigurationInformation[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException
    {
        final Collection<Future<?>> jobs = new LinkedList<Future<?>> ();

        for ( final ConfigurationInformation cfg : configurations )
        {
            jobs.add ( this.service.updateConfiguration ( makePrincipal (), factoryId, cfg.getId (), cfg.getData (), true ) );
        }

        complete ( timeout, jobs );
    }

    /* (non-Javadoc)
     * @see org.openscada.ca.servlet.jaxws.RemoteConfigurationAdministrator#create(java.lang.String, org.openscada.ca.servlet.jaxws.ConfigurationInformation[], int)
     */
    @Override
    public void create ( final String factoryId, final ConfigurationInformation[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException
    {
        final Collection<Future<?>> jobs = new LinkedList<Future<?>> ();

        for ( final ConfigurationInformation cfg : configurations )
        {
            jobs.add ( this.service.createConfiguration ( makePrincipal (), factoryId, cfg.getId (), cfg.getData () ) );
        }

        complete ( timeout, jobs );
    }

    @Override
    public void applyDiff ( final Collection<DiffEntry> changeSet, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException
    {
        final NotifyFuture<Void> future = this.service.applyDiff ( makePrincipal (), changeSet );

        final Collection<NotifyFuture<Void>> result = new LinkedList<NotifyFuture<Void>> ();
        result.add ( future );

        complete ( timeout, result );
    }

    protected Principal makePrincipal ()
    {
        final Object username = this.context.getMessageContext ().get ( "username" );
        if ( username instanceof String )
        {
            return new Principal () {

                @Override
                public String getName ()
                {
                    return (String)username;
                }
            };
        }
        return null;
    }
}
