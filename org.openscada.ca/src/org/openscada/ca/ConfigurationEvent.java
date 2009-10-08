/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2009 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.ca;

public class ConfigurationEvent
{
    public static enum Type
    {
        CREATED,
        MODIFIED,
        REMOVED,
        STATE
    }

    private final Configuration configuration;

    private final Type type;

    private final ConfigurationState state;

    private final Throwable error;

    public ConfigurationEvent ( final Type type, final Configuration configuration, final ConfigurationState state, final Throwable error )
    {
        this.type = type;
        this.configuration = configuration;
        this.state = state;
        this.error = error;
    }

    public ConfigurationState getState ()
    {
        return this.state;
    }

    public Throwable getError ()
    {
        return this.error;
    }

    public Configuration getConfiguration ()
    {
        return this.configuration;
    }

    public Type getType ()
    {
        return this.type;
    }

    @Override
    public String toString ()
    {
        switch ( this.type )
        {
        case STATE:
            return String.format ( "%s -> %s / %s", this.configuration.getId (), this.type, this.state );
        default:
            return String.format ( "%s -> %s / %s", this.configuration.getId (), this.type, this.configuration );
        }

    }
}
