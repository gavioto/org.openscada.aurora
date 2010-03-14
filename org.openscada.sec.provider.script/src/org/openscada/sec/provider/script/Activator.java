/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2009-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.sec.provider.script;

import java.util.Dictionary;
import java.util.Hashtable;

import org.openscada.ca.ConfigurationAdministrator;
import org.openscada.ca.ConfigurationFactory;
import org.openscada.sec.AuthorizationService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

public class Activator implements BundleActivator
{

    private ScriptAuthorizationProvider service;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start ( final BundleContext context ) throws Exception
    {
        this.service = new ScriptAuthorizationProvider ();

        final Dictionary<String, Object> properties = new Hashtable<String, Object> ();

        properties.put ( Constants.SERVICE_DESCRIPTION, "A script based authorization service" );
        properties.put ( Constants.SERVICE_VENDOR, "inavare GmbH" );
        properties.put ( ConfigurationAdministrator.FACTORY_ID, context.getBundle ().getSymbolicName () + ".factory" );

        context.registerService ( new String[] { AuthorizationService.class.getName (), ConfigurationFactory.class.getName () }, this.service, properties );
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop ( final BundleContext context ) throws Exception
    {
    }

}
