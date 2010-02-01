package org.openscada.utils.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class NamedThreadFactory implements ThreadFactory
{
    private final AtomicLong counter;

    private final String name;

    public NamedThreadFactory ( final String name )
    {
        this.counter = new AtomicLong ();
        this.name = name;
    }

    public Thread newThread ( final Runnable r )
    {
        return new Thread ( r, String.format ( "%s/%s", this.name, this.counter.incrementAndGet () ) );
    }
}