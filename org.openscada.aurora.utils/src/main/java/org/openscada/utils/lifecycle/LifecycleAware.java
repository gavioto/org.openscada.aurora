package org.openscada.utils.lifecycle;

public interface LifecycleAware
{
    public void start () throws Exception;

    public void stop () throws Exception;
}
