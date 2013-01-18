/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.sec.osgi;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Executor;

import org.openscada.sec.AuthorizationResult;
import org.openscada.sec.AuthorizationService;
import org.openscada.sec.NotifyingAuthorizationService;
import org.openscada.sec.UserInformation;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class which tracks authorization services and their content and updates the
 * permissions according to their state.
 * 
 * @author Jens Reimann
 * @since 1.0.0
 */
public class AuthorizationTracker
{

    private final static Logger logger = LoggerFactory.getLogger ( AuthorizationTracker.class );

    private final class ServiceTrackerCustomizerImplementation implements ServiceTrackerCustomizer<AuthorizationService, AuthorizationService>
    {
        private final BundleContext context;

        public ServiceTrackerCustomizerImplementation ( final BundleContext context )
        {
            this.context = context;
        }

        private int getPriority ( final ServiceReference<AuthorizationService> reference )
        {
            final Object p = reference.getProperty ( Constants.SERVICE_RANKING );

            final int priority;

            if ( p instanceof Number )
            {
                priority = ( (Number)p ).intValue ();
            }
            else if ( p instanceof String )
            {
                int prio;
                try
                {
                    prio = Integer.parseInt ( (String)p );
                }
                catch ( final NumberFormatException e )
                {
                    prio = 0;
                }
                priority = prio;
            }
            else
            {
                priority = 0;
            }
            return priority;
        }

        @Override
        public AuthorizationService addingService ( final ServiceReference<AuthorizationService> reference )
        {
            final int priority = getPriority ( reference );

            final AuthorizationService service = this.context.getService ( reference );

            addService ( service, priority );

            return service;
        }

        @Override
        public void modifiedService ( final ServiceReference<AuthorizationService> reference, final AuthorizationService service )
        {
            removeService ( service, true );

            final int priority = getPriority ( reference );
            addService ( service, priority );
        }

        @Override
        public void removedService ( final ServiceReference<AuthorizationService> reference, final AuthorizationService service )
        {
            try
            {
                removeService ( service, false );
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to remove service", e );
            }
            this.context.ungetService ( reference );
        }
    }

    public class ServiceEntry implements Comparable<ServiceEntry>, AuthorizationService, org.openscada.sec.NotifyingAuthorizationService.Listener
    {
        private final AuthorizationService service;

        private final Integer priority;

        public ServiceEntry ( final AuthorizationService service, final int priority )
        {
            this.service = service;
            this.priority = priority;

            if ( service instanceof NotifyingAuthorizationService )
            {
                ( (NotifyingAuthorizationService)service ).addListener ( this );
            }
        }

        @Override
        public int compareTo ( final ServiceEntry o )
        {
            return this.priority.compareTo ( o.priority );
        }

        public AuthorizationService getService ()
        {
            return this.service;
        }

        @Override
        public AuthorizationResult authorize ( final String objectType, final String objectId, final String action, final UserInformation userInformation, final Map<String, Object> context )
        {
            return this.service.authorize ( objectType, objectId, action, userInformation, context );
        }

        public void dispose ()
        {
            if ( this.service instanceof NotifyingAuthorizationService )
            {
                ( (NotifyingAuthorizationService)this.service ).removeListener ( this );
            }
        }

        @Override
        public void serviceChanged ()
        {
            logger.info ( "Service {} changed", this.service );
            revalidateAll ();
        }
    }

    public static interface Listener
    {
        public void resultChanged ( AuthorizationResult result );
    }

    public static interface Monitor
    {
        public void dispose ();
    }

    private class MonitorImpl implements Monitor
    {
        private volatile Listener listener;

        private final AuthorizationRequest request;

        private AuthorizationResult lastResult;

        public MonitorImpl ( final Listener listener, final AuthorizationRequest request )
        {
            this.listener = listener;
            this.request = request;
        }

        @Override
        public void dispose ()
        {
            logger.debug ( "Dispose monitor" );
            this.listener = null;
            disposeMonitor ( this );
        }

        public void setResult ( final AuthorizationResult result )
        {
            final Listener listener = this.listener;
            if ( listener != null )
            {

                if ( this.lastResult != null && this.lastResult.equals ( result ) )
                {
                    return;
                }

                this.lastResult = result;

                logger.debug ( "Updating result - {}", result );

                try
                {
                    listener.resultChanged ( result );
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed to notify listener", e );
                }
            }
        }
    }

    private final ServiceTracker<AuthorizationService, AuthorizationService> tracker;

    private final Set<MonitorImpl> monitors = new LinkedHashSet<MonitorImpl> ();

    private final AuthorizationResult defaultResult;

    private final PriorityQueue<ServiceEntry> services = new PriorityQueue<ServiceEntry> ();

    private final Executor executor;

    public AuthorizationTracker ( final BundleContext bundleContext, final AuthorizationResult defaultResult, final Executor executor )
    {
        this.tracker = new ServiceTracker<AuthorizationService, AuthorizationService> ( bundleContext, AuthorizationService.class, new ServiceTrackerCustomizerImplementation ( bundleContext ) );
        this.defaultResult = defaultResult;
        this.executor = executor;
    }

    public AuthorizationTracker ( final BundleContext bundleContext, final Executor executor )
    {
        this ( bundleContext, AuthorizationHelper.DEFAULT_RESULT, executor );
    }

    public synchronized Monitor createMonitor ( final Listener listener, final AuthorizationRequest request )
    {
        final MonitorImpl monitor = new MonitorImpl ( listener, request );

        this.monitors.add ( monitor );
        revalidateMonitor ( monitor );

        return monitor;
    }

    public synchronized void disposeMonitor ( final MonitorImpl monitor )
    {
        this.monitors.remove ( monitor );
    }

    public void open ()
    {
        this.tracker.open ();
    }

    public synchronized void close ()
    {
        this.tracker.close ();
        invalidateAll ();
    }

    private void invalidateAll ()
    {
        for ( final MonitorImpl monitor : this.monitors )
        {
            monitorSetResult ( monitor, this.defaultResult );
        }
    }

    private void monitorSetResult ( final MonitorImpl monitor, final AuthorizationResult result )
    {
        this.executor.execute ( new Runnable () {
            @Override
            public void run ()
            {
                monitor.setResult ( result );
            }
        } );
    }

    private synchronized void addService ( final AuthorizationService service, final int priority )
    {
        logger.debug ( "Service added - {} / {}", service, priority );

        this.services.add ( new ServiceEntry ( service, priority ) );
        revalidateAll ();
    }

    private synchronized void removeService ( final AuthorizationService service, final boolean skipUpdate )
    {
        logger.debug ( "Service removed - {}", service );

        boolean removed = false;
        final Iterator<ServiceEntry> i = this.services.iterator ();
        while ( i.hasNext () )
        {
            final ServiceEntry entry = i.next ();
            if ( i.next ().getService () == service )
            {
                i.remove ();
                removed = true;
                entry.dispose ();
            }
        }

        if ( removed && !skipUpdate )
        {
            revalidateAll ();
        }
    }

    public synchronized void revalidateAll ()
    {
        for ( final MonitorImpl monitor : this.monitors )
        {
            revalidateMonitor ( monitor );
        }
    }

    private void revalidateMonitor ( final MonitorImpl monitor )
    {
        final AuthorizationResult result = AuthorizationHelper.authorize ( this.services, monitor.request, this.defaultResult );
        monitorSetResult ( monitor, result );
    }

}
