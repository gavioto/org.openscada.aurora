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
import java.util.concurrent.Future;

public interface ConfigurationAdministrator
{
    public static final String FACTORY_ID = "factoryId";

    /* modifiers */

    public Future<Configuration> createConfiguration ( String factoryId, String configurationId, Map<String, String> initialProperties );

    public Future<Configuration> updateConfiguration ( String factoryId, String configurationId, Map<String, String> newProperties );

    public Future<Configuration> deleteConfiguration ( String factoryId, String configurationId );

    /* readers */

    public Factory getFactory ( String factoryId );

    public Factory[] getKnownFactories ();

    public Configuration[] getConfigurations ( String factoryId );

    public Configuration getConfiguration ( String factoryId, String configurationId );
}
