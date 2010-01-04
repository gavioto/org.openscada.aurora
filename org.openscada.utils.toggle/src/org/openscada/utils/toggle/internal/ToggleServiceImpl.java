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

import org.openscada.utils.toggle.ToggleError;
import org.openscada.utils.toggle.ToggleService;
import org.openscada.utils.toggle.TogleCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToggleServiceImpl implements ToggleService, Runnable
{
    private static final Logger logger = LoggerFactory.getLogger ( ToggleServiceImpl.class );

    private static final int delay = 100;

    private final ConcurrentMap<Integer, ToggleInfo> toggleInfos = new ConcurrentHashMap<Integer, ToggleInfo> ();

    private final ConcurrentMap<Integer, List<TogleCallback>> toggleCallbacks = new ConcurrentHashMap<Integer, List<TogleCallback>> ();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor ();

    private final AtomicLong counter = new AtomicLong ( 0 );

    private final Object addRemoveLock = new Object ();

    public void addListener ( final int interval, final TogleCallback bc ) throws ToggleError
    {
        synchronized ( addRemoveLock )
        {
            if ( !toggleInfos.containsKey ( interval ) )
            {
                toggleInfos.put ( interval, new ToggleInfo ( interval ) );
            }
            if ( !toggleCallbacks.containsKey ( interval ) )
            {
                toggleCallbacks.put ( interval, new CopyOnWriteArrayList<TogleCallback> () );
            }
            List<TogleCallback> handlers = toggleCallbacks.get ( interval );
            handlers.add ( bc );
        }
    }

    public void removeListener ( final TogleCallback bc )
    {
        synchronized ( addRemoveLock )
        {
            for ( List<TogleCallback> bcs : toggleCallbacks.values () )
            {
                bcs.remove ( bc );
            }
            List<Integer> toDelete = new ArrayList<Integer> ();
            for ( Entry<Integer, List<TogleCallback>> entry : toggleCallbacks.entrySet () )
            {
                if ( entry.getValue ().size () == 0 )
                {
                    toDelete.add ( entry.getKey () );
                }
            }
            for ( Integer integer : toDelete )
            {
                toggleCallbacks.remove ( integer );
            }
        }
    }

    public void start ()
    {
        executor.scheduleAtFixedRate ( this, 0, delay, TimeUnit.MILLISECONDS );
    }

    public void stop ()
    {
        executor.shutdownNow ();
        synchronized ( addRemoveLock )
        {
            toggleInfos.clear ();
            toggleCallbacks.clear ();
        }
    }

    public void run ()
    {
        final long c = counter.getAndAdd ( delay );
        for ( final int toggle : toggleInfos.keySet () )
        {
            if ( c % toggle == 0 )
            {
                final ToggleInfo i = toggleInfos.get ( toggle );
                final boolean isOn = i.toggle ();
                for ( TogleCallback bc : toggleCallbacks.get ( toggle ) )
                {
                    try
                    {
                        bc.toggle ( isOn );
                    }
                    catch ( Exception e )
                    {
                        logger.warn ( "call of toggle action failed", e );
                    }
                }
            }
        }
    }
}
