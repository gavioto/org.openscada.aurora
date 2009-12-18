package org.openscada.utils.osgi.pool;

public interface ObjectPool
{

    public abstract void addListener ( final String id, final ObjectPoolListener listener );

    public abstract void removeListener ( final String id, final ObjectPoolListener listener );

}