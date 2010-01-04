package org.openscada.utils.toggle;

public interface ToggleService
{
    public void addListener ( int interval, ToggleCallback runnable );

    public void removeListener ( ToggleCallback runnable );
}
