package org.openscada.ca;

public interface ConfigurationListener
{
    public void configurationUpdate ( Configuration[] addedOrChanged, String[] deleted );
}
