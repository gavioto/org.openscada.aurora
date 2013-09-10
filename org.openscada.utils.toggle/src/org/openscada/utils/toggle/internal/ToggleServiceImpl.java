/*******************************************************************************
 * Copyright (c) 2006, 2010 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
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

    @Override
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

    @Override
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

    @Override
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
