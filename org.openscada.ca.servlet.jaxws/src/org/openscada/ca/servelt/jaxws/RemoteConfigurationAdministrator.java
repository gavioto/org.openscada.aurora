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

package org.openscada.ca.servelt.jaxws;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.jws.WebService;

import org.openscada.ca.ConfigurationInformation;
import org.openscada.ca.FactoryInformation;

@WebService
public interface RemoteConfigurationAdministrator
{
    public abstract boolean hasService ();

    /**
     * Get factory information without content
     * @return the factories without a content
     */
    public abstract FactoryInformation[] getFactories ();

    public abstract FactoryInformation[] getCompleteConfiguration ();

    public abstract FactoryInformation getFactory ( String factoryId );

    public abstract ConfigurationInformation getConfiguration ( String factoryId, String configurationId );

    public abstract void purge ( final String factoryId, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void delete ( final String factoryId, final String[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void update ( final String factoryId, final ConfigurationInformation[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

    public abstract void create ( final String factoryId, final ConfigurationInformation[] configurations, final int timeout ) throws InterruptedException, ExecutionException, TimeoutException;

}