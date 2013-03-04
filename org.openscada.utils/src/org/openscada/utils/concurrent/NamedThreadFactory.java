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

package org.openscada.utils.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamedThreadFactory implements ThreadFactory
{

    private final static Logger logger = LoggerFactory.getLogger ( NamedThreadFactory.class );

    private final AtomicLong counter;

    private final String name;

    private final boolean daemon;

    private final boolean logExceptions;

    public NamedThreadFactory ( final String name )
    {
        this ( name, false );
    }

    public NamedThreadFactory ( final String name, final boolean daemon )
    {
        this ( name, daemon, true );
    }

    public NamedThreadFactory ( final String name, final boolean daemon, final boolean logExceptions )
    {
        this.logExceptions = logExceptions;
        this.counter = new AtomicLong ();
        this.name = name;
        this.daemon = daemon;
        if ( name == null )
        {
            throw new IllegalArgumentException ( String.format ( "'name' must not be null" ) );
        }
    }

    @Override
    public Thread newThread ( final Runnable r )
    {
        final Thread t = new Thread ( r, createName () );
        t.setDaemon ( this.daemon );

        if ( this.logExceptions && !Boolean.getBoolean ( "org.openscada.utils.concurrent.noDefaultLogger" ) )
        {
            t.setUncaughtExceptionHandler ( new UncaughtExceptionHandler () {

                @Override
                public void uncaughtException ( final Thread t, final Throwable e )
                {
                    logger.warn ( String.format ( "Thread %s failed and nobody cared", t ), e );
                }
            } );
        }
        return t;
    }

    protected String createName ()
    {
        return String.format ( "%s/%s", this.name, this.counter.incrementAndGet () );
    }
}