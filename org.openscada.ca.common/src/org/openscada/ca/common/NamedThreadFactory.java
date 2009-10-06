package org.openscada.ca.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class NamedThreadFactory implements ThreadFactory
{

    private final AtomicLong counter = new AtomicLong ();

    private final String name;

    public NamedThreadFactory ( final String name )
    {
        this.name = name;
    }

    public Thread newThread ( final Runnable r )
    {
        return new Thread ( r, String.format ( "%s/%s", this.name, this.counter.incrementAndGet () ) );
    }
}