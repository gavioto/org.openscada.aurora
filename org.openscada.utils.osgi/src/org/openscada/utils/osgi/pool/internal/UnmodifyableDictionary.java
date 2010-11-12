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

package org.openscada.utils.osgi.pool.internal;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class UnmodifyableDictionary<K, V> extends Hashtable<K, V>
{
    private static final long serialVersionUID = -2754201183982277308L;

    private final Dictionary<K, V> dictionary;

    public UnmodifyableDictionary ( final Dictionary<K, V> dictionary )
    {
        this.dictionary = dictionary;
    }

    public Enumeration<V> elements ()
    {
        return this.dictionary.elements ();
    }

    public boolean equals ( final Object o )
    {
        return this.dictionary.equals ( o );
    }

    public V get ( final Object key )
    {
        return this.dictionary.get ( key );
    }

    public int hashCode ()
    {
        return this.dictionary.hashCode ();
    }

    public boolean isEmpty ()
    {
        return this.dictionary.isEmpty ();
    }

    public Enumeration<K> keys ()
    {
        return this.dictionary.keys ();
    }

    public V put ( final K key, final V value )
    {
        throw new UnsupportedOperationException ();
    }

    public V remove ( final Object key )
    {
        throw new UnsupportedOperationException ();
    }

    public int size ()
    {
        return this.dictionary.size ();
    }

    public String toString ()
    {
        return this.dictionary.toString ();
    }

}
