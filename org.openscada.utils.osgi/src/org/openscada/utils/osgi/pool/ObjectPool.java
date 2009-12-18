package org.openscada.utils.osgi.pool;

public interface ObjectPool
{

    public static final String OBJECT_POOL_CLASS = "object.pool.class";

    public abstract void addListener ( final String id, final ObjectPoolListener listener );

    public abstract void removeListener ( final String id, final ObjectPoolListener listener );

    public abstract void addListener ( final ObjectPoolListener listener );

    public abstract void removeListener ( final ObjectPoolListener listener );

}