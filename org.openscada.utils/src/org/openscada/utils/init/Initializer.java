/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.utils.init;

/**
 * Initialization interface
 * 
 * @author Jens Reimann
 */
public interface Initializer
{
    /**
     * Perform initialization for a specific type
     * <p>
     * The idea is to join in initializing all stuff related to the provided
     * type. So the type can for example by the string
     * <q>emf</q> and then all initializers which want to join that
     * initialization phase simply perform their initialization when the method
     * is called with the string
     * <q>emf</q>.
     * </p>
     * 
     * @param type
     *            the type to initialize
     */
    public void initialize ( Object type );
}
