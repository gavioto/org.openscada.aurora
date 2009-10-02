package org.openscada.ca;

public interface StorageListener
{
    public void configurationUpdate ( Configuration[] addedOrChanged, String[] deleted );
}
