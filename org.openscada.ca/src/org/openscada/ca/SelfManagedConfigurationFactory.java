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

import java.util.Map;

import org.openscada.utils.concurrent.NotifyFuture;

public interface SelfManagedConfigurationFactory
{
    /**
     * Add a new configuration listener to the factory
     * <p>
     * If the listener is already registered the method has no effect.
     * </p>
     * <p>
     * The listener is added to the factory and will receive updates from now on.
     * Before the method return it will call the listener once with the already
     * known configuration element using an "add" notification.
     * </p> 
     * @param listener The new listener to add
     */
    public void addConfigurationListener ( ConfigurationListener listener );

    /**
     * Remove a listener from the factory
     * <p>
     * If the listener is not currently attache to the factory the call has no effect.
     * </p>
     * @param listener
     */
    public void removeConfigurationListener ( ConfigurationListener listener );

    public NotifyFuture<Configuration> update ( String configurationId, Map<String, String> properties, boolean fullSet );

    public NotifyFuture<Configuration> delete ( String configurationId );
}
