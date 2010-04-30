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

package org.openscada.hsdb.backend;

import org.openscada.hsdb.configuration.Configuration;

/**
 * This interface defines methods for accessing back end manager implementations.
 * @author Ludwig Straub
 */
public interface BackEndManagerFactory
{
    /**
     * This method returns all currently available back end manager instances.
     * @return all currently available back end manager instances
     */
    public abstract BackEndManager<?>[] getBackEndManagers ();

    /**
     * This method returns the back end manager that is reliable for the configuration with the specified id.
     * @param configuration configuration the requested back end manager is reliable for
     * @param createIfNotExists flag indicating whether a new back end manager object should be created if it not one already exists for the passed configuration
     * @return back end manager that is reliable for the configuration with the specified id
     */
    public abstract BackEndManager<?> getBackEndManager ( Configuration configuration, boolean createIfNotExists );

    /**
     * This method saves the passed configuration object.
     * @param configuration configuration object that has to be saved
     */
    public abstract void save ( Configuration configuration );

    /**
     * This method deletes the back end manager configuration.
     * @param configuration configuration that has to be deleted
     */
    public abstract void delete ( Configuration configuration );
}
