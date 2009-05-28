package org.openscada.utils.concurrent;

import java.util.concurrent.Future;

public interface FutureListener<T>
{
    public void complete ( Future<T> future );
}
