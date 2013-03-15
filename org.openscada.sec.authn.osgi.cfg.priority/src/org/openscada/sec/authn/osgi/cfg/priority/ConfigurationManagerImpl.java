/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassid.de)
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

package org.openscada.sec.authn.osgi.cfg.priority;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.sec.AuthenticationService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @since 1.1
 */
public class ConfigurationManagerImpl implements ConfigurationManager
{

    public static final class ConfigurationGroupImpl implements ConfigurationGroup
    {

        private final List<AuthenticationService> services;

        private final int retries = 3;

        public ConfigurationGroupImpl ()
        {
            this.services = new LinkedList<AuthenticationService> ();
        }

        @Override
        public int getRetries ()
        {
            return this.retries;
        }

        @Override
        public List<AuthenticationService> getServices ()
        {
            return this.services;
        }

        public void add ( final AuthenticationService service )
        {
            this.services.add ( service );
        }

        @Override
        public String toString ()
        {
            final StringBuilder sb = new StringBuilder ();
            sb.append ( "[ConfigurationGroup - retries: " + this.retries );
            sb.append ( ", services: " );
            for ( final AuthenticationService service : this.services )
            {
                sb.append ( "\n\t" + service );
            }
            sb.append ( "]" );
            return sb.toString ();
        }
    }

    public static final class ConfigurationImpl implements Configuration
    {
        private final List<ConfigurationGroup> groups;

        public ConfigurationImpl ( final List<ConfigurationGroup> groups )
        {
            this.groups = groups;
        }

        @Override
        public List<ConfigurationGroup> getGroups ()
        {
            return this.groups;
        }

        @Override
        public String toString ()
        {
            final StringBuilder sb = new StringBuilder ();
            sb.append ( "[Configuration - groups:" );

            for ( final ConfigurationGroup group : this.groups )
            {
                sb.append ( "\n" );
                sb.append ( group );
            }

            sb.append ( "]" );
            return sb.toString ();
        }
    }

    private final ServiceTracker<AuthenticationService, AuthenticationService> tracker;

    public ConfigurationManagerImpl ( final BundleContext context )
    {
        this.tracker = new ServiceTracker<AuthenticationService, AuthenticationService> ( context, AuthenticationService.class, null );
        this.tracker.open ();
    }

    public void dispose ()
    {
        this.tracker.close ();
    }

    @Override
    public Configuration getConfiguration ()
    {
        final List<ConfigurationGroup> groups = new LinkedList<ConfigurationGroup> ();

        Long lastRanking = null;
        ConfigurationGroupImpl lastGroup = null;

        for ( final Map.Entry<ServiceReference<AuthenticationService>, AuthenticationService> entry : this.tracker.getTracked ().entrySet () )
        {
            final Object o = entry.getKey ().getProperty ( Constants.SERVICE_RANKING );
            final long ranking = o instanceof Number ? ( (Number)o ).longValue () : 0;

            if ( lastRanking == null || lastRanking != ranking )
            {
                lastGroup = new ConfigurationGroupImpl ();
                groups.add ( lastGroup );
                lastRanking = ranking;
            }

            lastGroup.add ( entry.getValue () );
        }

        return new ConfigurationImpl ( groups );
    }

}
