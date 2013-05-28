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

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process all initialization found using the {@link ServiceLoader} mechnism
 * 
 * @author Jens Reimann
 */
public final class ServiceLoaderProcessor
{

    private final static Logger logger = LoggerFactory.getLogger ( ServiceLoaderProcessor.class );

    private ServiceLoaderProcessor ()
    {
    }

    /**
     * Initialize a specific type
     * 
     * @param type
     *            the initializer specific type, see
     *            {@link Initializer#initialize(Object)}
     */
    public static void initialize ( final Object type )
    {
        initialize ( type, null );
    }

    /**
     * Initialize a specific type
     * 
     * @param type
     *            the initializer specific type, see
     *            {@link Initializer#initialize(Object)}
     * @param classloader
     *            a specific class loader to use
     */
    public static void initialize ( final Object type, final ClassLoader classloader )
    {
        logger.debug ( "Initializing: {}", type );

        final ServiceLoader<Initializer> loader = ServiceLoader.load ( Initializer.class, classloader );
        final Iterator<Initializer> i = loader.iterator ();
        while ( i.hasNext () )
        {
            final Initializer initializer = i.next ();
            logger.debug ( "Processing: {}", initializer );

            try
            {
                initializer.initialize ( type );
            }
            catch ( final Exception e )
            {
                logger.info ( "Failed to initialize", e );
            }
        }
    }
}
