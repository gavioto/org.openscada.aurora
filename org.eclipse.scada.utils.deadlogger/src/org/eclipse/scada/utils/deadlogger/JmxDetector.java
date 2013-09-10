/*******************************************************************************
 * Copyright (c) 2011 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.utils.deadlogger;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxDetector implements Detector
{

    private static final String SINGLE_DASH = "----------------------------------------------------------------------------------------------";

    private static final String DOUBLE_DASH = "==============================================================================================";

    private final static Logger logger = LoggerFactory.getLogger ( JmxDetector.class );

    private final String hostname;

    private final int port;

    private JMXConnector localJMXConnector;

    private ThreadInfo[] threadInformation;

    public JmxDetector ()
    {
        this.hostname = null;
        this.port = 0;
    }

    public JmxDetector ( final String hostname, final int port )
    {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public synchronized boolean isDeadlock ()
    {
        try
        {
            if ( this.threadInformation == null )
            {
                return check ();
            }
            else
            {
                return this.threadInformation.length > 0;
            }
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to check", e );
            return false;
        }
    }

    @Override
    public synchronized void dump ( final PrintStream out )
    {
        if ( isDeadlock () )
        {
            out.println ( DOUBLE_DASH );
            out.println ( String.format ( " %1$s : %2$tF %2$tT.%2$tL - Deadlock detected involving %3$s threads", getString (), Calendar.getInstance (), this.threadInformation.length ) );
            for ( int i = 0; i < this.threadInformation.length; i++ )
            {
                dumpThread ( out, this.threadInformation[i], i );
            }
            out.println ( DOUBLE_DASH );
        }
        else
        {
            out.println ( " " + getString () + " : No deadlock detected" );
        }
    }

    private void dumpThread ( final PrintStream out, final ThreadInfo ti, final int i )
    {
        final LockInfo li = ti.getLockInfo ();
        out.println ( DOUBLE_DASH );
        out.println ( String.format ( " Thread #%s: %s - %s - %s", i, ti.getThreadName (), ti.getThreadId (), ti.getThreadState () ) );
        out.println ( SINGLE_DASH );
        if ( li != null )
        {
            out.println ( String.format ( " Waiting for: %s, Owner: %s", li, ti.getLockOwnerName () ) );
        }
        out.println ( SINGLE_DASH );
        printStackTrace ( out, ti );
    }

    private void printStackTrace ( final PrintStream out, final ThreadInfo ti )
    {
        final StackTraceElement[] st = ti.getStackTrace ();
        for ( int i = 0; i < st.length; i++ )
        {
            final StackTraceElement ele = st[i];
            out.println ( "\t" + ele );

            for ( final MonitorInfo mi : ti.getLockedMonitors () )
            {
                if ( mi.getLockedStackDepth () == i )
                {
                    out.println ( "\t\t- " + mi.toString () );
                }
            }
        }
    }

    protected ThreadMXBean lookup () throws IOException
    {
        if ( this.hostname == null )
        {
            return ManagementFactory.getThreadMXBean ();
        }
        else
        {
            final Map<Object, Object> localHashMap = new HashMap<Object, Object> ();
            final String[] arrayOfString = new String[2];
            arrayOfString[0] = "";
            arrayOfString[1] = "";
            localHashMap.put ( "jmx.remote.credentials", arrayOfString );
            final JMXServiceURL localJMXServiceURL = new JMXServiceURL ( "service:jmx:rmi:///jndi/rmi://" + this.hostname + ":" + this.port + "/jmxrmi" );
            this.localJMXConnector = JMXConnectorFactory.connect ( localJMXServiceURL );
            final MBeanServerConnection localMBeanServerConnection = this.localJMXConnector.getMBeanServerConnection ();
            return ManagementFactory.newPlatformMXBeanProxy ( localMBeanServerConnection, "java.lang:type=Threading", ThreadMXBean.class );
        }
    }

    protected String getString ()
    {
        if ( this.hostname == null )
        {
            return "<local>";
        }
        else
        {
            return String.format ( "<remote:%s:%s>", this.hostname, this.port );
        }
    }

    protected boolean check () throws Exception
    {
        final ThreadMXBean localThreadMXBean = lookup ();

        try
        {
            final long[] threads;
            if ( localThreadMXBean.isSynchronizerUsageSupported () )
            {
                threads = localThreadMXBean.findDeadlockedThreads ();
            }
            else
            {
                threads = localThreadMXBean.findMonitorDeadlockedThreads ();
            }

            if ( threads == null || threads.length == 0 )
            {
                this.threadInformation = new ThreadInfo[0];
            }
            else
            {
                this.threadInformation = localThreadMXBean.getThreadInfo ( threads, true, true );
            }
            return threads == null ? false : threads.length > 0;
        }
        finally
        {
            final JMXConnector localJMXConnector = this.localJMXConnector;
            this.localJMXConnector = null;
            if ( localJMXConnector != null )
            {
                localJMXConnector.close ();
            }
        }

    }
}
