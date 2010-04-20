/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.osgi.equinox.console;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Queue;

import org.apache.mina.core.session.IoSession;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.osgi.framework.Bundle;

public class CommandInterpreterImpl implements CommandInterpreter
{

    private final static String NL = System.getProperty ( "line.separator", "\n" );

    private final IoSession session;

    private final Queue<String> args;

    public CommandInterpreterImpl ( final IoSession session, final Queue<String> args )
    {
        this.session = session;
        this.args = args;
    }

    public Object execute ( final String cmd )
    {
        println ( "Remote execution not allowed" );
        return null;
    }

    public String nextArgument ()
    {
        if ( this.args.isEmpty () )
        {
            return null;
        }
        return this.args.remove ();
    }

    public void print ( final Object o )
    {
        this.session.write ( "" + o );
    }

    public void printBundleResource ( final Bundle bundle, final String resource )
    {
        final URL url = bundle.getResource ( resource );
        try
        {
            this.session.write ( url.openStream () );
        }
        catch ( final IOException e )
        {
        }
    }

    @SuppressWarnings ( "unchecked" )
    public void printDictionary ( final Dictionary dic, final String title )
    {
        println ( title );
        final Enumeration<?> e = dic.keys ();
        while ( e.hasMoreElements () )
        {
            final Object key = e.nextElement ();
            final Object value = dic.get ( key );
            println ( String.format ( "\t%s => %s", key, value ) );
        }
    }

    public void printStackTrace ( final Throwable t )
    {
        final StringWriter sw = new StringWriter ();
        final PrintWriter pw = new PrintWriter ( sw );

        t.printStackTrace ( pw );

        try
        {
            sw.close ();
        }
        catch ( final IOException e )
        {
            this.session.write ( sw.getBuffer ().toString () );
        }
    }

    public void println ()
    {
        this.session.write ( NL );
    }

    public void println ( final Object o )
    {
        this.session.write ( "" + o + NL );
    }

}