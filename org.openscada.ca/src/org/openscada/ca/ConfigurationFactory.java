/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import java.security.Principal;
import java.util.Map;

public interface ConfigurationFactory
{
    /**
     * receive configuration update
     * @param principal the principal that performed the change, may be <code>null</code>
     * @param configurationId the configuration object to change
     * @param properties the new properties
     * @throws Exception can be thrown if anything goes wrong changing the configuration
     */
    public void update ( Principal principal, String configurationId, Map<String, String> properties ) throws Exception;

    /**
     * receive configuration delete request
     * @param principal the principal that performed the change, may be <code>null</code>
     * @param configurationId the configuration object to change
     * @throws Exception can be thrown if anything goes wrong changing the configuration
     */
    public void delete ( Principal principal, String configurationId ) throws Exception;
}
