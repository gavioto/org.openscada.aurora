/**
 * 
 */
package org.openscada.osgi.equinox.console;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.jibble.pircbot.PircBot;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ConsoleBot extends PircBot
{

    private final static Logger logger = LoggerFactory.getLogger ( ConsoleBot.class );

    private final String host;

    private final int port;

    private Thread connector;

    public ConsoleBot ( final String host, final int port )
    {
        this.host = host;
        this.port = port;

        setName ( "OSGiConsoleBot" );

        startConnect ();
    }

    private void startConnect ()
    {
        this.connector = new Thread ( new Runnable () {

            public void run ()
            {
                ConsoleBot.this.connector ();
            }
        }, "IrcBotWorker" );
        this.connector.start ();
    }

    protected void connector ()
    {
        try
        {
            while ( true )
            {
                try
                {
                    setAutoNickChange ( true );
                    connect ( this.host, this.port );

                    // we are connected
                    return;
                }
                catch ( final Exception e )
                {
                    logger.warn ( "Failed to connect", e );
                    try
                    {
                        Thread.sleep ( 10 * 1000 );
                    }
                    catch ( final InterruptedException e1 )
                    {
                        logger.warn ( "Is interrupted" );
                        return;
                    }
                }

                if ( Thread.currentThread ().isInterrupted () )
                {
                    logger.warn ( "Is interrupted" );
                    return;
                }
            }
        }
        finally
        {
            synchronized ( this )
            {
                this.connector = null;
            }
        }
    }

    @Override
    protected void onConnect ()
    {
        joinChannel ( "#servers" );
    }

    @Override
    protected synchronized void onDisconnect ()
    {
        startConnect ();
    }

    @Override
    public synchronized void dispose ()
    {
        if ( this.connector != null )
        {
            this.connector.interrupt ();
        }
        super.dispose ();
    }

    @Override
    protected void onPrivateMessage ( final String sender, final String login, final String hostname, final String message )
    {
        handleCommand ( sender, message );
    }

    private void handleCommand ( final String sender, final String command )
    {
        logger.debug ( "Handle command (from {}): {}", new Object[] { sender, command } );

        try
        {
            final ServiceReference[] refs = Activator.getDefault ().getServiceReferences ( CommandProvider.class.getName (), null );
            if ( refs != null )
            {
                for ( final ServiceReference ref : refs )
                {
                    if ( handleService ( sender, ref, command ) )
                    {
                        return;
                    }
                }
            }
            handleHelp ( sender );
        }
        catch ( final Exception e )
        {
            handleError ( sender, e );
        }
    }

    private void handleHelp ( final String sender ) throws InvalidSyntaxException
    {
        final StringBuilder sb = new StringBuilder ();

        final ServiceReference[] refs = Activator.getDefault ().getServiceReferences ( CommandProvider.class.getName (), null );
        if ( refs != null )
        {
            for ( final ServiceReference ref : refs )
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

        sendMultiMessage ( sender, sb.toString () );
    }

    public void sendMultiMessage ( final String receiver, final String message )
    {
        final StringTokenizer tok = new StringTokenizer ( message, CommandInterpreterImpl.NL );
        while ( tok.hasMoreElements () )
        {
            sendMessage ( receiver, tok.nextToken () );
        }
    }

    private void handleError ( final String sender, final Exception e )
    {
        final StringWriter sw = new StringWriter ();
        final PrintWriter pw = new PrintWriter ( sw );

        e.printStackTrace ( pw );

        sendMessage ( sender, sw.getBuffer ().toString () );
    }

    private boolean handleService ( final String sender, final ServiceReference ref, final String command ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
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
                handleMethod ( sender, cmd, m, args );
                return true;
            }
        }
        finally
        {
            Activator.getDefault ().ungetService ( ref );
        }
        return false;
    }

    private void handleMethod ( final String sender, final CommandProvider cmd, final Method m, final Queue<String> args ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        final CommandInterpreter inter = new CommandInterpreterImpl ( this, sender, args );
        m.invoke ( cmd, new Object[] { inter } );
    }

}