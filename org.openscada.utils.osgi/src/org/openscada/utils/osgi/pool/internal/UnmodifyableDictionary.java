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
