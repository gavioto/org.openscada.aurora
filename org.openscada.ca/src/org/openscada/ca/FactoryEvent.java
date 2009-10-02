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

    private final FactoryState state;

    public FactoryEvent ( final Type type, final Factory factory, final FactoryState state )
    {
        this.type = type;
        this.factory = factory;
        this.state = state;
    }

    public FactoryState getState ()
    {
        return this.state;
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
        switch ( this.type )
        {
        case STATE:
            return String.format ( "%s -> %s : %s", this.factory.getId (), this.type, this.state );
        default:
            return String.format ( "%s -> %s", this.factory.getId (), this.type );
        }

    }
}
