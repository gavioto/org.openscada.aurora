package org.openscada.ca;

public class ConfigurationEvent
{
    public static enum Type
    {
        CREATED,
        MODIFIED,
        REMOVED,
        STATE
    }

    private final Configuration configuration;

    private final Type type;

    public ConfigurationEvent ( final Type type, final Configuration configuration )
    {
        this.type = type;
        this.configuration = configuration;
    }

    public Configuration getConfiguration ()
    {
        return this.configuration;
    }

    public Type getType ()
    {
        return this.type;
    }
}
