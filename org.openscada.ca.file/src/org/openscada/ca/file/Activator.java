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

package org.openscada.ca.file;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.FreezableConfigurationAdministrator;
import org.openscada.ca.common.AbstractConfigurationAdministrator;
import org.openscada.ca.file.internal.ConfigurationAdminImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator
{
    private AbstractConfigurationAdministrator service;

    private ServiceRegistration handle;

    public void start ( final BundleContext context ) throws Exception
    {
        this.service = new ConfigurationAdminImpl ( context );

        this.service.start ();

        final Dictionary<Object, Object> properties = new Hashtable<Object, Object> ();
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        properties.put ( Constants.SERVICE_DESCRIPTION, "An OpenSCADA CA File Implementation" );

        this.handle = context.registerService ( new String[] { ConfigurationAdministrator.class.getName (), FreezableConfigurationAdministrator.class.getName () }, this.service, properties );
    }

    public void stop ( final BundleContext context ) throws Exception
    {
        this.handle.unregister ();
        this.handle = null;

        this.service.stop ();
        this.service = null;
    }

}
