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

package org.openscada.utils.toggle.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.openscada.utils.toggle.ToggleCallback;
import org.openscada.utils.toggle.ToggleError;
import org.openscada.utils.toggle.ToggleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToggleServiceImpl implements ToggleService, Runnable
{
    private static final Logger logger = LoggerFactory.getLogger ( ToggleServiceImpl.class );

    private static final int delay = 100;

    private final ConcurrentMap<Integer, ToggleInfo> toggleInfos = new ConcurrentHashMap<Integer, ToggleInfo> ();

    private final ConcurrentMap<Integer, List<ToggleCallback>> toggleCallbacks = new ConcurrentHashMap<Integer, List<ToggleCallback>> ();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor ();

    private final AtomicLong counter = new AtomicLong ( 0 );

    private final Object addRemoveLock = new Object ();

    public void addListener ( final int interval, final ToggleCallback bc ) throws ToggleError
    {
        synchronized ( this.addRemoveLock )
        {
            if ( !this.toggleInfos.containsKey ( interval ) )
            {
                this.toggleInfos.put ( interval, new ToggleInfo ( interval ) );
            }
            if ( !this.toggleCallbacks.containsKey ( interval ) )
            {
                this.toggleCallbacks.put ( interval, new CopyOnWriteArrayList<ToggleCallback> () );
            }
            final List<ToggleCallback> handlers = this.toggleCallbacks.get ( interval );
            handlers.add ( bc );
        }
    }

    public void removeListener ( final ToggleCallback bc )
    {
        synchronized ( this.addRemoveLock )
        {
            for ( final List<ToggleCallback> bcs : this.toggleCallbacks.values () )
            {
                bcs.remove ( bc );
            }
            final List<Integer> toDelete = new ArrayList<Integer> ();
            for ( final Entry<Integer, List<ToggleCallback>> entry : this.toggleCallbacks.entrySet () )
            {
                if ( entry.getValue ().size () == 0 )
                {
                    toDelete.add ( entry.getKey () );
                }
            }
            for ( final Integer integer : toDelete )
            {
                this.toggleCallbacks.remove ( integer );
            }
        }
    }

    public void start ()
    {
        this.executor.scheduleAtFixedRate ( this, 0, delay, TimeUnit.MILLISECONDS );
    }

    public void stop ()
    {
        this.executor.shutdownNow ();
        synchronized ( this.addRemoveLock )
        {
            this.toggleInfos.clear ();
            this.toggleCallbacks.clear ();
        }
    }

    public void run ()
    {
        final long c = this.counter.getAndAdd ( delay );
        for ( final int toggle : this.toggleInfos.keySet () )
        {
            if ( c % toggle == 0 )
            {
                final ToggleInfo i = this.toggleInfos.get ( toggle );
                final boolean isOn = i.toggle ();
                for ( final ToggleCallback bc : this.toggleCallbacks.get ( toggle ) )
                {
                    try
                    {
                        bc.toggle ( isOn );
                    }
                    catch ( final Exception e )
                    {
                        logger.warn ( "call of toggle action failed", e );
                    }
                }
            }
        }
    }
}
