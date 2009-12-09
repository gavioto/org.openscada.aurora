package org.openscada.utils.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

public class BoundedPriorityQueueSet<E> implements SortedSet<E>, BoundedQueue<E>
{
    private final SortedSet<E> internalSet;

    private final int capacity;

    public BoundedPriorityQueueSet ( int capacity )
    {
        this ( capacity, (Comparator<E>)null );
    }

    public BoundedPriorityQueueSet ( int capacity, Comparator<E> comparator )
    {
        if ( capacity < 1 )
            throw new IllegalArgumentException ();
        this.capacity = capacity;
        this.internalSet = new TreeSet<E> ( comparator );
    }

    public BoundedPriorityQueueSet ( int capacity, Collection<E> c )
    {
        this ( capacity, (Comparator<E>)null );
        shrinkToSize ();
    }

    public BoundedPriorityQueueSet ( int capacity, Comparator<E> comparator, Collection<E> c )
    {
        this ( capacity, comparator );
        shrinkToSize ();
    }

    private void shrinkToSize ()
    {
        while ( internalSet.size () > capacity )
        {
            remove ();
        }
    }

    public boolean add ( E e )
    {
        final boolean result = internalSet.add ( e );
        if ( !result )
        {
            return false;
        }
        shrinkToSize ();
        return internalSet.contains ( e );
    }

    public boolean addAll ( Collection<? extends E> c )
    {
        final boolean result = internalSet.addAll ( c );
        if ( !result )
        {
            return false;
        }
        shrinkToSize ();
        for ( E e : c )
        {
            if (internalSet.contains ( e )) {
                return true;
            }
        }
        return false;
    }

    public void clear ()
    {
        internalSet.clear ();
    }

    public Comparator<? super E> comparator ()
    {
        return internalSet.comparator ();
    }

    public boolean contains ( Object o )
    {
        return internalSet.contains ( o );
    }

    public boolean containsAll ( Collection<?> c )
    {
        return internalSet.containsAll ( c );
    }

    public boolean equals ( Object o )
    {
        return internalSet.equals ( o );
    }

    public E first ()
    {
        return internalSet.first ();
    }

    public int hashCode ()
    {
        return internalSet.hashCode ();
    }

    public SortedSet<E> headSet ( E toElement )
    {
        return internalSet.headSet ( toElement );
    }

    public boolean isEmpty ()
    {
        return internalSet.isEmpty ();
    }

    public Iterator<E> iterator ()
    {
        return internalSet.iterator ();
    }

    public E last ()
    {
        return internalSet.last ();
    }

    public boolean remove ( Object o )
    {
        return internalSet.remove ( o );
    }

    public boolean removeAll ( Collection<?> c )
    {
        return internalSet.removeAll ( c );
    }

    public boolean retainAll ( Collection<?> c )
    {
        return internalSet.retainAll ( c );
    }

    public int size ()
    {
        return internalSet.size ();
    }

    public SortedSet<E> subSet ( E fromElement, E toElement )
    {
        return internalSet.subSet ( fromElement, toElement );
    }

    public SortedSet<E> tailSet ( E fromElement )
    {
        return internalSet.tailSet ( fromElement );
    }

    public Object[] toArray ()
    {
        return internalSet.toArray ();
    }

    public <T> T[] toArray ( T[] a )
    {
        return internalSet.toArray ( a );
    }

    public E element ()
    {
        return internalSet.first ();
    }

    public E peek ()
    {
        try
        {
            return internalSet.first ();
        }
        catch ( NoSuchElementException e )
        {
            return null;
        }
    }

    public boolean offer ( E e )
    {
        return this.add ( e );
    }

    public E poll ()
    {
        try
        {
            return internalSet.last ();
        }
        catch ( NoSuchElementException e )
        {
            return null;
        }
    }

    public E remove ()
    {
        E result = internalSet.last ();
        internalSet.remove ( result );
        return result;
    }

    public int getCapacity ()
    {
        return capacity;
    }
}
