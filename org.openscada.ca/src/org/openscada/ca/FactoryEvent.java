package org.openscada.ca;

import org.openscada.utils.lang.Immutable;

@Immutable
public class FactoryEvent
{

    public static enum Type
    {
        STATE,
        ADDED,
        REMOVED,
    }

    private final Factory factory;

    private final Type type;

    public FactoryEvent ( final Type type, final Factory factory )
    {
        this.type = type;
        this.factory = factory;
    }

    public Factory getFactory ()
    {
        return this.factory;
    }

    public Type getType ()
    {
        return this.type;
    }

    @Override
    public String toString ()
    {
        return String.format ( "%s -> %s", this.factory.getId (), this.type );
    }
}
