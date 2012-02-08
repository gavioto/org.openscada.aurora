/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.utils.osgi.autostart;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.startlevel.BundleStartLevel;

public class Activator implements BundleActivator
{
    private BundleContext context;

    private final Map<String, Integer> bundleStartList = new LinkedHashMap<String, Integer> ();

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext bundleContext ) throws Exception
    {
        this.context = bundleContext;

        loadStartLevels ();

        for ( final Map.Entry<String, Integer> entry : this.bundleStartList.entrySet () )
        {
            setStartLevel ( entry.getKey (), entry.getValue () );
        }
    }

    protected void loadStartLevels () throws IOException
    {
        final String fileName = System.getProperty ( "org.openscada.utils.osgi.equinox.autostart", null );

        this.bundleStartList.clear ();

        if ( fileName == null )
        {
            return;
        }

        final File file = new File ( fileName );
        final Properties p = new Properties ();
        final FileReader reader = new FileReader ( file );
        try
        {
            p.load ( reader );
        }
        finally
        {
            reader.close ();
        }

        for ( final String key : p.stringPropertyNames () )
        {
            final String value = p.getProperty ( key );
            this.bundleStartList.put ( key, Integer.parseInt ( value ) );
        }
    }

    private void setStartLevel ( final String symbolicName, final int startLevel )
    {
        final Bundle bundle = findBundle ( symbolicName );
        if ( bundle == null )
        {
            return;
        }
        final BundleStartLevel bundleStartLevel = bundle.adapt ( BundleStartLevel.class );
        if ( bundleStartLevel == null )
        {
            return;
        }

        bundleStartLevel.setStartLevel ( startLevel );
    }

    private Bundle findBundle ( final String symbolicName )
    {
        final Bundle[] bundles = this.context.getBundles ();
        if ( bundles == null )
        {
            return null;
        }

        for ( final Bundle bundle : bundles )
        {
            if ( bundle.getSymbolicName ().equals ( symbolicName ) )
            {
                return bundle;
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext bundleContext ) throws Exception
    {
        this.context = null;
    }

}
