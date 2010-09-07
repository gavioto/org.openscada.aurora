/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.osgi.equinox.ircbot;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Queue;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.osgi.framework.Bundle;

public class CommandInterpreterImpl implements CommandInterpreter
{

    final static String NL = System.getProperty ( "line.separator", "\n" );

    private final String sender;

    private final Queue<String> args;

    private final ConsoleBot bot;

    public CommandInterpreterImpl ( final ConsoleBot bot, final String sender, final Queue<String> args )
    {
        this.bot = bot;
        this.sender = sender;
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
        this.bot.sendMessage ( this.sender, "" + o );
    }

    public void printBundleResource ( final Bundle bundle, final String resource )
    {
        final URL url = bundle.getResource ( resource );

        // FIXME: sending file is missing
        this.bot.sendMessage ( this.sender, "Receive: " + url );
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
            this.bot.sendMessage ( this.sender, sw.getBuffer ().toString () );
        }
    }

    public void println ()
    {
        this.bot.sendMessage ( this.sender, "" );
    }

    public void println ( final Object o )
    {
        this.bot.sendMessage ( this.sender, "" + o );
    }

}