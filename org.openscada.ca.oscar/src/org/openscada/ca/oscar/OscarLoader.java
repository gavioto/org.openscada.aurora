/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ca.oscar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.codehaus.jackson.map.ObjectMapper;

public class OscarLoader
{
    private final Map<String, Map<String, Map<String, String>>> data;

    private Map<String, Set<String>> ignoreFields;

    public OscarLoader ( final File file ) throws Exception
    {
        final ZipFile zfile = new ZipFile ( file );
        try
        {
            this.data = loadData ( zfile );
            this.ignoreFields = loadIgnoreData ( zfile );
        }
        finally
        {
            zfile.close ();
        }
    }

    private static Map<String, Map<String, Map<String, String>>> loadData ( final ZipFile zfile ) throws IOException, Exception
    {
        final ZipEntry entry = zfile.getEntry ( "data.json" ); //$NON-NLS-1$
        if ( entry == null )
        {
            throw new IllegalArgumentException ( Messages.getString("OscarLoader.InvalidFileType") ); //$NON-NLS-1$
        }
        final InputStream stream = zfile.getInputStream ( entry );
        try
        {
            return loadJsonData ( stream );
        }
        finally
        {
            stream.close ();
        }
    }

    private static Map<String, Set<String>> loadIgnoreData ( final ZipFile zfile ) throws IOException, Exception
    {
        final ZipEntry entry = zfile.getEntry ( "ignoreFields.json" ); //$NON-NLS-1$
        if ( entry == null )
        {
            return null;
        }
        final InputStream stream = zfile.getInputStream ( entry );
        try
        {
            return loadIgnoreData ( stream );
        }
        finally
        {
            stream.close ();
        }
    }

    public Map<String, Map<String, Map<String, String>>> getData ()
    {
        return this.data;
    }

    public Map<String, Set<String>> getIgnoreFields ()
    {
        return this.ignoreFields;
    }

    @SuppressWarnings ( "unchecked" )
    public static Map<String, Set<String>> loadIgnoreData ( final InputStream stream ) throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper ();

        final BufferedReader reader = new BufferedReader ( new InputStreamReader ( stream, "UTF-8" ) ); //$NON-NLS-1$

        final Map<String, Collection<Object>> data = mapper.readValue ( reader, HashMap.class );

        final Map<String, Set<String>> result = new HashMap<String, Set<String>> ();
        for ( final Map.Entry<String, Collection<Object>> entry : data.entrySet () )
        {
            final Set<String> set = new HashSet<String> ();
            result.put ( entry.getKey (), set );
            for ( final Object o : entry.getValue () )
            {
                if ( o != null )
                {
                    set.add ( o.toString () );
                }
            }
        }
        return result;
    }

    @SuppressWarnings ( "unchecked" )
    public static Map<String, Map<String, Map<String, String>>> loadJsonData ( final InputStream stream ) throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper ();

        final BufferedReader reader = new BufferedReader ( new InputStreamReader ( stream, "UTF-8" ) ); //$NON-NLS-1$

        final Map<String, Map<String, Map<String, Object>>> data = mapper.readValue ( reader, HashMap.class );

        final Map<String, Map<String, Map<String, String>>> result = new HashMap<String, Map<String, Map<String, String>>> ( data.size () );

        for ( final Map.Entry<String, Map<String, Map<String, Object>>> entry : data.entrySet () )
        {
            final Map<String, Map<String, String>> newFactory = new HashMap<String, Map<String, String>> ( entry.getValue ().size () );
            result.put ( entry.getKey (), newFactory );
            for ( final Map.Entry<String, Map<String, Object>> subEntry : entry.getValue ().entrySet () )
            {
                final Map<String, String> newConfiguration = new HashMap<String, String> ( subEntry.getValue ().size () );
                newFactory.put ( subEntry.getKey (), newConfiguration );
                for ( final Map.Entry<String, Object> subSubEntry : subEntry.getValue ().entrySet () )
                {
                    newConfiguration.put ( subSubEntry.getKey (), subSubEntry.getValue ().toString () );
                }
            }
        }

        return result;
    }

    /**
     * The oscar file suffix excluding the dot
     */
    public static final String OSCAR_SUFFIX = "oscar"; //$NON-NLS-1$

    /**
     * the oscar file suffix including the dot
     */
    public static final String OSCAR_DOT_SUFFIX = ".oscar"; //$NON-NLS-1$

    public static boolean isOscar ( final File file )
    {
        final String fileName = file.getName ().toLowerCase ();
        return fileName.endsWith ( OSCAR_DOT_SUFFIX );
    }
}
