package org.openscada.ca;

public interface Factory
{
    public String getId ();

    public FactoryState getState ();

    public String getDescription ();
}
