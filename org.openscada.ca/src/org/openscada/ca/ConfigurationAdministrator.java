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

import java.util.Map;
import java.util.concurrent.Future;

public interface ConfigurationAdministrator
{
    public static final String FACTORY_ID = "factoryId";

    /* modifiers */

    public Future<Configuration> createConfiguration ( String factoryId, String configurationId, Map<String, String> initialProperties );

    public Future<Configuration> updateConfiguration ( String factoryId, String configurationId, Map<String, String> newProperties, boolean fullSet );

    public Future<Configuration> deleteConfiguration ( String factoryId, String configurationId );

    public Future<Void> purgeFactory ( String factoryId );

    /* readers */

    public Factory getFactory ( String factoryId );

    public Factory[] getKnownFactories ();

    public Configuration[] getConfigurations ( String factoryId );

    public Configuration getConfiguration ( String factoryId, String configurationId );
}
