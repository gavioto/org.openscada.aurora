/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2008-2010 inavare GmbH (http://inavare.com)
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

public class ConfigurationInformation
{
    private String factoryId;

    private String id;

    private ConfigurationState state;

    private Map<String, String> data;

    private String errorInformation;

    public ConfigurationInformation ()
    {
    }

    public ConfigurationInformation ( final String factoryId, final org.openscada.ca.Configuration configuration )
    {
        this.factoryId = factoryId;
        this.id = configuration.getId ();
        this.state = configuration.getState ();
        this.data = configuration.getData ();
        this.errorInformation = convertError ( configuration.getErrorInformation () );
    }

    public ConfigurationInformation ( final FactoryInformation factory, final org.openscada.ca.Configuration configuration )
    {
        this.factoryId = factory.getId ();
        this.id = configuration.getId ();
        this.state = configuration.getState ();
        this.data = configuration.getData ();
        this.errorInformation = convertError ( configuration.getErrorInformation () );
    }

    public void setData ( final Map<String, String> data )
    {
        this.data = data;
    }

    public Map<String, String> getData ()
    {
        return this.data;
    }

    public void setState ( final ConfigurationState state )
    {
        this.state = state;
    }

    public ConfigurationState getState ()
    {
        return this.state;
    }

    public void setFactoryId ( final String factoryId )
    {
        this.factoryId = factoryId;
    }

    public String getFactoryId ()
    {
        return this.factoryId;
    }

    public void setId ( final String id )
    {
        this.id = id;
    }

    public String getId ()
    {
        return this.id;
    }

    public void setErrorInformation ( final String errorInformation )
    {
        this.errorInformation = errorInformation;
    }

    public String getErrorInformation ()
    {
        return this.errorInformation;
    }

    private String convertError ( final Throwable errorInformation )
    {
        if ( errorInformation == null )
        {
            return null;
        }

        final StringWriter sw = new StringWriter ();
        final PrintWriter pw = new PrintWriter ( sw );

        errorInformation.printStackTrace ( pw );

        return sw.toString ();
    }
}
