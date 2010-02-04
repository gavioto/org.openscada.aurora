/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.statuscodes;

import org.openscada.utils.lang.Immutable;

@Immutable
public class StatusCode
{
    private final String moduleCode;

    private final String subModuleCode;

    private final long code;

    private final SeverityLevel severity;

    public StatusCode ( final String module, final String subModule, final long code, final SeverityLevel severity )
    {
        this.moduleCode = module;
        this.subModuleCode = subModule;
        this.code = code;
        this.severity = severity;
    }

    public String getModuleCode ()
    {
        return this.moduleCode;
    }

    public String getSubModuleCode ()
    {
        return this.subModuleCode;
    }

    public long getNumberCode ()
    {
        return this.code;
    }

    public SeverityLevel getSeverity ()
    {
        return this.severity;
    }

    public String toString ()
    {
        final String statusCode = String.format ( "%s-%s-%08X", this.moduleCode, this.subModuleCode, this.code );
        return statusCode;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) ( this.code ^ this.code >>> 32 );
        result = prime * result + ( this.moduleCode == null ? 0 : this.moduleCode.hashCode () );
        result = prime * result + ( this.subModuleCode == null ? 0 : this.subModuleCode.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass () != obj.getClass () )
        {
            return false;
        }
        final StatusCode other = (StatusCode)obj;
        if ( this.code != other.code )
        {
            return false;
        }
        if ( this.moduleCode == null )
        {
            if ( other.moduleCode != null )
            {
                return false;
            }
        }
        else if ( !this.moduleCode.equals ( other.moduleCode ) )
        {
            return false;
        }
        if ( this.subModuleCode == null )
        {
            if ( other.subModuleCode != null )
            {
                return false;
            }
        }
        else if ( !this.subModuleCode.equals ( other.subModuleCode ) )
        {
            return false;
        }
        return true;
    }

}
