package org.openscada.ca;

public class FactoryEvent
{

    public static enum Type
    {
        STATE,
        PURGED
    }

    private final String factoryId;

    private final Type type;

    public FactoryEvent ( final Type type, final String factoryId )
    {
        this.type = type;
        this.factoryId = factoryId;
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public Type getType ()
    {
        return this.type;
    }
}
