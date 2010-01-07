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

package org.openscada.ca.servelt.axis;

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

@WebService
public class ConfigurationAdministratorService
{
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

    public boolean hasService ()
    {
        return this.service != null;
    }

    public Factory[] getFactories ()
    {
        return getFactories ( false );
    }

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
            result[i] = new Factory ( knownFactories[i] );
            if ( withConfiguration )
            {
                result[i].setConfigurations ( convert ( result[i], this.service.getConfigurations ( knownFactories[i].getId () ) ) );
            }
        }

        return result;
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
            if ( timeout >= 0 )
            {
                future.get ( timeout, TimeUnit.MILLISECONDS );
            }
            else
            {
                future.get ();
            }
        }
    }

    public void update ( final String factoryId, final Configuration[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException
    {
        final Collection<Future<?>> jobs = new LinkedList<Future<?>> ();

        for ( final Configuration cfg : configurations )
        {
            jobs.add ( this.service.updateConfiguration ( factoryId, cfg.getId (), cfg.getData (), true ) );
        }

        complete ( timeout, jobs );
    }

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
