package org.openscada.ca;

public interface StorageListener
{
    public void configurationUpdate ( ConfigurationData[] addedOrChanged, String[] deleted );
}
