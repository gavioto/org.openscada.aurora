package org.openscada.utils.toggle;

public interface ToggleService
{
    public void addListener ( int interval, TogleCallback runnable );

    public void removeListener ( TogleCallback runnable );
}
