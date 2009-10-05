package org.openscada.ca;

public class FactoryNotFoundException extends Exception
{
    private static final long serialVersionUID = 5992543762315104988L;

    private final String factoryId;

    public FactoryNotFoundException ( final String factoryId )
    {
        this.factoryId = factoryId;
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }
}
