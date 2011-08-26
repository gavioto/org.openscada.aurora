/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.utils.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class BoundedPriorityQueueSet<E> implements SortedSet<E>, BoundedQueue<E>
{
    private final SortedSet<E> internalSet;

    private final int capacity;

    public BoundedPriorityQueueSet ( final int capacity )
    {
        this ( capacity, (Comparator<E>)null );
    }

    public BoundedPriorityQueueSet ( final int capacity, final Comparator<E> comparator )
    {
        if ( capacity < 1 )
        {
            throw new IllegalArgumentException ();
        }
        this.capacity = capacity;
        this.internalSet = new ConcurrentSkipListSet<E> ( comparator );
    }

    public BoundedPriorityQueueSet ( final int capacity, final Collection<E> c )
    {
        this ( capacity, (Comparator<E>)null );
        shrinkToSize ();
    }

    public BoundedPriorityQueueSet ( final int capacity, final Comparator<E> comparator, final Collection<E> c )
    {
        this ( capacity, comparator );
        shrinkToSize ();
    }

    private void shrinkToSize ()
    {
        while ( this.internalSet.size () > this.capacity )
        {
            remove ();
        }
    }

    public boolean add ( final E e )
    {
        final boolean result = this.internalSet.add ( e );
        if ( !result )
        {
            return false;
        }
        shrinkToSize ();
        return this.internalSet.contains ( e );
    }

    public boolean addAll ( final Collection<? extends E> c )
    {
        final boolean result = this.internalSet.addAll ( c );
        if ( !result )
        {
            return false;
        }
        shrinkToSize ();
        for ( final E e : c )
        {
            if ( this.internalSet.contains ( e ) )
            {
                return true;
            }
        }
        return false;
    }

    public void clear ()
    {
        this.internalSet.clear ();
    }

    public Comparator<? super E> comparator ()
    {
        return this.internalSet.comparator ();
    }

    public boolean contains ( final Object o )
    {
        return this.internalSet.contains ( o );
    }

    public boolean containsAll ( final Collection<?> c )
    {
        return this.internalSet.containsAll ( c );
    }

    public boolean equals ( final Object o )
    {
        return this.internalSet.equals ( o );
    }

    public E first ()
    {
        return this.internalSet.first ();
    }

    public int hashCode ()
    {
        return this.internalSet.hashCode ();
    }

    public SortedSet<E> headSet ( final E toElement )
    {
        return this.internalSet.headSet ( toElement );
    }

    public boolean isEmpty ()
    {
        return this.internalSet.isEmpty ();
    }

    public Iterator<E> iterator ()
    {
        return this.internalSet.iterator ();
    }

    public E last ()
    {
        return this.internalSet.last ();
    }

    public boolean remove ( final Object o )
    {
        return this.internalSet.remove ( o );
    }

    public boolean removeAll ( final Collection<?> c )
    {
        return this.internalSet.removeAll ( c );
    }

    public boolean retainAll ( final Collection<?> c )
    {
        return this.internalSet.retainAll ( c );
    }

    public int size ()
    {
        return this.internalSet.size ();
    }

    public SortedSet<E> subSet ( final E fromElement, final E toElement )
    {
        return this.internalSet.subSet ( fromElement, toElement );
    }

    public SortedSet<E> tailSet ( final E fromElement )
    {
        return this.internalSet.tailSet ( fromElement );
    }

    public Object[] toArray ()
    {
        return this.internalSet.toArray ();
    }

    public <T> T[] toArray ( final T[] a )
    {
        return this.internalSet.toArray ( a );
    }

    public E element ()
    {
        return this.internalSet.first ();
    }

    public E peek ()
    {
        try
        {
            return this.internalSet.first ();
        }
        catch ( final NoSuchElementException e )
        {
            return null;
        }
    }

    public boolean offer ( final E e )
    {
        return this.add ( e );
    }

    public E poll ()
    {
        try
        {
            return this.internalSet.last ();
        }
        catch ( final NoSuchElementException e )
        {
            return null;
        }
    }

    public E remove ()
    {
        final E result = this.internalSet.last ();
        this.internalSet.remove ( result );
        return result;
    }

    public int getCapacity ()
    {
        return this.capacity;
    }
}
