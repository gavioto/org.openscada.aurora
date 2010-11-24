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

package org.openscada.utils.propertyeditors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author jrose
 * 
 */
public class PropertyEditorRegistry
{

    private final ConcurrentMap<String, PropertyEditor> propertyEditors = new ConcurrentHashMap<String, PropertyEditor> ();

    public PropertyEditorRegistry ()
    {
        this ( false );
    }

    public PropertyEditorRegistry ( final boolean fillWithDefault )
    {
        if ( fillWithDefault )
        {
            registerCustomEditor ( Boolean.class, new BooleanEditor () );
            registerCustomEditor ( boolean.class, new BooleanEditor () );
            registerCustomEditor ( Byte.class, new ByteEditor () );
            registerCustomEditor ( byte.class, new ByteEditor () );
            registerCustomEditor ( Double.class, new DoubleEditor () );
            registerCustomEditor ( double.class, new DoubleEditor () );
            registerCustomEditor ( Float.class, new FloatEditor () );
            registerCustomEditor ( float.class, new FloatEditor () );
            registerCustomEditor ( Integer.class, new IntegerEditor () );
            registerCustomEditor ( int.class, new IntegerEditor () );
            registerCustomEditor ( Long.class, new LongEditor () );
            registerCustomEditor ( long.class, new LongEditor () );
            registerCustomEditor ( Short.class, new ShortEditor () );
            registerCustomEditor ( short.class, new ShortEditor () );

            registerCustomEditor ( String.class, new StringEditor () );
            registerCustomEditor ( Date.class, new DateEditor () );
            registerCustomEditor ( UUID.class, new UUIDEditor () );
            registerCustomEditor ( Number.class, new NumberEditor () );
        }
    }

    public Map<String, PropertyEditor> getPropertyEditors ()
    {
        return Collections.unmodifiableMap ( this.propertyEditors );
    }

    /**
     * @param requiredType
     * @param propertyPath
     * @return
     */
    public PropertyEditor findCustomEditor ( final Class<?> requiredType, final String propertyPath )
    {
        // first try to find exact match
        String key = requiredType.getCanonicalName () + ":" + propertyPath;
        PropertyEditor pe = this.propertyEditors.get ( key );
        // 2nd: try to find for class only
        if ( pe == null )
        {
            key = requiredType.getCanonicalName () + ":";
            pe = this.propertyEditors.get ( key );
        }
        // 3rd: try to get internal
        if ( pe == null )
        {
            pe = PropertyEditorManager.findEditor ( requiredType );
        }
        return pe;
    }

    /**
     * @param requiredType
     * @return
     */
    public PropertyEditor findCustomEditor ( final Class<?> requiredType )
    {
        return findCustomEditor ( requiredType, "" );
    }

    /**
     * @param requiredType
     * @param propertyEditor
     */
    public void registerCustomEditor ( final Class<?> requiredType, final PropertyEditor propertyEditor )
    {
        registerCustomEditor ( requiredType, "", propertyEditor );
    }

    /**
     * @param requiredType
     * @param propertyPath
     * @param propertyEditor
     */
    public void registerCustomEditor ( final Class<?> requiredType, final String propertyPath, final PropertyEditor propertyEditor )
    {
        final String key = requiredType.getCanonicalName () + ":" + propertyPath;
        this.propertyEditors.put ( key, propertyEditor );
    }
}
