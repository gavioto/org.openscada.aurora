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

package org.openscada.osgi.equinox.console;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleHandlerImpl extends IoHandlerAdapter
{
    private final static String NL = "\n";

    private static final String KEY_AUTHENTICATED = "authenticated";

    private final static Logger logger = LoggerFactory.getLogger ( ConsoleHandlerImpl.class );

    private static final String secret = System.getProperty ( "org.openscada.osgi.equinox.console.secret", null );

    @Override
    public void sessionOpened ( final IoSession session ) throws Exception
    {
        session.write ( "OpenSCADA OSGi Console for Equinox" + NL );

        session.getConfig ().setBothIdleTime ( Integer.getInteger ( "org.openscada.osgi.equinox.console.timeout.login", 15 ) );

        prompt ( session );
    }

    @Override
    public void sessionIdle ( final IoSession session, final IdleStatus status ) throws Exception
    {
        session.write ( "Disconnected due to timeout" + NL );
        session.close ( true );
    }

    private void prompt ( final IoSession session )
    {
        if ( isAuthenticated ( session ) )
        {
            session.write ( "osgi> " );
        }
        else
        {
            session.write ( "Authenticate: " );
        }
    }

    private boolean isAuthenticated ( final IoSession session )
    {
        return session.containsAttribute ( KEY_AUTHENTICATED );
    }

    @Override
    public void sessionClosed ( final IoSession session ) throws Exception
    {
        session.removeAttribute ( KEY_AUTHENTICATED );
    }

    @Override
    public void messageReceived ( final IoSession session, final Object message ) throws Exception
    {
        final String command = (String)message;

        if ( isAuthenticated ( session ) )
        {
            if ( !"".equals ( message ) )
            {
                handleCommand ( session, command );
            }
        }
        else
        {
            handleAuthentication ( session, message );
        }

        prompt ( session );
    }

    private void handleAuthentication ( final IoSession session, final Object message )
    {
        if ( secret == null )
        {
            session.write ( "Console is locked" + NL );
            return;
        }

        if ( message instanceof String )
        {
            if ( secret.equals ( message ) )
            {
                setAuthenticated ( session );
            }
        }
    }

    /**
     * Mark the session authenticated
     * @param session the session to mark
     */
    private void setAuthenticated ( final IoSession session )
    {
        session.setAttribute ( KEY_AUTHENTICATED );
        session.getConfig ().setBothIdleTime ( Integer.getInteger ( "org.openscada.osgi.equinox.console.timeout", 120 ) );
    }

    private void handleCommand ( final IoSession session, final String command )
    {
        logger.debug ( "Handle command: " + command );

        if ( "close".equals ( command ) || "exit".equals ( command ) )
        {
            handleClose ( session );
            return;
        }
        if ( "disconnect".equals ( command ) )
        {
            session.write ( "Disconnect by user" + NL );
            session.close ( true );
            return;
        }

        try
        {
            final ServiceReference<?>[] refs = Activator.getDefault ().getServiceReferences ( CommandProvider.class.getName (), null );
            if ( refs != null )
            {
                for ( final ServiceReference<?> ref : refs )
                {
                    if ( handleService ( session, ref, command ) )
                    {
                        return;
                    }
                }
            }
            handleHelp ( session );
        }
        catch ( final Exception e )
        {
            handleError ( session, e );
        }
    }

    private void handleClose ( final IoSession session )
    {
        session.write ( "The commands 'close' and 'exit' are not allowed from the remote console. In order to disconnect use 'disconnect'." + NL );
    }

    private void handleHelp ( final IoSession session ) throws InvalidSyntaxException
    {
        final StringBuilder sb = new StringBuilder ();

        final ServiceReference<?>[] refs = Activator.getDefault ().getServiceReferences ( CommandProvider.class.getName (), null );
        if ( refs != null )
        {
            for ( final ServiceReference<?> ref : refs )
            {
                final Object o = Activator.getDefault ().getService ( ref );
                try
                {
                    final CommandProvider cmd = (CommandProvider)o;
                    sb.append ( cmd.getHelp () );
                }
                finally
                {
                    Activator.getDefault ().ungetService ( ref );
                }
            }
        }

        session.write ( sb.toString () );
    }

    private void handleError ( final IoSession session, final Exception e )
    {
        final StringWriter sw = new StringWriter ();
        final PrintWriter pw = new PrintWriter ( sw );
        e.printStackTrace ( pw );
        session.write ( sw.getBuffer ().toString () );
    }

    private boolean handleService ( final IoSession session, final ServiceReference<?> ref, final String command ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        final String toks[] = command.split ( " " );
        final LinkedList<String> args = new LinkedList<String> ( Arrays.asList ( toks ) );
        final String commandName = args.remove ();

        final Object o = Activator.getDefault ().getService ( ref );
        try
        {
            final CommandProvider cmd = (CommandProvider)o;
            for ( final Method m : o.getClass ().getMethods () )
            {

                String name = m.getName ();
                if ( !name.startsWith ( "_" ) )
                {
                    continue;
                }
                name = name.substring ( 1 );
                if ( !name.equals ( commandName ) )
                {
                    continue;
                }
                if ( m.getReturnType () != void.class )
                {
                    continue;
                }
                final Class<?>[] params = m.getParameterTypes ();
                if ( params == null || params.length != 1 )
                {
                    continue;
                }
                if ( !params[0].isAssignableFrom ( CommandInterpreter.class ) )
                {
                    continue;
                }
                handleMethod ( session, cmd, m, args );
                return true;
            }
        }
        finally
        {
            Activator.getDefault ().ungetService ( ref );
        }
        return false;
    }

    private void handleMethod ( final IoSession session, final CommandProvider cmd, final Method m, final Queue<String> args ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        final CommandInterpreter inter = new CommandInterpreterImpl ( session, args );
        m.invoke ( cmd, new Object[] { inter } );
    }
}
