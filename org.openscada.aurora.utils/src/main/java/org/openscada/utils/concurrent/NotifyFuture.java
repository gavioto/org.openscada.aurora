package org.openscada.utils.concurrent;

import java.util.concurrent.Future;

public interface NotifyFuture<T> extends Future<T>
{
    public void addListener ( FutureListener<T> listener );

    public void removeListener ( FutureListener<T> listener );
}
