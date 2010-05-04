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

package org.openscada.ca;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.openscada.utils.lang.Immutable;

@Immutable
public class DiffEntry implements Serializable
{
    private static final long serialVersionUID = -3327628959921694038L;

    public static enum Operation
    {
        ADD,
        DELETE,
        UPDATE
    }

    private final String factoryId;

    private final String configurationId;

    private final Operation operation;

    private final Map<String, String> data;

    public DiffEntry ( final String factoryId, final String configurationId, final Operation operation, final Map<String, String> data )
    {
        super ();
        this.factoryId = factoryId;
        this.configurationId = configurationId;
        this.operation = operation;
        this.data = new HashMap<String, String> ( data );
    }

    public String getConfigurationId ()
    {
        return this.configurationId;
    }

    public Map<String, String> getData ()
    {
        return Collections.unmodifiableMap ( this.data );
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public Operation getOperation ()
    {
        return this.operation;
    }

    @Override
    public String toString ()
    {
        return String.format ( "%s/%s/%s", this.factoryId, this.configurationId, this.operation );
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( configurationId == null ) ? 0 : configurationId.hashCode () );
        result = prime * result + ( ( factoryId == null ) ? 0 : factoryId.hashCode () );
        result = prime * result + ( ( operation == null ) ? 0 : operation.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        DiffEntry other = (DiffEntry)obj;
        if ( configurationId == null )
        {
            if ( other.configurationId != null )
                return false;
        }
        else if ( !configurationId.equals ( other.configurationId ) )
            return false;
        if ( data == null )
        {
            if ( other.data != null )
                return false;
        }
        else if ( !data.equals ( other.data ) )
            return false;
        if ( factoryId == null )
        {
            if ( other.factoryId != null )
                return false;
        }
        else if ( !factoryId.equals ( other.factoryId ) )
            return false;
        if ( operation == null )
        {
            if ( other.operation != null )
                return false;
        }
        else if ( !operation.equals ( other.operation ) )
            return false;
        return true;
    }
}
