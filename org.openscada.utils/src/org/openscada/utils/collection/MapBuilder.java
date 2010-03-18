/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

import java.util.HashMap;
import java.util.Map;

import org.openscada.utils.lang.Pair;

/**
 * A map builder which can create a HashMap by chained calls.
 * @author Jens Reimann &lt;jens.reimann@inavare.net&gt;
 *
 * @param <Key> The key type
 * @param <Value> The value type
 */
public class MapBuilder<Key, Value>
{
    private final Map<Key, Value> map;

    /**
     * Create a new map builder with the provided map as input
     * @param map the content that should be used as initial content. The provided map will not be modified.
     */
    public MapBuilder ( final Map<Key, Value> map )
    {
        this.map = new HashMap<Key, Value> ( map );
    }

    public MapBuilder ()
    {
        this.map = new HashMap<Key, Value> ( 0 );
    }

    /**
     * Put a pair into the map held by the map builder
     * @param key The key
     * @param value The value
     * @return the current instance of the map builder
     */
    public final MapBuilder<Key, Value> put ( final Key key, final Value value )
    {
        this.map.put ( key, value );
        return this;
    }

    /**
     * Clean the map held by the map builder
     * @return the current instance of the map builder
     */
    public final MapBuilder<Key, Value> clear ()
    {
        this.map.clear ();
        return this;
    }

    /**
     * Get the map of the map builder.
     * @return The map
     */
    public final Map<Key, Value> getMap ()
    {
        return this.map;
    }

    /**
     * Return a new map the containing only the provided value pair
     * @param <Key> The key type
     * @param <Value> The value type
     * @param pair The pair to add
     * @return the new map containing the pair
     */
    public static <Key, Value> Map<Key, Value> toMap ( final Pair<Key, Value> pair )
    {
        return toMap ( null, pair );
    }

    /**
     * Return a new map containing the provided pair, or add the pair to an already existing
     * map.
     * @param <Key> The key type
     * @param <Value> The value type
     * @param map The map to which the pair should be added (may be <code>null</code>)
     * @param pair The pair to add (may <em>not</em> be <code>null</code>)
     * @return The (new) map.
     */
    public static <Key, Value> Map<Key, Value> toMap ( Map<Key, Value> map, final Pair<Key, Value> pair )
    {
        if ( map == null )
        {
            map = new HashMap<Key, Value> ();
        }

        map.put ( pair.first, pair.second );

        return map;
    }
}
