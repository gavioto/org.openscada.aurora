package org.openscada.utils.collection;

import java.util.Queue;

public interface BoundedQueue<E> extends Queue<E>
{
    public int getCapacity ();
}
