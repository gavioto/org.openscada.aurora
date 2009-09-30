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

    @Override
    public String toString ()
    {
        return String.format ( "%s -> %s / %s", this.configuration.getId (), this.type, this.configuration );
    }
}
